package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import java.util.List;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.TOFactory;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.common.ConfirmModalView;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPlace;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellNotification;

public class AdminCourseClassesPresenter implements AdminCourseClassesView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseClassesPresenter.class.getName());
	private AdminCourseClassesView view;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private CourseClassesTO courseClassesTO;
	private String pageSize;
	private String pageNumber;
	private String searchTerm;
	private String asc;
	private String orderBy;
	private EventBus bus;
	private boolean blockActions;
	private ConfirmModalView confirmModal;

	public AdminCourseClassesPresenter(KornellSession session, EventBus bus,
			PlaceController placeController, Place defaultPlace,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		this.confirmModal = viewFactory.getConfirmModalView();
		formHelper = new FormHelper();

		init();
	}

	private void init() {
		if (RoleCategory.hasRole(session.getCurrentUser().getRoles(), RoleType.courseClassAdmin)
				|| RoleCategory.hasRole(session.getCurrentUser().getRoles(), RoleType.observer)
				|| RoleCategory.hasRole(session.getCurrentUser().getRoles(), RoleType.tutor)
				|| session.isInstitutionAdmin()) {
			String orderByProperty = ClientProperties.get(getClientPropertyName("orderBy"));
			String ascProperty = ClientProperties.get(getClientPropertyName("asc"));
			String pageSizeProperty = ClientProperties.get(getClientPropertyName("pageSize"));
			String pageNumberProperty = ClientProperties.get(getClientPropertyName("pageNumber"));
			this.orderBy = orderByProperty != null ? orderByProperty : "c.name";
			this.asc = ascProperty != null ? ascProperty : "true";
			this.pageSize = pageSizeProperty != null ? pageSizeProperty : "20";
			this.pageNumber = pageNumberProperty != null ? pageNumberProperty : "1";
			this.searchTerm = "";
			
			view = getView();
			view.setPresenter(this);			
			String selectedCourseClass = "";
			updateCourseClass(selectedCourseClass);
      
		} else {
			logger.warning("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	@Override
	public void updateCourseClass(final String courseClassUUID) {
		bus.fireEvent(new ShowPacifierEvent(true));
		session.courseClasses().getAdministratedCourseClassesTOPaged(pageSize, pageNumber, searchTerm, orderBy, asc,
				new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO to) {
				courseClassesTO = to;
				view.setCourseClasses(courseClassesTO.getCourseClasses());
				bus.fireEvent(new ShowPacifierEvent(false));
				if(courseClassesTO.getCourseClasses().size() != 0){
					for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
						if (courseClassUUID == null || courseClassTO.getCourseClass().getUUID().equals(courseClassUUID)) {
							return;
						}
					}
				}

				ClientProperties.set(getClientPropertyName("orderBy"), getOrderBy());
				ClientProperties.set(getClientPropertyName("asc"), getAsc());
				ClientProperties.set(getClientPropertyName("pageSize"), getPageSize());
				ClientProperties.set(getClientPropertyName("pageNumber"), getPageNumber());
			}
		});
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminCourseClassesView getView() {
		return viewFactory.getAdminCourseClassesView();
	}

	@Override
	public String getPageSize() {
		return pageSize;
	}

	@Override
	public void setPageSize(String pageSize) {
		this.pageSize = pageSize;
	}

	@Override
	public String getPageNumber() {
		return pageNumber;
	}

	@Override
	public void setPageNumber(String pageNumber) {
		this.pageNumber = pageNumber;
	}

	@Override
	public String getSearchTerm() {
		return searchTerm;
	}

	@Override
	public void setSearchTerm(String searchTerm) {
		this.searchTerm = searchTerm;	
	}

	@Override
	public void updateData() {
    	updateCourseClass("");
	}

	@Override
	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	@Override
	public void setAsc(String asc) {
		this.asc = asc;
	}

	@Override
	public String getOrderBy() {
		return orderBy;
	}

	@Override
	public String getAsc() {
		return asc;
	}

	@Override
	public String getClientPropertyName(String property){
		return session.getAdminHomePropertyPrefix() +
				"courseClasses" + ClientProperties.SEPARATOR +
				property;
	}

	@Override
	public int getTotalRowCount() {
		return StringUtils.isSome(getSearchTerm()) ? courseClassesTO.getCount() : courseClassesTO.getSearchCount();
	}

	@Override
	public int getCount() {
		return courseClassesTO.getCount();
	}

	@Override
	public List<CourseClassTO> getRowData() {
		return courseClassesTO.getCourseClasses();
	}

	@Override
	public void deleteCourseClass(CourseClassTO courseClassTO) {
		if(!blockActions){
			blockActions = true;

			confirmModal.showModal(
					"Tem certeza que deseja excluir a turma \"" + courseClassTO.getCourseClass().getName() + "\"?", 
					new com.google.gwt.core.client.Callback<Void, Void>() {
				@Override
				public void onSuccess(Void result) {
					bus.fireEvent(new ShowPacifierEvent(true));
					session.courseClass(courseClassTO.getCourseClass().getUUID()).delete(new Callback<CourseClass>() {	
						@Override
						public void ok(CourseClass to) {
							blockActions = false;
							bus.fireEvent(new ShowPacifierEvent(false));
							KornellNotification.show("Turma excluída com sucesso.");
							updateData();
						}
						
						@Override
						public void internalServerError(KornellErrorTO error){
							blockActions = false;
							bus.fireEvent(new ShowPacifierEvent(false));
							KornellNotification.show("Erro ao tentar excluir a turma.", AlertType.ERROR);
						}
					});
				}
				@Override
				public void onFailure(Void reason) {
					blockActions = false;
				}
			});
		}

	}

	@Override
	public void duplicateCourseClass(CourseClassTO courseClassTO) {
		if(!blockActions){
			blockActions = false;

			confirmModal.showModal(
					"Tem certeza que deseja duplicar a turma \"" + courseClassTO.getCourseClass().getName() + "\"?", 
					new com.google.gwt.core.client.Callback<Void, Void>() {
				@Override
				public void onSuccess(Void result) {
					bus.fireEvent(new ShowPacifierEvent(true));
					session.courseClass(courseClassTO.getCourseClass().getUUID()).copy(new Callback<CourseClass>() {	
						@Override
						public void ok(CourseClass courseClass) {
							blockActions = false;
							bus.fireEvent(new ShowPacifierEvent(false));
							KornellNotification.show("Turma duplicada com sucesso.");
							placeController.goTo(new AdminCourseClassPlace(courseClass.getUUID()));
						}
						
						@Override
						public void internalServerError(KornellErrorTO error){
							blockActions = false;
							bus.fireEvent(new ShowPacifierEvent(false));
							KornellNotification.show("Erro ao tentar duplicar a turma.", AlertType.ERROR);
						}
						
						@Override
						public void conflict(KornellErrorTO error){
							blockActions = false;
							bus.fireEvent(new ShowPacifierEvent(false));
							KornellNotification.show("Erro ao tentar duplicar a turma. Verifique se já existe uma turma com o nome \"" + courseClassTO.getCourseClass().getName() + "\" (2).", AlertType.ERROR, 5000);
						}
					});
				}
				@Override
				public void onFailure(Void reason) {
					blockActions = false;
				}
			});
		}

	}
}