package kornell.gui.client.presentation.admin.courseversion.courseversion;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.ViewFactory;

public class AdminCourseVersionContentPresenter implements AdminCourseVersionContentView.Presenter {
	Logger logger = Logger.getLogger(AdminCourseVersionContentPresenter.class.getName());
	private AdminCourseVersionContentView view;
	private KornellSession session;
	private PlaceController placeController;
	Place defaultPlace;
	private ViewFactory viewFactory;

	public AdminCourseVersionContentPresenter(KornellSession session, PlaceController placeController, EventBus bus,
			Place defaultPlace, ViewFactory viewFactory) {
		this.session = session;
		this.placeController = placeController;
		this.defaultPlace = defaultPlace;
		this.viewFactory = viewFactory;
	}

	@Override
	public void init(CourseVersion courseVersion, ContentSpec contentSpec) {
		if (session.isInstitutionAdmin()) {
			view = viewFactory.getAdminCourseVersionContentView();
			view.setPresenter(this);
			
			boolean isWizardVersion = ContentSpec.WIZARD.equals(contentSpec);
			view.init(courseVersion, isWizardVersion);
			
		} else {
			logger.warning("Hey, only admins are allowed to see this! " + this.getClass().getName());
			placeController.goTo(defaultPlace);
		}
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	@Override
	public AdminCourseVersionContentView getView() {
		return view;
	}
}