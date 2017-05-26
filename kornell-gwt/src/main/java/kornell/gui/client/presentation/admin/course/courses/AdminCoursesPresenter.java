package kornell.gui.client.presentation.admin.course.courses;

import java.util.List;
import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.TOFactory;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.common.ConfirmModalView;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePlace;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellNotification;

public class AdminCoursesPresenter implements AdminCoursesView.Presenter {
	Logger logger = Logger.getLogger(AdminCoursesPresenter.class.getName());
	private AdminCoursesView view;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private CoursesTO coursesTO;
	private String pageSize;
	private String pageNumber;
	private String searchTerm;
	private String asc;
	private String orderBy;
	private EventBus bus;
	private boolean blockActions;
	private ConfirmModalView confirmModal;


	public AdminCoursesPresenter(KornellSession session, EventBus bus,
			PlaceController placeController,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
		this.placeController = placeController;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		this.confirmModal = viewFactory.getConfirmModalView();
		formHelper = new FormHelper();
		init();
	}

	private void init() {
		if (session.isInstitutionAdmin()) {
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
			bus.fireEvent(new ShowPacifierEvent(true));
			getCourses();
      
		} else {
			logger.warning("Hey, only admins are allowed to see this! "
					+ this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	private void getCourses() {
		bus.fireEvent(new ShowPacifierEvent(true));
		session.courses().get(true, pageSize, pageNumber, searchTerm, orderBy, asc, new Callback<CoursesTO>() {
  			@Override
  			public void ok(CoursesTO to) {
  				coursesTO = to;
  				view.setCourses(coursesTO.getCourses());
  				bus.fireEvent(new ShowPacifierEvent(false));

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

	private AdminCoursesView getView() {
		return viewFactory.getAdminCoursesView();
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
		getCourses();
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
				"courses" + ClientProperties.SEPARATOR +
				property;
	}

	@Override
	public int getTotalRowCount() {
		return StringUtils.isSome(getSearchTerm()) ? coursesTO.getCount() : coursesTO.getSearchCount();
	}

	@Override
	public int getCount() {
		return coursesTO.getCount();
	}
	
	@Override
	public List<CourseTO> getRowData(){
		return coursesTO.getCourses();
	}

	@Override
	public void deleteCourse(CourseTO courseTO) {
		if(!blockActions){
			blockActions = true;

			confirmModal.showModal(
					"Tem certeza que deseja excluir o curso \"" + courseTO.getCourse().getName() + "\"?", 
					new com.google.gwt.core.client.Callback<Void, Void>() {
				@Override
				public void onSuccess(Void result) {
					bus.fireEvent(new ShowPacifierEvent(true));
					session.course(courseTO.getCourse().getUUID()).delete(new Callback<Course>() {	
						@Override
						public void ok(Course to) {
							blockActions = false;
							bus.fireEvent(new ShowPacifierEvent(false));
							KornellNotification.show("Curso excluído com sucesso.");
							updateData();
						}
						
						@Override
						public void internalServerError(KornellErrorTO error){
							blockActions = false;
							bus.fireEvent(new ShowPacifierEvent(false));
							KornellNotification.show("Erro ao tentar excluir o curso.", AlertType.ERROR);
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
	public void duplicateCourse(CourseTO courseTO) {
		if(!blockActions){
			blockActions = true;

			confirmModal.showModal(
					"Tem certeza que deseja duplicar o curso \"" + courseTO.getCourse().getName() + "\"?", 
					new com.google.gwt.core.client.Callback<Void, Void>() {
				@Override
				public void onSuccess(Void result) {

					bus.fireEvent(new ShowPacifierEvent(true));
					session.course(courseTO.getCourse().getUUID()).copy(new Callback<Course>() {	
						@Override
						public void ok(Course course) {
							blockActions = false;
							bus.fireEvent(new ShowPacifierEvent(false));
							KornellNotification.show("Curso duplicado com sucesso.");
							placeController.goTo(new AdminCoursePlace(course.getUUID()));
						}
						
						@Override
						public void internalServerError(KornellErrorTO error){
							blockActions = false;
							bus.fireEvent(new ShowPacifierEvent(false));
							KornellNotification.show("Erro ao tentar duplicar o curso.", AlertType.ERROR);
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