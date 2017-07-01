package kornell.gui.client;

import com.google.gwt.i18n.client.ConstantsWithLookup;

public interface KornellConstants extends ConstantsWithLookup {
	
	@DefaultStringValue("Loading...")
	String loading();
	
	@DefaultStringValue("A new version of the site is available.")
	String newVersionAvailable();
	
	@DefaultStringValue("Redresh")
	String refresh();

	/**
	 * GenericClientFactoryImpl
	 */
	
	@DefaultStringValue("Institution not found.")
	String institutionNotFound();
	
	@DefaultStringValue("We are currently under maintenance. Please check again later. If the problem persists, contact us at suporte@craftware.com.br. Thanks for your patience.")
	String maintenanceMessage();
	
	/**
	 * VitrinePlace
	 */
	
	@DefaultStringValue("Select a language")
	String selectLanguage();
	
	@DefaultStringValue("Use the same email in which you receive the registration.")
	String registrationEmailMessage();
	
	@DefaultStringValue("Invalid username or password, please try again.")
	String badUsernamePassword();
	
	@DefaultStringValue("Your name must be at least 2 characters long.")
	String nameTooShort();

	@DefaultStringValue("Invalid email address.")
	String invalidEmail();
	
	@DefaultStringValue("Your password must be at least 6 characters long")
	String invalidPasswordTooShort();
	
	@DefaultStringValue("Your password contains invalid characters.")
	String invalidPasswordBadChar();
	
	@DefaultStringValue("Your passwords don't match.")
	String passwordMismatch();
	
	@DefaultStringValue("User created successfully.")
	String userCreated();
	
	@DefaultStringValue("Email address already exists.")
	String emailExists();
	
	@DefaultStringValue("Request completed. Please check your email.")
	String requestPasswordReset();
	
	@DefaultStringValue("The request was not successful. Please check that the email was entered correctly.")
	String requestPasswordResetError();
	
	@DefaultStringValue("Password changed successfully.")
	String passwordChangeComplete();
	
	@DefaultStringValue("We could not update your password. Please check your email or make another request.")
	String passwordChangeError();
	

	/**
	 * 
	 * GenericWelcomeView
	 * 
	 */
	@DefaultStringValue("All")
	String allClasses();

	@DefaultStringValue("Finished")
	String finished();

	@DefaultStringValue("To start")
	String toStart();

	@DefaultStringValue("In progress")
	String inProgress();


	/**
	 * 
	 * GenericCourseDetailsView
	 * 
	 */
	@DefaultStringValue("Course details: ")
	String detailsHeader();
	
	@DefaultStringValue("Class: ")
	String detailsSubHeader();
	
	@DefaultStringValue("About the course")
	String btnAbout();
	
	@DefaultStringValue("Topics")
	String btnTopics();
	
	@DefaultStringValue("Certification")
	String btnCertification();
	
	@DefaultStringValue("Chat")
	String btnChat();
	
	@DefaultStringValue("Tutoring")
	String btnTutor();
	
	@DefaultStringValue("Library")
	String btnLibrary();
	
	@DefaultStringValue("General view")
	String btnAboutInfo();
	
	@DefaultStringValue("Main topics covered on this course")
	String btnTopicsInfo();
	
	@DefaultStringValue("Evaluations and tests")
	String btnCertificationInfo();
	
	@DefaultStringValue("Close Details")
	String closeDetails();

	@DefaultStringValue("Topic")
	String topic();
	
	@DefaultStringValue("Certification")
	String certification();
	
	@DefaultStringValue("You can generate the certificate when you finish the course.")
	String certificationInfoText();
	
	@DefaultStringValue("Info")
	String certificationTableInfo();
	
	@DefaultStringValue("Status")
	String certificationTableStatus();
	
	@DefaultStringValue("Grade")
	String certificationTableGrade();
	
	@DefaultStringValue("Actions")
	String certificationTableActions();
	
	@DefaultStringValue("Print certificate")
	String printCertificateButton();
	
	@DefaultStringValue("With all the participants of this class")
	String classChatButton();
	
