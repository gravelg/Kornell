package kornell.gui.client.presentation.classroom;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseClassState;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentState;
import kornell.core.entity.EnrollmentsEntries;
import kornell.core.error.KornellErrorTO;
import kornell.core.lom.Contents;
import kornell.core.to.CourseClassTO;
import kornell.core.to.EnrollmentLaunchTO;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.HideSouthBarEvent;
import kornell.gui.client.presentation.vitrine.VitrinePlace;
import kornell.gui.client.sequence.Sequencer;
import kornell.gui.client.sequence.SequencerFactory;
import kornell.gui.client.util.ClientProperties;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.LoadingPopup;
import kornell.scorm.client.scorm12.SCORM12Runtime;

public class ClassroomPresenter implements ClassroomView.Presenter {
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private static final String PREFIX = ClientProperties.PREFIX + "Classroom";

	private ClassroomView view;
	private ClassroomPlace place;
	private PlaceController placeCtrl;
	private SequencerFactory sequencerFactory;
	private KornellSession session;
	private Contents contents;
	private Sequencer sequencer;
	private EventBus bus;

	public ClassroomPresenter(EventBus bus, ClassroomView view, PlaceController placeCtrl,
			SequencerFactory seqFactory, KornellSession session) {
		this.bus = bus;
		this.view = view;
		view.setPresenter(this);
		this.placeCtrl = placeCtrl;
		this.sequencerFactory = seqFactory;
		this.session = session;


		bus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
			@Override
			public void onPlaceChange(PlaceChangeEvent event) {
				stopSequencer();
			}
		});
	}

	private void displayPlace() {
		if(session.isAnonymous()){
			placeCtrl.goTo(new VitrinePlace());
			return;
		}
		
		view.asWidget().setVisible(false);

        LoadingPopup.show();
        session.courseClasses().getByEnrollment(place.getEnrollmentUUID(), new Callback<CourseClassTO>() {
            @Override            
            public void ok(CourseClassTO courseClassTO) {
                LoadingPopup.hide();
        		courseClassFetched(courseClassTO);
            }
            
            @Override
            public void notFound(KornellErrorTO kornellErrorTO){
                LoadingPopup.hide();
        		courseClassFetched(null);
            }
        });
	}

	private void courseClassFetched(CourseClassTO courseClassTO) {
		
        ClientProperties.set(PREFIX + ClientProperties.SEPARATOR + session.getCurrentUser().getPerson().getUUID()
                + ClientProperties.SEPARATOR + ClientProperties.CURRENT_ENROLLMENT,
        		courseClassTO.getEnrollment().getUUID());
		
		Enrollment enrollment = courseClassTO != null ? courseClassTO.getEnrollment() : null;
		boolean isEnrolled = enrollment != null && EnrollmentState.enrolled.equals(enrollment.getState());
		if(enrollment == null){
			session.setCurrentCourseClass((CourseClassTO)null);
		} else {
			session.setCurrentCourseClass(courseClassTO);
		}
		
		CourseClass courseClass = session.getCurrentCourseClass() != null ? session.getCurrentCourseClass().getCourseClass() : null;
		CourseClassState courseClassState = courseClass != null ? courseClass.getState() : null;
		
		//TODO: Consider if the null state is inactive				
        final boolean showCourseClassContent = enrollment == null || (isEnrolled && (courseClassState != null && !CourseClassState.inactive.equals(courseClassState)));

        if(showCourseClassContent){
			LoadingPopup.show();		
			final PopupPanel popup = KornellNotification.show(constants.loadingTheCourse(), AlertType.WARNING, -1);
			bus.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
				@Override
				public void onPlaceChange(PlaceChangeEvent event) {
					popup.hide();
				}
			});
			session.enrollment(place.getEnrollmentUUID()).launch(new Callback<EnrollmentLaunchTO>() {
				
				public void ok(EnrollmentLaunchTO to) {
					popup.hide();
					loadRuntime(to.getEnrollmentEntries());
					loadContents(place.getEnrollmentUUID(),to.getContents());
				};
				
				private void loadRuntime(EnrollmentsEntries enrollmentEntries) {
					SCORM12Runtime.launch(bus, session,placeCtrl, enrollmentEntries);
				}
	
				private void loadContents(final String enrollmentUUID, final Contents contents) {
					// check if user has a valid enrollment to this course
					boolean shouldHideSouthBar = (session.getCurrentCourseClass().getCourseVersionTO().getCourseVersion().getParentVersionUUID() == null);
					GenericClientFactoryImpl.EVENT_BUS.fireEvent(new HideSouthBarEvent(shouldHideSouthBar));
					setContents(contents);
					view.display(showCourseClassContent);	
					view.asWidget().setVisible(true);
					LoadingPopup.hide();
				}
	
			});
		} else {
			GenericClientFactoryImpl.EVENT_BUS.fireEvent(new HideSouthBarEvent());
			setContents(null);
			view.display(showCourseClassContent);	
			view.asWidget().setVisible(true);
		}
	}	

	private FlowPanel getPanel() {
		return view.getContentPanel();
	}

	@Override
	public void startSequencer() {
		if(contents != null){
			sequencer = sequencerFactory.withPlace(place).withPanel(getPanel());
			sequencer.go(contents);
		}
	}

	@Override
	public void stopSequencer() {
		if (sequencer != null)
			sequencer.stop();
	}

	@Override
	public Contents getContents() {
		return contents;
	}

	private void setContents(Contents contents) {
		this.contents = contents;
	}

	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	public void setPlace(ClassroomPlace place) {
		this.place = place;
		displayPlace();
	}

	@Override
	public void fireProgressEvent() {
		if (sequencer != null)
			sequencer.fireProgressEvent();
	}
}
