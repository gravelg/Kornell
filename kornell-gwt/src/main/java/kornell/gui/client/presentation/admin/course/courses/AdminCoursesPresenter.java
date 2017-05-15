package kornell.gui.client.presentation.admin.course.courses;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.CoursesTO;
import kornell.core.to.TOFactory;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.util.forms.FormHelper;

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
	private String pageSize = "20";
	private String pageNumber = "1";
	private String searchTerm = "";
	private boolean asc = true;
	private String orderBy = "c.title";
	private EventBus bus;


	public AdminCoursesPresenter(KornellSession session, EventBus bus,
			PlaceController placeController,
			TOFactory toFactory, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
		this.placeController = placeController;
		this.toFactory = toFactory;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		init();
	}

	private void init() {
		if (session.isInstitutionAdmin()) {
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
		session.courses().get(true, pageSize, pageNumber, searchTerm, orderBy, asc, new Callback<CoursesTO>() {
  			@Override
  			public void ok(CoursesTO to) {
  				coursesTO = to;
  				view.setCourses(coursesTO.getCourses(), to.getCount(), to.getSearchCount());
  				bus.fireEvent(new ShowPacifierEvent(false));
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
	public void setAsc(boolean asc) {
		this.asc = asc;
	}

	@Override
	public String getOrderBy() {
		return orderBy;
	}

	@Override
	public boolean getAsc() {
		return asc;
	}
	
}