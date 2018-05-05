package kornell.gui.client.presentation.welcome.generic;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.constants.IconType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.gui.client.ClientFactory;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.CourseClassesFetchedEvent;
import kornell.gui.client.personnel.classroom.Student;
import kornell.gui.client.personnel.classroom.Teacher;
import kornell.gui.client.personnel.classroom.Teachers;
import kornell.gui.client.presentation.welcome.WelcomePlace;
import kornell.gui.client.presentation.welcome.WelcomeView;
import kornell.gui.client.util.view.KornellNotification;

public class GenericWelcomeView extends Composite implements WelcomeView {
    interface MyUiBinder extends UiBinder<Widget, GenericWelcomeView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    @UiField
    FlowPanel coursesWrapper;
    @UiField
    FlowPanel pnlCourses;

    Button btnCoursesAll, btnCoursesInProgress, btnCoursesToStart, btnCoursesToAcquire, btnCoursesFinished;

    private KornellSession session;
    private PlaceController placeCtrl;
    private ViewFactory viewFactory;
    private EventBus bus;
    private static KornellConstants constants = GWT.create(KornellConstants.class);
    private Button selectedFilterButton;
    private ArrayList<IsWidget> widgets;

    private int classesCount;

    public GenericWelcomeView(ClientFactory clientFactory) {
        this.session = clientFactory.getKornellSession();
        this.placeCtrl = clientFactory.getPlaceController();
        this.viewFactory = clientFactory.getViewFactory();
        this.bus = clientFactory.getEventBus();
        initWidget(uiBinder.createAndBindUi(this));
        coursesWrapper.setVisible(false);

        initData();

        bus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
            @Override
            public void onPlaceChange(PlaceChangeEvent event) {
                if (event.getNewPlace() instanceof WelcomePlace) {
                    initData();
                }
            }
        });
    }

    private void startPlaceBar() {
        if (widgets == null) {
            widgets = new ArrayList<>();
            btnCoursesFinished = startButton(constants.finished(), widgets);
            btnCoursesToAcquire = startButton(constants.available(), widgets);
            btnCoursesToStart = startButton(constants.toStart(), widgets);
            btnCoursesInProgress = startButton(constants.inProgress(), widgets);
            btnCoursesAll = startButton(constants.allClasses(), widgets);
            selectedFilterButton = btnCoursesAll;
        }
        viewFactory.getMenuBarView().setPlaceBarWidgets(widgets);
    }

    private Button startButton(String text, List<IsWidget> widgets) {
        Button button = new Button(text);
        button.addStyleName("btnPlaceBar");
        button.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                selectedFilterButton = (Button) event.getSource();
                initData();
            }
        });
        widgets.add(button);
        return button;
    }

    private void initData() {
        viewFactory.getMenuBarView().initPlaceBar(IconType.HOME, constants.homeTitle(), constants.homeDescription());
        session.courseClasses().getCourseClassesTO(new Callback<CourseClassesTO>() {
            @Override
            public void ok(CourseClassesTO courseClassesTO) {
                bus.fireEvent(new CourseClassesFetchedEvent(courseClassesTO));
                startPlaceBar();
                display(courseClassesTO);
            }
        });
    }

    private void display(final CourseClassesTO tos) {
        pnlCourses.clear();
        final int classesCount = tos.getCourseClasses().size();
        if (classesCount == 0) {
            coursesWrapper.setVisible(false);
            KornellNotification.show(constants.noClassesAvailable(), AlertType.WARNING, 8000);
        } else {
            coursesWrapper.setVisible(true);
        }

        prepareButtons(classesCount);
        prepareClassesPanel(tos);
    }

    private void prepareClassesPanel(final CourseClassesTO tos) {
        classesCount = tos.getCourseClasses().size();

        for (final CourseClassTO courseClassTO : tos.getCourseClasses()) {
            if (courseClassTO.getCourseClass().isInvisible()) {
                continue;
            }

            final Teacher teacher = Teachers.of(courseClassTO);
            Student student = teacher.student(session.getCurrentUser());
            if (!courseClassTO.getCourseClass().isSandbox()) {
                addPanelIfFiltered(btnCoursesAll, courseClassTO);
                if (student.isEnrolled()) {
                    EnrollmentProgressDescription description = student.getEnrollmentProgress().getDescription();
                    switch (description) {
                    case completed:
                        addPanelIfFiltered(btnCoursesFinished, courseClassTO);
                        break;
                    case inProgress:
                        addPanelIfFiltered(btnCoursesInProgress, courseClassTO);
                        break;
                    case notStarted:
                        addPanelIfFiltered(btnCoursesToStart, courseClassTO);
                        break;
                    }
                } else {
                    addPanelIfFiltered(btnCoursesToAcquire, courseClassTO);
                }
            }
        }
    }

    private void addPanelIfFiltered(Button button, CourseClassTO courseClassTO) {
        if (button.equals(selectedFilterButton)) {
            pnlCourses.add(new GenericCourseSummaryView(placeCtrl, courseClassTO, session));
        }
        if (classesCount > 3) {
            button.setVisible(true);
        }
    }

    private void prepareButtons(int classesCount) {
        refreshButtonSelection(btnCoursesAll);
        refreshButtonSelection(btnCoursesInProgress);
        refreshButtonSelection(btnCoursesToStart);
        refreshButtonSelection(btnCoursesToAcquire);
        refreshButtonSelection(btnCoursesFinished);
    }

    private void refreshButtonSelection(Button button) {
        button.setVisible(false);
        button.removeStyleName("btnAction");
        button.addStyleName("btnNotSelected");
        if (selectedFilterButton != null && selectedFilterButton.equals(button)) {
            button.addStyleName("btnAction");
            button.removeStyleName("btnNotSelected");
        }
    }

    @Override
    public void setPresenter(Presenter presenter) {
    }
}