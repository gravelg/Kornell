package kornell.gui.client;

import static kornell.core.util.StringUtils.mkurl;

import com.google.gwt.dom.client.StyleInjector;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

import kornell.core.to.CourseClassesTO;
import kornell.gui.client.event.CourseClassesFetchedEvent;
import kornell.gui.client.event.CourseClassesFetchedEventHandler;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.event.ShowDetailsEventHandler;
import kornell.gui.client.presentation.admin.assets.AdminAssetsView;
import kornell.gui.client.presentation.admin.assets.generic.GenericAdminAssetsView;
import kornell.gui.client.presentation.admin.audit.AdminAuditView;
import kornell.gui.client.presentation.admin.audit.generic.GenericAdminAuditView;
import kornell.gui.client.presentation.admin.common.ConfirmModalView;
import kornell.gui.client.presentation.admin.course.course.AdminCoursePresenter;
import kornell.gui.client.presentation.admin.course.course.AdminCourseView;
import kornell.gui.client.presentation.admin.course.course.generic.GenericAdminCourseView;
import kornell.gui.client.presentation.admin.course.courses.AdminCoursesView;
import kornell.gui.client.presentation.admin.course.courses.generic.GenericAdminCoursesView;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassPresenter;
import kornell.gui.client.presentation.admin.courseclass.courseclass.AdminCourseClassView;
import kornell.gui.client.presentation.admin.courseclass.courseclass.generic.GenericAdminCourseClassView;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.AdminCourseClassesView;
import kornell.gui.client.presentation.admin.courseclass.courseclasses.generic.GenericAdminCourseClassesView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPresenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.generic.GenericAdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.generic.GenericAdminCourseVersionView;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsView;
import kornell.gui.client.presentation.admin.courseversion.courseversions.generic.GenericAdminCourseVersionsView;
import kornell.gui.client.presentation.admin.institution.AdminInstitutionView;
import kornell.gui.client.presentation.admin.institution.generic.GenericAdminInstitutionView;
import kornell.gui.client.presentation.bar.MenuBarView;
import kornell.gui.client.presentation.bar.SouthBarView;
import kornell.gui.client.presentation.bar.generic.GenericMenuBarView;
import kornell.gui.client.presentation.bar.generic.GenericSouthBarView;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.presentation.classroom.ClassroomPresenter;
import kornell.gui.client.presentation.classroom.ClassroomView;
import kornell.gui.client.presentation.classroom.generic.GenericClassroomView;
import kornell.gui.client.presentation.home.HomeView;
import kornell.gui.client.presentation.home.generic.GenericHomeView;
import kornell.gui.client.presentation.message.MessagePanelType;
import kornell.gui.client.presentation.message.MessagePresenter;
import kornell.gui.client.presentation.message.MessageView;
import kornell.gui.client.presentation.message.compose.GenericMessageComposeView;
import kornell.gui.client.presentation.message.compose.MessageComposeView;
import kornell.gui.client.presentation.message.generic.GenericMessageView;
import kornell.gui.client.presentation.profile.ProfileView;
import kornell.gui.client.presentation.profile.generic.GenericProfileView;
import kornell.gui.client.presentation.sandbox.SandboxPresenter;
import kornell.gui.client.presentation.sandbox.SandboxView;
import kornell.gui.client.presentation.sandbox.generic.GenericSandboxView;
import kornell.gui.client.presentation.terms.TermsView;
import kornell.gui.client.presentation.terms.generic.GenericTermsView;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.presentation.vitrine.VitrineView;
import kornell.gui.client.presentation.vitrine.generic.GenericVitrineView;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.presentation.welcome.generic.GenericWelcomeView;
import kornell.gui.client.sequence.SequencerFactory;
import kornell.gui.client.sequence.SequencerFactoryImpl;

public class GenericViewFactoryImpl implements ViewFactory, ShowDetailsEventHandler, CourseClassesFetchedEventHandler {

    private ClientFactory clientFactory;

