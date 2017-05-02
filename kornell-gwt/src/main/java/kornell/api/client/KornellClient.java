package kornell.api.client;

import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.util.ClientProperties;

public class KornellClient extends RESTClient implements LogoutEventHandler {

	protected KornellClient() {}

	public UserClient user() {
		// TODO: Consider lifecycle
		return new UserClient();
	}

	public InstitutionsClient institutions() {
		return new InstitutionsClient();
	}
	
	public ReportClient report() {
		return new ReportClient();
	}
	
	public EmailClient email() {
		return new EmailClient();
	}

	public AssetsClient assets() {
		return new AssetsClient();
	}

	public CertificatesDetailsClient certificatesDetails() {
		return new CertificatesDetailsClient();
	}

	public CertificateDetailsClient certificateDetails(String uuid) {
		return new CertificateDetailsClient(uuid);
	}

	public CourseDetailsHintsClient courseDetailsHints() {
		return new CourseDetailsHintsClient();
	}

	public CourseDetailsHintClient courseDetailsHint(String uuid) {
		return new CourseDetailsHintClient(uuid);
	}

	public CourseDetailsLibrariesClient courseDetailsLibraries() {
		return new CourseDetailsLibrariesClient();
	}

	public CourseDetailsLibraryClient courseDetailsLibrary(String uuid) {
		return new CourseDetailsLibraryClient(uuid);
	}

	public CourseDetailsSectionsClient courseDetailsSections() {
		return new CourseDetailsSectionsClient();
	}

	public CourseDetailsSectionClient courseDetailsSection(String uuid) {
		return new CourseDetailsSectionClient(uuid);
	}

	public CoursesClient courses() {
		return new CoursesClient();
	}

	public CourseClient course(String uuid) {
		return new CourseClient(uuid);
	}

	public CourseVersionsClient courseVersions() {
		return new CourseVersionsClient();
	}

	public CourseVersionClient courseVersion(String uuid) {
		return new CourseVersionClient(uuid);
	}

	public CourseClassesClient courseClasses() {
		return new CourseClassesClient();
	}

	public InstitutionClient institution(String uuid) {
		return new InstitutionClient(uuid);
	}

	public PeopleClient people() {
		return new PeopleClient();
	}

	public PersonClient person(String personUUID){
		return new PersonClient(personUUID);
	}

	public CourseClassClient courseClass(String courseClassUUID) {
		return new CourseClassClient(courseClassUUID);
	}

	public EnrollmentClient enrollment(String enrollmentUUID){
		return new EnrollmentClient(enrollmentUUID);
	}

	public EnrollmentsClient enrollments(){
		return new EnrollmentsClient();
	}

	public ChatThreadsClient chatThreads() {
		return new ChatThreadsClient();
	}
	
	public RepositoryClient repository() {
		return new RepositoryClient();
	}

	static final EventsClient eventsClient = new EventsClient();
	public EventsClient events() {
		return eventsClient;
	}

	@SuppressWarnings("rawtypes")
	// TODO: Remove raw type
	public void check(String src, Callback callback) {
		HEAD(src).go(callback);
	}

	@Override
	public void onLogout() {
		forgetCredentials();
	}

	private void forgetCredentials() {
		ClientProperties.remove(ClientProperties.X_KNL_TOKEN);
	}

}
