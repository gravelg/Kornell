package kornell.gui.client.presentation.admin.courseversion.courseversion;

import java.util.logging.Logger;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.CourseVersion;
import kornell.core.error.KornellErrorTO;
import kornell.gui.client.KornellConstantsHelper;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.mvp.PlaceUtils;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsPlace;
import kornell.gui.client.util.view.KornellNotification;

public class AdminCourseVersionPresenter implements AdminCourseVersionView.Presenter {
    Logger logger = Logger.getLogger(AdminCourseVersionPresenter.class.getName());
    private AdminCourseVersionView view;
    private KornellSession session;
    private PlaceController placeController;
    private EventBus bus;
    Place defaultPlace;
    private ViewFactory viewFactory;
    private AdminCourseVersionContentPresenter courseVersionContentPresenter;

    public AdminCourseVersionPresenter(KornellSession session, PlaceController placeController, EventBus bus,
            Place defaultPlace, ViewFactory viewFactory) {
        this.session = session;
        this.placeController = placeController;
        this.bus = bus;
        this.defaultPlace = defaultPlace;
        this.viewFactory = viewFactory;
        init();
    }

    private void init() {
        if (session.isInstitutionAdmin()) {
            view = viewFactory.getAdminCourseVersionView();
            if (view.getPresenter() == null) {
                view.setPresenter(this);
            }
        } else {
            logger.warning("Hey, only admins are allowed to see this! " + this.getClass().getName());
            placeController.goTo(defaultPlace);
        }
    }

    public void eventListener(String message) {
        KornellNotification.show("Received a message from child: " + message);
        sendIFrameMessage("wow");
    }

    private native void sendIFrameMessage(String message) /*-{
        var domain = $wnd.location.protocol + "//" + $wnd.location.hostname;
        var iframe = $wnd.document.getElementById('angularFrame').contentWindow;
        iframe.postMessage(message, domain);
    }-*/;

    @Override
    public Widget asWidget() {
        return view.asWidget();
    }

    public AdminCourseVersionView getView() {
        return view;
    }

    @Override
    public void upsertCourseVersion(CourseVersion courseVersion) {
        upsertCourseVersion(courseVersion, true);
    }

    @Override
    public void upsertCourseVersion(CourseVersion courseVersion, boolean goToListPlace) {
        if (courseVersion.getUUID() == null) {
            session.courseVersions().create(courseVersion, new Callback<CourseVersion>() {
                @Override
                public void ok(CourseVersion courseVersion) {
                    bus.fireEvent(new ShowPacifierEvent(false));
                    KornellNotification.show("Versão de curso criada com sucesso!");
                    PlaceUtils.reloadCurrentPlace(bus, placeController);
                }

                @Override
                public void conflict(KornellErrorTO kornellErrorTO) {
                    bus.fireEvent(new ShowPacifierEvent(false));
                    KornellNotification.show(KornellConstantsHelper.getErrorMessage(kornellErrorTO), AlertType.ERROR,
                            2500);
                }
            });
        } else {
            session.courseVersion(courseVersion.getUUID()).update(courseVersion, new Callback<CourseVersion>() {
                @Override
                public void ok(CourseVersion courseVersion) {
                    bus.fireEvent(new ShowPacifierEvent(false));
                    KornellNotification.show("Alterações salvas com sucesso!");
                    if (goToListPlace) {
                        placeController.goTo(new AdminCourseVersionsPlace());
                    }
                }

                @Override
                public void conflict(KornellErrorTO kornellErrorTO) {
                    bus.fireEvent(new ShowPacifierEvent(false));
                    KornellNotification.show(KornellConstantsHelper.getErrorMessage(kornellErrorTO), AlertType.ERROR,
                            2500);
                }
            });
        }
    }

    @Override
    public void buildContentView(CourseVersion courseVersion, Course course) {
        if (courseVersionContentPresenter == null) {
            courseVersionContentPresenter = new AdminCourseVersionContentPresenter(session, placeController, bus,
                    defaultPlace, viewFactory);
        }
        courseVersionContentPresenter.init(courseVersion, course);
        view.addContentPanel((AdminCourseVersionContentView) courseVersionContentPresenter.asWidget());
    }
}