    /* Views */
    private ScrollPanel scrollPanel;
    private GenericMenuBarView menuBarView;
    private SouthBarView southBarView;
    private GenericHomeView genericHomeView;
    private GenericAdminCourseClassView genericAdminHomeView;
    private AdminCourseClassPresenter genericAdminCourseClassPresenter;
    private AdminCourseVersionPresenter genericAdminCourseVersionPresenter;
    private GenericAdminCourseClassesView genericAdminCourseClassesView;
    private GenericAdminInstitutionView genericAdminInstitutionView;
    private GenericAdminCoursesView genericAdminCoursesView;
    private GenericAdminCourseView genericAdminCourseView;
    private AdminCoursePresenter genericAdminCoursePresenter;
    private GenericAdminCourseVersionsView genericAdminCourseVersionsView;
    private GenericAdminCourseVersionView genericAdminCourseVersionView;
    private GenericAdminCourseVersionContentView genericAdminCourseVersionContentView;
    private GenericAdminAssetsView genericAdminAssetsView;
    private GenericAdminAuditView genericAdminAuditView;
    private ConfirmModalView confirmModalView;
    private ClassroomPresenter coursePresenter;
    private SandboxPresenter sandboxPresenter;
    private MessagePresenter messagePresenter, messagePresenterCourseClass, messagePresenterClassroomGlobalChat,
            messagePresenterClassroomTutorChat;
    private boolean isMantleShown = false;
    private CourseClassesTO courseClassesTO;

    private SimplePanel shell = new SimplePanel();

    public GenericViewFactoryImpl(ClientFactory clientFactory, CourseClassesTO courseClassesTO) {
        this.clientFactory = clientFactory;
        this.courseClassesTO = courseClassesTO;
        clientFactory.getEventBus().addHandler(ShowDetailsEvent.TYPE, this);
        clientFactory.getEventBus().addHandler(CourseClassesFetchedEvent.TYPE, this);
    }