	@DefaultStringValue("With a tutor")
	String tutorChatButton();
	
	@DefaultStringValue("Supplementary material")
	String libraryButton();
	
	@DefaultStringValue("Go to class")
	String goToClassButton();
	
	@DefaultStringValue("This class has been disabled by the institution.<br><br> The material in this class is inaccessible.<br>")
	String inactiveCourseClass();
	
	@DefaultStringValue("Your registration was canceled by the institution.<br><br> The material in this class is inaccessible.<br>")
	String cancelledEnrollment();
	
	@DefaultStringValue("Your registration has not yet been approved by the institution.")
	String enrollmentNotApproved();
	
	@DefaultStringValue("You will receive an email at the time of approval.")
	String enrollmentConfirmationEmail();
	
	@DefaultStringValue("Description")
	String description();
	

	
	
	/**
	 * 
	 * GenericBarView
	 */
	
	@DefaultStringValue("course")
	String course();
	
	@DefaultStringValue("details")
	String details();
	
	@DefaultStringValue("library")
	String library();
	
	@DefaultStringValue("forum")
	String forum();
	
	@DefaultStringValue("chat")
	String chat();
	
	@DefaultStringValue("specialists")
	String specialists();
	
	@DefaultStringValue("notes")
	String notes();
	
	@DefaultStringValue("back")
	String back();
	
	@DefaultStringValue("next")
	String next();
	
	@DefaultStringValue("previous")
	String previous();
	
	@DefaultStringValue("institution")
	String institution();
	
	@DefaultStringValue("courses")
	String courses();
	
	@DefaultStringValue("versions")
	String versions();
	
	@DefaultStringValue("classes")
	String classes();
	
	
	/**
	 * GenericActivityBarView
	 */
	@DefaultStringValue("completed")
	String completed();
	
	@DefaultStringValue("Page")
    String pageForPagination();
	
	
	/**
	 * ClassroomPresenter
	 */
	@DefaultStringValue("Loading the course...")
	String loadingTheCourse();

	@DefaultStringValue("This class was set as invisible by the institution.")
	String classSetAsInvisible();
	
	
	/**
	* GenericCertificationItemVIew
	*/	
	@DefaultStringValue("Certificate")
	String certificateName();
	
	@DefaultStringValue("Generate")
	String generate();
	
	@DefaultStringValue("Wait a minute...")
	String waitAMinute();
	
	@DefaultStringValue("Available")
	String certificateAvailable();
	
	@DefaultStringValue("Unavailable")
	String certificateNotAvailable();
	
	
	/**
	 * GenericCourseLibraryView
	 */
	@DefaultStringValue("Library")
	String libraryTitle();
	
	@DefaultStringValue("Here you can find the supplementary material for the course.")
	String libraryInfo();
	
	@DefaultStringValue("Type")
	String libraryEntryIcon();
	
	@DefaultStringValue("File name")
	String libraryEntryName();
	
	@DefaultStringValue("Size")
	String libraryEntrySize();
	
	@DefaultStringValue("Publication date")
	String libraryEntryDate();
	
	
	/**
	 * GenericIncludeFileView
	 */
	@DefaultStringValue("--TODO--")
	String fileFormInfoTitle();
	
	@DefaultStringValue("--TODO--")
	String fileFormInfoText();
	
	@DefaultStringValue("--TODO--")
	String fileDescription();
	
	@DefaultStringValue("Relevance:")
	String starsLabelText();
	
	@DefaultStringValue("Publish")
	String btnPublish();
	
	
	/**
	 * NotesPopup
	 */
	@DefaultStringValue("< Use this space to make your notes during the course. >")
	String notesPopupPlaceholder();
	
	
	/**
	 * MessagePresenter
	 */
	@DefaultStringValue("Messages")
	String messagesTitle();
	
	@DefaultStringValue("Keep track of your conversations with other platform participants")
	String messagesDescription();
	
	@DefaultStringValue("You have no established conversations.")
	String noThreadsMessage();
	
