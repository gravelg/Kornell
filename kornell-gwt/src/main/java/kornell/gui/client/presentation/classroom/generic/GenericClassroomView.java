package kornell.gui.client.presentation.classroom.generic;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.to.CourseClassTO;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowChatDockEvent;
import kornell.gui.client.event.ShowChatDockEventHandler;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.event.ShowDetailsEventHandler;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionPresenter;
import kornell.gui.client.presentation.classroom.ClassroomView;
import kornell.gui.client.presentation.classroom.generic.details.GenericCourseDetailsView;

public class GenericClassroomView extends Composite
        implements ClassroomView, ShowDetailsEventHandler, ShowChatDockEventHandler {
    interface MyUiBinder extends UiBinder<Widget, GenericClassroomView> {
    }

    private PlaceController placeCtrl;
    private KornellSession session;
    private EventBus bus;
    private ViewFactory viewFactory;

    Logger logger = Logger.getLogger(AdminInstitutionPresenter.class.getName());
    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    FlowPanel contentPanel;
    @UiField
    FlowPanel detailsPanel;
    @UiField
    FlowPanel dockChatPanel;

    private boolean showCourseClassContent;

    private GenericCourseDetailsView detailsView;

    private Presenter presenter;

    boolean chatDockWasShown = false;

    public GenericClassroomView(PlaceController placeCtrl, KornellSession session, EventBus bus,
            ViewFactory viewFactory) {
        this.placeCtrl = placeCtrl;
        this.session = session;
        this.bus = bus;
        this.bus.addHandler(ShowChatDockEvent.TYPE, this);
        this.viewFactory = viewFactory;
        bus.addHandler(ShowDetailsEvent.TYPE, this);
        initWidget(uiBinder.createAndBindUi(this));
        detailsPanel.setVisible(true);
        contentPanel.setVisible(false);
        dockChatPanel.setVisible(false);
    }

    @Override
    public void display(boolean showCourseClassContent) {
        presenter.stopSequencer();
        this.showCourseClassContent = showCourseClassContent;
        if (this.showCourseClassContent) {
            presenter.startSequencer();
        }
        detailsView = new GenericCourseDetailsView(bus, session, placeCtrl, viewFactory);
        detailsView.setPresenter(presenter);
        detailsView.initData();
        detailsPanel.clear();
        detailsPanel.add(detailsView);
        CourseClassTO courseClassTO = session.getCurrentCourseClass();
        Enrollment enrollment = courseClassTO != null ? courseClassTO.getEnrollment() : null;
        boolean showDetails = !showCourseClassContent || EnrollmentCategory.isFinished(enrollment);
        bus.fireEvent(new ShowDetailsEvent(showDetails));
        bus.fireEvent(new ShowChatDockEvent(!showDetails && session.getCurrentCourseClass() != null
                && session.getCurrentCourseClass().getCourseClass().isChatDockEnabled()));
    }

    @Override
    public void setPresenter(Presenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public FlowPanel getContentPanel() {
        return contentPanel;
    }

    @Override
    public void onShowDetails(ShowDetailsEvent event) {
        boolean showDetails = event.isShowDetails();
        contentPanel.setVisible(!showDetails);
        detailsPanel.setVisible(showDetails);
        if (showDetails) {
            if (dockChatPanel != null) {
                dockChatPanel.clear();
            }
            presenter.fireProgressEvent();
        }
    }

    @Override
    public void onShowChatDock(ShowChatDockEvent event) {
        dockChatPanel.setVisible(event.isShowChatDock());
        if (event.isShowChatDock()) {
            if (!chatDockWasShown) {
                dockChatPanel.clear();
                viewFactory.getMessagePresenterClassroomGlobalChat().threadClicked(null);
                chatDockWasShown = true;
            } else {
                viewFactory.getMessagePresenterClassroomGlobalChat().getChatThreadMessagesSinceLast();
            }
            dockChatPanel.add(viewFactory.getMessagePresenterClassroomGlobalChat().asWidget());
        } else if (dockChatPanel != null) {
            dockChatPanel.clear();
        }
    }
}
