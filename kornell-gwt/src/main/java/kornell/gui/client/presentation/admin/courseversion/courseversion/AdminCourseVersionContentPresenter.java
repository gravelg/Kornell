package kornell.gui.client.presentation.admin.courseversion.courseversion;

import java.util.logging.Logger;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.presentation.classroom.ClassroomPlace;

public class AdminCourseVersionContentPresenter implements AdminCourseVersionContentView.Presenter {
    Logger logger = Logger.getLogger(AdminCourseVersionContentPresenter.class.getName());
    private AdminCourseVersionContentView view;
    private KornellSession session;
    private PlaceController placeController;
    Place defaultPlace;
    private ViewFactory viewFactory;
    private AdminCourseVersionPresenter adminCourseVersionPresenter;

    public AdminCourseVersionContentPresenter(KornellSession session, PlaceController placeController, EventBus bus,
            Place defaultPlace, ViewFactory viewFactory) {
        this.session = session;
        this.placeController = placeController;
        this.defaultPlace = defaultPlace;
        this.viewFactory = viewFactory;
    }

    @Override
    public void init(AdminCourseVersionPresenter adminCourseVersionPresenter) {
        this.adminCourseVersionPresenter = adminCourseVersionPresenter;
        if (session.hasPublishingRole()) {
            view = viewFactory.getAdminCourseVersionContentView();
            view.setPresenter(this);
            view.init();
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

    @Override
    public CourseVersion getCourseVersion() {
        return adminCourseVersionPresenter.getCourseVersion();
    }

    @Override
    public void setCourseVersion(CourseVersion courseVersion) {
        adminCourseVersionPresenter.setCourseVersion(courseVersion);
    }

    @Override
    public Course getCourse() {
        return adminCourseVersionPresenter.getCourse();
    }

    @Override
    public void goToSandboxClass() {
        session.courseVersion(this.getCourseVersion().getUUID()).sandboxEnrollment(new Callback<String>() {
            @Override
            public void ok(String enrollmentUUID) {
                placeController.goTo(new ClassroomPlace(enrollmentUUID));
            }
        });
    }
}