	@DefaultStringValue("Enter your question here and a tutor will contact you soon.")
	String tutorPlaceholderMessage();
	
	
	/**
	 * GenericMessageComposeView
	 */
	@DefaultStringValue("Institution admin")
	String institutionAdminLabel();
	
	@DefaultStringValue("Platform admin")
	String platformAdminLabel();
	
	
	/**
	 * MessageComposePresenter
	 */
	@DefaultStringValue("Message sent successfully!")
	String messageSentSuccess();
	
	@DefaultStringValue("Please fill the message body.")
	String noMessageBodyError();
	
	
	/**
	 * GenericMessageView
	 */
	@DefaultStringValue("Filter conversations...")
	String filterConversationPlaceholder();
	
	@DefaultStringValue("Global class chat:")
	String courseClassChatThreadLabel();
	
	@DefaultStringValue("Direct chat with:")
	String directChatLabel();
	
	@DefaultStringValue("Support chat:")
	String supportChatThreadLabel();
	
	@DefaultStringValue("Support")
	String supportLabel();
	
	@DefaultStringValue("Tutor chat:")
	String tutorChatThreadLabel();
	
	@DefaultStringValue("Tutor")
	String tutorLabel();
	
	@DefaultStringValue("Institution help for class:")
	String institutionSupportChatThreadLabel();
	
	@DefaultStringValue("Platform support:")
	String platformSupportChatThreadLabel();
	
	
	/**
	 * GenericPasswordChangeView
	 */
	@DefaultStringValue("New Password")
	String newPassword();
	
	@DefaultStringValue("Confirm Password")
	String confirmPassword();
	
	@DefaultStringValue("Password change successful!")
	String confirmPasswordChange();
	
	
	/**
	 * GenericProfileView
	 */
	@DefaultStringValue("Edit")
	String editButton();
	
	@DefaultStringValue("Close")
	String closeButton();
	
	@DefaultStringValue("Save")
	String saveButton();
	
	@DefaultStringValue("Cancel")
	String cancelButton();
	
	@DefaultStringValue("OK")
	String okButton();
	
	@DefaultStringValue("Change password")
	String changePasswordButton();
	
	@DefaultStringValue("Send message")
	String sendMessageButton();
	
	@DefaultStringValue("Profile")
	String profileTitle();
	
	@DefaultStringValue("Keep your details updated")
	String profileDescription();
	
	@DefaultStringValue("Enter a name.")
	String missingNameMessage();
	
	@DefaultStringValue("Enter a telephone number.")
	String missingTelephoneMessage();
	
	@DefaultStringValue("Enter a country.")
	String missingCountryMessage();
	
	@DefaultStringValue("Select a state.")
	String selectStateMessage();
	
	@DefaultStringValue("Enter a state.")
	String missingStateMessage();
	
	@DefaultStringValue("Enter a city.")
	String missingCityMessage();
	
	@DefaultStringValue("Enter an address.")
	String missingAddressMessage();
	
	@DefaultStringValue("Enter a postal code.")
	String missingPostalCodeMessage();
	
	@DefaultStringValue("Successfully saved changes!")
	String confirmSaveProfile();
	
	@DefaultStringValue("Errors when saving user.")
	String errorSaveProfile();
	
	@DefaultStringValue("There are errors with your entries.")
	String formContainsErrors();
	
	@DefaultStringValue("User not found.")
	String userNotFound();
	
	@DefaultStringValue("Please complete registration")
	String pleaseCompleteRegistrationMessage();
	
	@DefaultStringValue("Username")
	String usernameLabel();
	
	@DefaultStringValue("Full name")
	String fullnameLabel();
	
	@DefaultStringValue("Email")
	String emailLabel();
	
	@DefaultStringValue("CPF")
	String cpfLabel();
	
	@DefaultStringValue("Company")
	String companyLabel();
	
	@DefaultStringValue("Position")
	String posititonLabel();
	
	@DefaultStringValue("Gender")
	String genderLabel();
	
	@DefaultStringValue("Date of birth")
	String birthDateLabel();
	
	@DefaultStringValue("Receive email communication")
	String receiveEmailCommunicationLabel();
	
