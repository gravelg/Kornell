package kornell.gui.client.presentation.admin.courseclass.courseclasses;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.RoleCategory;
import kornell.core.entity.RoleType;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.forms.FormHelper;

public class AdminCourseClassesPresenter implements AdminCourseClassesView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseClassesPresenter.class.getName());
	private AdminCourseClassesView view;
	FormHelper formHelper;
	private KornellSession session;
	private PlaceController placeController;
	private Place defaultPlace;
	TOFactory toFactory;
	private ViewFactory viewFactory;
	private EventBus bus;
	private CourseClassesTO courseClassesTO;
	private String pageSize = "20";
	private String pageNumber = "1";
	private String searchTerm = "";
	private String asc;
	private String orderBy;

	public AdminCourseClassesPresenter(KornellSession session, EventBus bus,
			PlaceController placeController, Place defaultPlace,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
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
			this.orderBy = orderByProperty != null ? orderByProperty : "cc.name";
			this.asc = ascProperty != null ? ascProperty : "true";
			
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
		view.setCourseClasses(null, 0, 0);
		session.courseClasses().getAdministratedCourseClassesTOPaged(pageSize, pageNumber, searchTerm, orderBy, asc,
				new Callback<CourseClassesTO>() {
			@Override
			public void ok(CourseClassesTO to) {
				courseClassesTO = to;
				view.setCourseClasses(courseClassesTO.getCourseClasses(), to.getCount(), to.getSearchCount());
				bus.fireEvent(new ShowPacifierEvent(false));
				if(courseClassesTO.getCourseClasses().size() == 0){
				} else {
					for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
						if (courseClassUUID == null || courseClassTO.getCourseClass().getUUID().equals(courseClassUUID)) {
							return;
						}
					}
				}
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
}