    @Override
    public void initGUI() {

        shell.addStyleName("wrapper");
        scrollPanel = new ScrollPanel();
        scrollPanel.add(shell);
        scrollPanel.addStyleName("vScrollBar");

        final RootPanel rootPanel = RootPanel.get();
        rootPanel.add(scrollPanel);
        setBackgroundImage(true);

        clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
            @Override
            public void onPlaceChange(PlaceChangeEvent event) {
                setPlaceNameAsBodyStyle(event);
                setBackgroundImage(event.getNewPlace() instanceof VitrinePlace);
                checkMenuBars(event.getNewPlace() instanceof VitrinePlace);
                changeOverflow(event.getNewPlace() instanceof ClassroomPlace
                        && clientFactory.getKornellSession().getCurrentCourseClass() != null);
            }

            private void checkMenuBars(boolean removePanels) {
                if (!removePanels && rootPanel.getWidgetCount() < 3) {
                    rootPanel.add(getMenuBarView());
                    rootPanel.add(getSouthBarView());
                }
            }

            private void setPlaceNameAsBodyStyle(PlaceChangeEvent event) {
                String styleName = rootPanel.getStyleName();
                if (!styleName.isEmpty())
                    rootPanel.removeStyleName(styleName);
                String[] split = event.getNewPlace().getClass().getName().split("\\.");
                String newStyle = split[split.length - 1];
                rootPanel.addStyleName(newStyle);
            }
        });
    }

    @Override
    public void onShowDetails(ShowDetailsEvent event) {
        changeOverflow(event.isShowDetails());
    }

    private void changeOverflow(boolean removeOverflow) {
        if (removeOverflow) {
            scrollPanel.removeStyleName("overflow");
        } else {
            scrollPanel.addStyleName("overflow");
        }
    }

    private void setBackgroundImage(boolean showMantle) {
        if (showMantle == isMantleShown)
            return;
        String style = "position: relative; " + "zoom: 1; " + "-webkit-background-size: cover; "
                + "-moz-background-size: cover; " + "-o-background-size: cover; " + "background-size: cover;"
                + "overflow:auto;";
        if (showMantle) {
            style = "background: url('"
                    + mkurl(clientFactory.getKornellSession().getInstitutionAssetsURL(), "bgVitrine.jpg")
                    + "') no-repeat center center fixed; " + style;
        } else {
            style = "background: transparent; " + style;
        }
        style = ".vScrollBar {" + style + "}";
        StyleInjector.inject(style);
        // DOM.setElementAttribute(scrollPanel.getElement(), "style", style);
        isMantleShown = showMantle;
    }

    @Override
    public MenuBarView getMenuBarView() {
        if (menuBarView == null)
            menuBarView = new GenericMenuBarView(clientFactory, scrollPanel, courseClassesTO);
        return menuBarView;
    }

    @Override
    public SouthBarView getSouthBarView() {
        if (southBarView == null)
            southBarView = new GenericSouthBarView(clientFactory, scrollPanel);
        return southBarView;
    }

    @Override
    public HomeView getHomeView() {
        if (genericHomeView == null) {
            genericHomeView = new GenericHomeView(clientFactory, clientFactory.getEventBus(),
                    clientFactory.getKornellSession());
        }
        return genericHomeView;
    }

    @Override
    public VitrineView getVitrineView() {
        return new GenericVitrineView();
    }

    @Override
    public WelcomeView getWelcomeView() {
        return new GenericWelcomeView(clientFactory);
    }

    @Override
    public ProfileView getProfileView() {
        return new GenericProfileView(clientFactory);
    }

    @Override
    public MessageView getMessageView() {
        return new GenericMessageView(clientFactory.getEventBus(), clientFactory.getKornellSession());
    }

    @Override
    public MessageComposeView getMessageComposeView() {
        return new GenericMessageComposeView(clientFactory.getKornellSession());
    }

    @Override
    public ClassroomView getClassroomView() {
        return new GenericClassroomView(clientFactory.getPlaceController(), clientFactory.getKornellSession(),
                clientFactory.getEventBus(), this);
    }

    @Override
    public TermsView getTermsView() {
        return new GenericTermsView(clientFactory);
    }

    @Override
    public ClassroomPresenter getClassroomPresenter() {
        if (coursePresenter == null) {
            SequencerFactory rendererFactory = new SequencerFactoryImpl(clientFactory.getEventBus(),
                    clientFactory.getPlaceController(), clientFactory.getKornellSession());
            ClassroomView activityView = getClassroomView();
            coursePresenter = new ClassroomPresenter(clientFactory.getEventBus(), activityView,
                    clientFactory.getPlaceController(), rendererFactory, clientFactory.getKornellSession());
        }
        return coursePresenter;
    }

    @Override
    public SandboxView getSandboxView() {
        return new GenericSandboxView(clientFactory.getKornellSession());
    }

    @Override
    public SandboxPresenter getSandboxPresenter() {
        if (sandboxPresenter == null) {
            SandboxView sandboxView = getSandboxView();
            sandboxPresenter = new SandboxPresenter(sandboxView);
        }
        return sandboxPresenter;
    }

    @Override
    public AdminCourseClassView getAdminCourseClassView() {
        if (genericAdminHomeView == null)
            genericAdminHomeView = new GenericAdminCourseClassView(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
        return genericAdminHomeView;
    }

    @Override
    public MessagePresenter getMessagePresenter() {
        if (messagePresenter == null)
            messagePresenter = new MessagePresenter(clientFactory.getKornellSession(), clientFactory.getEventBus(),
                    clientFactory.getPlaceController(), clientFactory.getViewFactory(), MessagePanelType.inbox);
        return messagePresenter;
    }

    @Override
    public MessagePresenter getMessagePresenterCourseClass() {
        if (messagePresenterCourseClass == null)
            messagePresenterCourseClass = new MessagePresenter(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory(),
                    MessagePanelType.courseClassSupport);
        return messagePresenterCourseClass;
    }

    @Override
    public MessagePresenter getMessagePresenterClassroomGlobalChat() {
        if (messagePresenterClassroomGlobalChat == null)
            messagePresenterClassroomGlobalChat = new MessagePresenter(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory(),
                    MessagePanelType.courseClassGlobal);
        return messagePresenterClassroomGlobalChat;
    }

    @Override
    public MessagePresenter getMessagePresenterClassroomTutorChat() {
        if (messagePresenterClassroomTutorChat == null)
            messagePresenterClassroomTutorChat = new MessagePresenter(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory(),
                    MessagePanelType.courseClassTutor);
        return messagePresenterClassroomTutorChat;
    }

    @Override
    public SimplePanel getShell() {
        return shell;
    }

    @Override
    public ScrollPanel getScrollPanel() {
        return scrollPanel;
    }

    @Override
    public AdminInstitutionView getAdminInstitutionView() {
        if (genericAdminInstitutionView == null)
            genericAdminInstitutionView = new GenericAdminInstitutionView(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
        return genericAdminInstitutionView;
    }

    @Override
    public AdminCourseView getAdminCourseView() {
        if (genericAdminCourseView == null)
            genericAdminCourseView = new GenericAdminCourseView(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
        return genericAdminCourseView;
    }

    @Override
    public AdminCoursePresenter getAdminCoursePresenter() {
        if (genericAdminCoursePresenter == null)
            genericAdminCoursePresenter = new AdminCoursePresenter(clientFactory.getKornellSession(),
                    clientFactory.getPlaceController(), clientFactory.getEventBus(), clientFactory.getDefaultPlace(),
                    this);
        return genericAdminCoursePresenter;
    }

    @Override
    public AdminCourseVersionView getAdminCourseVersionView() {
        if (genericAdminCourseVersionView == null)
            genericAdminCourseVersionView = new GenericAdminCourseVersionView(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
        return genericAdminCourseVersionView;
    }

    @Override
    public AdminCourseVersionContentView getAdminCourseVersionContentView() {
        if (genericAdminCourseVersionContentView == null)
            genericAdminCourseVersionContentView = new GenericAdminCourseVersionContentView(
                    clientFactory.getKornellSession(), clientFactory.getEventBus(), clientFactory.getPlaceController());
        return genericAdminCourseVersionContentView;
    }

    @Override
    public AdminCoursesView getAdminCoursesView() {
        if (genericAdminCoursesView == null)
            genericAdminCoursesView = new GenericAdminCoursesView(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
        return genericAdminCoursesView;
    }

    @Override
    public AdminCourseVersionsView getAdminCourseVersionsView() {
        if (genericAdminCourseVersionsView == null)
            genericAdminCourseVersionsView = new GenericAdminCourseVersionsView(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
        return genericAdminCourseVersionsView;
    }

    @Override
    public AdminCourseVersionPresenter getAdminCourseVersionPresenter() {
        if (genericAdminCourseVersionPresenter == null)
            genericAdminCourseVersionPresenter = new AdminCourseVersionPresenter(clientFactory.getKornellSession(),
                    clientFactory.getPlaceController(), clientFactory.getEventBus(), clientFactory.getDefaultPlace(),
                    this);
        return genericAdminCourseVersionPresenter;
    }

    @Override
    public AdminCourseClassesView getAdminCourseClassesView() {
        if (genericAdminCourseClassesView == null)
            genericAdminCourseClassesView = new GenericAdminCourseClassesView(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
        return genericAdminCourseClassesView;
    }

    @Override
    public AdminCourseClassPresenter getAdminCourseClassPresenter() {
        if (genericAdminCourseClassPresenter == null)
            genericAdminCourseClassPresenter = new AdminCourseClassPresenter(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getDefaultPlace(),
                    GenericClientFactoryImpl.TO_FACTORY, this);
        return genericAdminCourseClassPresenter;
    }

    @Override
    public AdminAssetsView getAdminAssetsView() {
        if (genericAdminAssetsView == null)
            genericAdminAssetsView = new GenericAdminAssetsView(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), this);
        return genericAdminAssetsView;
    }

    @Override
    public AdminAuditView getAdminAuditView() {
        if (genericAdminAuditView == null)
            genericAdminAuditView = new GenericAdminAuditView(clientFactory.getKornellSession(),
                    clientFactory.getEventBus(), clientFactory.getPlaceController(), clientFactory.getViewFactory());
        return genericAdminAuditView;
    }

    @Override
    public void onCourseClassesFetched(CourseClassesFetchedEvent event) {
        this.courseClassesTO = event.getCourseClassesTO();
    }

    @Override
    public ConfirmModalView getConfirmModalView() {
        if (confirmModalView == null) {
            confirmModalView = new ConfirmModalView();
            clientFactory.getEventBus().addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
                @Override
                public void onPlaceChange(PlaceChangeEvent event) {
                    confirmModalView.hide();
                }
            });
        }
        return confirmModalView;
    }
}