	@DefaultStringValue("Telephone")
	String telephoneLabel();
	
	@DefaultStringValue("Country")
	String countryLabel();
	
	@DefaultStringValue("State")
	String stateLabel();
	
	@DefaultStringValue("City")
	String cityLabel();
	
	@DefaultStringValue("Address")
	String address1Label();
	
	@DefaultStringValue("Suite/App")
	String address2Label();
	
	@DefaultStringValue("Postal code")
	String postalCodeLabel();
	
	
	/**
	 * GenericTermsView
	 */
	@DefaultStringValue("Accept")
	String agreeTerms();
	
	@DefaultStringValue("Refuse")
	String refuseTerms();
	
	@DefaultStringValue("Terms of Use")
	String termsTitle();
	
	@DefaultStringValue("Read and sign the terms of use before proceeding")
	String termsDescription();
	
	
	/**
	 * GenericCourseSummaryView
	 */
	@DefaultStringValue("(CANCELLED)")
	String cancelledClassLabel();
	
	@DefaultStringValue("Available")
	String availableClassLabel();
	
	@DefaultStringValue("Awaiting grade")
	String pendingGradeLabel();
	
	@DefaultStringValue("Awaiting enrollment approval")
	String pendingEnrollmentApproval();
	
	@DefaultStringValue("Start class")
	String startCourseLabel();
	
	@DefaultStringValue("Request enrollment")
	String requestEnrollmentLabel();
	
	@DefaultStringValue("Inactive class")
	String inactiveClassLabel();
	
	@DefaultStringValue("Grade")
	String completedCourseGradeLabel();
	
	@DefaultStringValue("on")
	String completedOnToken();
	
	
	/**
	 * GenericWelcomeView
	 */
	@DefaultStringValue("Classes")
	String homeTitle();
	
	@DefaultStringValue("Select a class below")
	String homeDescription();
	
	@DefaultStringValue("You are not enrolled in a class and there are no classes available to request a new registration.")
	String noClassesAvailable();
	
	@DefaultStringValue("Class")
	String courseClass();
	
	/**
	 * Captain
	 */
	@DefaultStringValue("Are you sure you wish to leave the classroom? Your progress since last save may be lost.")
	String leavingTheClassroom();
	
	/**
	 * Util
	 */
	@DefaultStringValue("January")
	String january();
	@DefaultStringValue("February")
	String february();
	@DefaultStringValue("March")
	String march();
	@DefaultStringValue("April")
	String april();
	@DefaultStringValue("May")
	String may();
	@DefaultStringValue("June")
	String june();
	@DefaultStringValue("July")
	String july();
	@DefaultStringValue("August")
	String august();
	@DefaultStringValue("September")
	String september();
	@DefaultStringValue("October")
	String october();
	@DefaultStringValue("November")
	String november();
	@DefaultStringValue("December")
	String december();

	
	/**
	 * Message
	 */
	@DefaultStringValue("Help")
	String composeTitle();
	@DefaultStringValue("Leave your questions or suggestions here.")
	String composeSubTitle();
	@DefaultStringValue("Class")
	String courseClassAdmin();
	@DefaultStringValue("Institution")
	String institutionAdmin();
	@DefaultStringValue("Recipient:")
	String recipient();
	@DefaultStringValue("Message:")
	String message();
	@DefaultStringValue("Back")
	String backButton();
	
	
	/**
	 * Validations
	 */
	@DefaultStringValue("Invalid CPF")
	String invalidCPF();
	
	@DefaultStringValue("CPF already exists")
	String existingCPF();
	
	@DefaultStringValue("Email already exists")
	String existingEmail();
	
	
	/**
	 * FormHelper
	 */
	@DefaultStringValue("Select:")
	String selectboxDefault();
	
	@DefaultStringValue("Female")
	String genderFemale();
	
	@DefaultStringValue("Male")
	String genderMale();
	
	
	/**
	 * EntityState
	 */
	@DefaultStringValue("Active")
	String EntityState_active();
	
	@DefaultStringValue("Inactive")
	String EntityState_inactive();
	
