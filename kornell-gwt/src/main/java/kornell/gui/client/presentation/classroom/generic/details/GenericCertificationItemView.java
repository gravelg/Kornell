package kornell.gui.client.presentation.classroom.generic.details;

import static kornell.core.util.StringUtils.mkurl;
import static kornell.core.util.StringUtils.noneEmpty;

import java.math.BigDecimal;

import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Assessment;
import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentCategory;
import kornell.core.entity.EnrollmentProgressDescription;
import kornell.core.entity.Person;
import kornell.core.to.CourseClassTO;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.event.ProgressEventHandler;
import kornell.gui.client.event.ShowDetailsEvent;
import kornell.gui.client.event.ShowDetailsEventHandler;
import kornell.gui.client.util.ClientConstants;
import kornell.gui.client.util.view.KornellNotification;

public class GenericCertificationItemView extends Composite implements ProgressEventHandler, ShowDetailsEventHandler{
	interface MyUiBinder extends UiBinder<Widget, GenericCertificationItemView> {
	}

	private static KornellConstants constants = GWT.create(KornellConstants.class);
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private EventBus bus;
	private KornellSession session;
	private CourseClassTO currentCourseClass;
	private String name;
	private String status;
	private String grade;
	
	private HandlerRegistration actionHandler;
	
	@UiField
	Image certificationIcon;
	@UiField
	Label lblName;
	@UiField
	Label lblStatus;
	@UiField
	Label lblGrade;
	@UiField
	Anchor lblActions;
	
	private boolean courseClassComplete;
	private boolean allowCertificateGeneration;


	public GenericCertificationItemView(EventBus eventBus, KornellSession session, CourseClassTO currentCourseClass) {
		this.bus = eventBus;
		this.session = session;
		this.currentCourseClass = currentCourseClass;
		bus.addHandler(ProgressEvent.TYPE,this);
		bus.addHandler(ShowDetailsEvent.TYPE,this);
		initWidget(uiBinder.createAndBindUi(this));
		initData();
		displayGenericCertificationItemView();
	}

	private void initData() {
		this.name = constants.certificateName();
		this.grade = "-";
		updateCertificationLinkAndLabel();
	}

	private void displayGenericCertificationItemView() {
		String url = mkurl(ClientConstants.IMAGES_PATH, "courseDetails", "certification.png");
		certificationIcon.setUrl(url);
		lblName.setText(name);
		lblStatus.setText(status);
		lblGrade.setText(grade);
	}

	private void displayActionCell(boolean show) {
		if (show) {
			lblActions.setText(constants.generate());
			lblActions.addStyleName("cursorPointer");
			actionHandler = lblActions.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					KornellNotification.show(constants.waitAMinute(), AlertType.WARNING, 2000);
					CourseClass courseClass = currentCourseClass != null ? currentCourseClass.getCourseClass() : null;		
					UserInfoTO currentUser = session != null ? session.getCurrentUser() : null;
					Person person = currentUser != null? currentUser.getPerson() : null;
					String personUUID = person != null ? person.getUUID() : null;
					String courseClassUUID = courseClass != null ? courseClass.getUUID() : null;
					if (noneEmpty(personUUID,courseClassUUID)){
						session.report().locationAssign("/report/certificate/",personUUID,courseClassUUID);
					}
				}
			});
		} else {
			lblActions.setText("-");
			lblActions.removeStyleName("cursorPointer");
			if(actionHandler != null){
				actionHandler.removeHandler();
			}
		}
	}

	@Override
	public void onProgress(ProgressEvent event) {
		if(event.getProgressPercent() >= 100){
			courseClassComplete = true;
			checkCertificateAvailability();
		}
		if(currentCourseClass != null){
			currentCourseClass.getEnrollment().setProgress(event.getProgressPercent());
		}
	}

	private void updateCertificationLinkAndLabel(){
		currentCourseClass = session.getCurrentCourseClass();
		if(currentCourseClass == null) return;
		CourseClass courseClass = currentCourseClass.getCourseClass();
		Enrollment currEnrollment = currentCourseClass.getEnrollment();
		if(currEnrollment == null) return;
		
		Integer progress = currEnrollment.getProgress() != null ? currEnrollment.getProgress() : -1;
		courseClassComplete = progress >= 100;
		
		Assessment assessment = currEnrollment.getAssessment(); 
		boolean approvedOnTest = (Assessment.PASSED.equals(assessment) && currEnrollment.getCertifiedAt() != null) ||
				currentCourseClass.getCourseClass().getRequiredScore() == null ||
				BigDecimal.ZERO.equals(currentCourseClass.getCourseClass().getRequiredScore()) ||
				(currEnrollment.getAssessmentScore() != null &&
						currentCourseClass.getCourseClass().getRequiredScore().compareTo(currEnrollment.getAssessmentScore()) <= 0);
		
		allowCertificateGeneration = (courseClassComplete && approvedOnTest);
		status = allowCertificateGeneration ? constants.certificateAvailable() : constants.certificateNotAvailable();
		lblStatus.setText(status);

		BigDecimal requiredScore = courseClass != null ? courseClass.getRequiredScore() : null;
		if(currEnrollment != null &&
				requiredScore != null && 
				requiredScore.intValue() != 0 &&
				currEnrollment.getAssessmentScore() != null &&
				(EnrollmentProgressDescription.completed.equals(EnrollmentCategory.getEnrollmentProgressDescription(currEnrollment)) || allowCertificateGeneration)){
			this.grade = ""+currEnrollment.getAssessmentScore().intValue();
		} else {
			this.grade = "-";
		}
		lblGrade.setText(this.grade);

		displayActionCell(allowCertificateGeneration);
	}
	
	@Override
	public void onShowDetails(ShowDetailsEvent event) {
		if(event.isShowDetails())
			checkCertificateAvailability();
	}
	
	private void checkCertificateAvailability() {
		if(!allowCertificateGeneration && session.getCurrentCourseClass() != null && session.getCurrentCourseClass().getEnrollment() != null){
			Timer checkTimer = new Timer() {
				@Override
				public void run() {
					if(session.getCurrentCourseClass() != null){
					    session.enrollment(session.getCurrentCourseClass().getEnrollment().getUUID())
					    .isApproved(new Callback<String>() {
					    	@Override
					    	public void ok(String grade) {
					    		if(StringUtils.isSome(grade)){
					    			currentCourseClass = session.getCurrentCourseClass();
					    			session.getCurrentCourseClass().getEnrollment().setAssessmentScore(new BigDecimal(grade));	
					    		}
					    		updateCertificationLinkAndLabel();
					    	}
						});
					}
				}
			};
			checkTimer.schedule(3000);
			//TODO: MDA (to be resolved on the refactoring of the details)
		}
	}
}
