package kornell.gui.client.personnel;

import java.util.ArrayList;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.ChatThreadsClient;
import kornell.api.client.KornellSession;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.UnreadChatThreadsTO;
import kornell.core.to.UserInfoTO;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ComposeMessageEvent;
import kornell.gui.client.event.ComposeMessageEventHandler;
import kornell.gui.client.event.CourseClassesFetchedEvent;
import kornell.gui.client.event.CourseClassesFetchedEventHandler;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.event.UnreadMessagesFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.presentation.admin.AdminPlace;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPlace;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.message.MessagePlace;
import kornell.gui.client.presentation.message.compose.MessageComposePresenter;
import kornell.gui.client.presentation.message.compose.MessageComposeView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.util.view.Positioning;

public class MrPostman implements ComposeMessageEventHandler, LoginEventHandler, CourseClassesFetchedEventHandler {

    private static PopupPanel popup;
    private EventBus bus;
    private KornellSession session;
    private ViewFactory viewFactory;
    private EntityFactory entityFactory;
    private ChatThreadsClient chatThreadsClient;
    private PlaceController placeCtrl;
    private MessageComposeView.Presenter messageComposePresenter;
    private Timer unreadMessagesCountTimer;
    private Timer unreadMessagesCountPerThreadTimer;
    private ArrayList<CourseClassTO> helpCourseClasses;

    public MrPostman(ViewFactory viewFactory, EventBus bus, KornellSession session, PlaceController placeCtrl,
            EntityFactory entityFactory, CourseClassesTO courseClassesTO) {
        this.viewFactory = viewFactory;
        this.bus = bus;
        this.session = session;
        this.entityFactory = entityFactory;
        this.chatThreadsClient = session.chatThreads();
        this.placeCtrl = placeCtrl;

        filterHelpCourseClasses(courseClassesTO);

        initializeMessagePresenters();

        // initializeUnreadMessagesCountTimer();
        initializeUnreadMessagesCountPerThreadTimer();
        this.bus.addHandler(ComposeMessageEvent.TYPE, this);
        this.bus.addHandler(LoginEvent.TYPE, this);
        this.bus.addHandler(CourseClassesFetchedEvent.TYPE, this);
    }

    private void initializeMessagePresenters() {
        viewFactory.getMessagePresenter();
        viewFactory.getMessagePresenterClassroomGlobalChat();
        viewFactory.getMessagePresenterClassroomTutorChat();
        if (session.hasAnyAdminRole()) {
            viewFactory.getMessagePresenterCourseClass();
        }
        this.messageComposePresenter = new MessageComposePresenter(placeCtrl, session, viewFactory, entityFactory);
    }

    private void initializeUnreadMessagesCountPerThreadTimer() {

        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getUnreadMessagesPerThread();
            }
        });

        unreadMessagesCountPerThreadTimer = new Timer() {
            public void run() {
                getUnreadMessagesPerThread();
            }
        };

        // Schedule the timer to run every 30 seconds
        unreadMessagesCountPerThreadTimer.scheduleRepeating(30 * 1000);

        bus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {

            @Override
            public void onPlaceChange(PlaceChangeEvent event) {
                Place place = event.getNewPlace();
                if (place instanceof MessagePlace || place instanceof AdminCourseClassPlace
                        || place instanceof ClassroomPlace) {
                    getUnreadMessagesPerThread(true);
                }
                if (popup != null && popup.isShowing()) {
                    boolean showingPlacePanel = !(place instanceof VitrinePlace || place instanceof ClassroomPlace
                            || place instanceof AdminPlace);
                    int popupPosition = showingPlacePanel ? Positioning.NORTH_BAR_PLUS : Positioning.NORTH_BAR;
                    popup.setPopupPosition(popup.getAbsoluteLeft(), popupPosition);
                }
            }
        });
    }

    private void getUnreadMessagesPerThread() {
        getUnreadMessagesPerThread(false);
    }

    private void getUnreadMessagesPerThread(boolean forceFetch) {
        if (forceFetch || !(placeCtrl.getWhere() instanceof VitrinePlace)) {
            chatThreadsClient.getTotalUnreadCountsPerThread(new Callback<UnreadChatThreadsTO>() {
                @Override
                public void ok(UnreadChatThreadsTO unreadChatThreadsTO) {
                    bus.fireEvent(
                            new UnreadMessagesPerThreadFetchedEvent(unreadChatThreadsTO.getUnreadChatThreadTOs()));
                }
            });
        }
    }

    @Override
    public void onLogin(UserInfoTO user) {
        // Fetch all messages after 2 seconds
        new Timer() {
            public void run() {
                getUnreadMessagesPerThread(true);
            }
        }.schedule(2 * 1000);
    }

    @Override
    public void onCourseClassesFetched(CourseClassesFetchedEvent event) {
        filterHelpCourseClasses(event.getCourseClassesTO());
    }

    private void filterHelpCourseClasses(CourseClassesTO courseClassesTO) {
        this.helpCourseClasses = new ArrayList<CourseClassTO>();
        if (courseClassesTO != null) {
            for (CourseClassTO courseClassTO : courseClassesTO.getCourseClasses()) {
                if ((courseClassTO.getEnrollment() != null || courseClassTO.getCourseClass().isPublicClass())
                        && !courseClassTO.getCourseClass().isInvisible()) {
                    this.helpCourseClasses.add(courseClassTO);
                }
            }
        }
    }

    @Override
    public void onComposeMessage(ComposeMessageEvent event) {
        if (popup == null || !popup.isShowing()) {
            messageComposePresenter.init(helpCourseClasses);
            show(event.isShowingPlacePanel());
        } else {
            hide();
        }
    }

    @SuppressWarnings("unused")
    private void initializeUnreadMessagesCountTimer() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                getUnreadMessages();
            }
        });

        unreadMessagesCountTimer = new Timer() {
            public void run() {
                getUnreadMessages();
            }
        };

        // Schedule the timer to run every 2 minutes
        unreadMessagesCountTimer.scheduleRepeating(2 * 60 * 1000);
    }

    private void getUnreadMessages() {
        if (!(placeCtrl.getWhere() instanceof VitrinePlace)) {
            chatThreadsClient.getTotalUnreadCount(new Callback<String>() {
                @Override
                public void ok(String unreadMessagesCount) {
                    bus.fireEvent(new UnreadMessagesFetchedEvent(unreadMessagesCount));
                }
            });
        }
    }

    public void show(boolean showingPlacePanel) {
        if (popup == null) {
            popup = new PopupPanel(false, false);
            popup.addStyleName("messagesPopup");
            FlowPanel panel = new FlowPanel();
            if (messageComposePresenter != null) {
                panel.add(messageComposePresenter.asWidget());
            }
            popup.setGlassEnabled(false);
            popup.add(panel);
            popup.center();
        }
        popup.show();
        popup.setPopupPosition(popup.getAbsoluteLeft(),
                showingPlacePanel ? Positioning.NORTH_BAR_PLUS : Positioning.NORTH_BAR);
    }

    public static void hide() {
        if (popup != null) {
            popup.hide();
        }
    }
}