	@DefaultStringValue("Deleted")
	String EntityState_deleted();
	
	
	/**
	 * EnrollmentState
	 */
	@DefaultStringValue("notEnrolled")
	String EnrollmentState_notEnrolled();
	
	@DefaultStringValue("enrolled")
	String EnrollmentState_enrolled();
	
	@DefaultStringValue("requested")
	String EnrollmentState_requested();
	
	@DefaultStringValue("denied")
	String EnrollmentState_denied();
	
	@DefaultStringValue("cancelled")
	String EnrollmentState_cancelled();
	
	
	/**
	 * EnrollmentProgressDescription
	 */
	@DefaultStringValue("Not started")
	String EnrollmentProgressDescription_notStarted();
	
	@DefaultStringValue("In progress")
	String EnrollmentProgressDescription_inProgress();
	
	@DefaultStringValue("Completed")
	String EnrollmentProgressDescription_completed();
	
	
	/**
	 * RegistrationType
	 */
	@DefaultStringValue("Email")
	String RegistrationType_email();
	
	@DefaultStringValue("CPF")
	String RegistrationType_cpf();
	
	@DefaultStringValue("Username")
	String RegistrationType_username();
	
	
	/**
	 * RoleType
	 */
	@DefaultStringValue("Participant")
	String RoleType_user();

	@DefaultStringValue("Course Class Admin")
	String RoleType_courseClassAdmin();

	@DefaultStringValue("Institution Admin")
	String RoleType_institutionAdmin();

	@DefaultStringValue("Platform Admin")
	String RoleType_platformAdmin();

	@DefaultStringValue("Tutor")
	String RoleType_tutor();

	@DefaultStringValue("Observer")
	String RoleType_observer();
	
	
	/**
	 * Errors 404
	 */
	@DefaultStringValue("Person not found.")
	String personNotFound();
	@DefaultStringValue("Repository not found.")
	String repositoryNotFound();
	@DefaultStringValue("Class not found.")
	String classNotFound();
	@DefaultStringValue("Person or institution not found.")
	String personOrInstitutionNotFound();

	
	/**
	 * Errors 401
	 */
	@DefaultStringValue("Authentication failed.")
	String authenticationFailed();
	@DefaultStringValue("You must authenticate to access this path.")
	String mustAuthenticate();
	@DefaultStringValue("It wasn't possible to change your password.")
	String passwordChangeFailed();
	@DefaultStringValue("Unauthorized attempt to change the password.")
	String passwordChangeDenied();
	@DefaultStringValue("Unauthorized attempt to update a class without platformAdmin or institutionAdmin rights.")
	String classNoRights();
	@DefaultStringValue("Unauthorized attempt to generate the class' certificates without admin rights.")
	String unauthorizedAccessReport();
	@DefaultStringValue("Access denied.")
	String accessDenied();
	
	/**
	 * Errors 403
	 * 
	 */
	@DefaultStringValue("For security reasons, you must update your password.")
	String forcedPasswordChange();
	
	/**
	 * Errors 409
	 */
	@DefaultStringValue("A course with this name or code already exists.")
	String courseAlreadyExists();
	@DefaultStringValue("A course version with this name or code already exists.")
	String courseVersionAlreadyExists();
	@DefaultStringValue("A class with this name already exists.")
	String courseClassAlreadyExists();
	@DefaultStringValue("Invalid input value")
	String invalidValue();
	@DefaultStringValue("Constraint Violated (uuid or name).")
	String constraintViolatedUUIDName();
	@DefaultStringValue("User is already enrolled on the selected class.")
	String userAlreadyEnrolledInClass();
	@DefaultStringValue("Could not complete the request. Check the amount of enrollments available in this class.")
	String tooManyEnrollments();
	@DefaultStringValue("Enrollment for this class is only available on a Parent version.")
	String cannotEnrollOnChildVersion();
	
	/**
	 * Errors 500
	 */
	
	@DefaultStringValue("Error generating the report.")
	String errorGeneratingReport();
	
	@DefaultStringValue("Error checking for certificates.")
	String errorCheckingCerts();


	
}