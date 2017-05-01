package kornell.api.client;

import java.util.HashMap;
import java.util.Map;

import kornell.core.entity.ActomEntries;
import kornell.core.entity.CertificateDetails;
import kornell.core.entity.ChatThread;
import kornell.core.entity.ContentRepository;
import kornell.core.entity.Course;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.Enrollment;
import kornell.core.entity.EnrollmentEntries;
import kornell.core.entity.EnrollmentsEntries;
import kornell.core.entity.Institution;
import kornell.core.entity.People;
import kornell.core.entity.Person;
import kornell.core.entity.Roles;
import kornell.core.error.KornellErrorTO;
import kornell.core.lom.Contents;
import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CourseClassesTO;
import kornell.core.to.CourseVersionTO;
import kornell.core.to.CourseVersionsTO;
import kornell.core.to.CoursesTO;
import kornell.core.to.DashboardLeaderboardTO;
import kornell.core.to.EnrollmentLaunchTO;
import kornell.core.to.EnrollmentsTO;
import kornell.core.to.EntityChangedEventsTO;
import kornell.core.to.InstitutionEmailWhitelistTO;
import kornell.core.to.InstitutionHostNamesTO;
import kornell.core.to.InstitutionRegistrationPrefixesTO;
import kornell.core.to.LibraryFilesTO;
import kornell.core.to.PeopleTO;
import kornell.core.to.PersonTO;
import kornell.core.to.RolesTO;
import kornell.core.to.SimplePeopleTO;
import kornell.core.to.SimplePersonTO;
import kornell.core.to.TokenTO;
import kornell.core.to.UnreadChatThreadsTO;
import kornell.core.to.UserHelloTO;
import kornell.core.to.UserInfoTO;

public class MediaTypes {

	static final MediaTypes instance = new MediaTypes();

	Map<String, Class<?>> type2class = new HashMap<String, Class<?>>();
	Map<Class<?>, String> class2type = new HashMap<Class<?>, String>();

	public MediaTypes() {
		//TODO: Consider using a GWT Generator instead of manual declaration
		registerEntities();
		registerTOs();
	}

	private void registerEntities() {
		register(Course.TYPE, Course.class);
		register(People.TYPE, People.class);
		register(Person.TYPE, Person.class);
		register(CourseClass.TYPE, CourseClass.class);
		register(CourseVersion.TYPE, CourseVersion.class);		
		register(Roles.TYPE, Roles.class);
		register(Institution.TYPE, Institution.class);
		register(Contents.TYPE, Contents.class);
		register(Enrollment.TYPE, Enrollment.class);
		register(ActomEntries.TYPE, ActomEntries.class);
		register(ChatThread.TYPE, ChatThread.class);
		register(EnrollmentsEntries.TYPE, EnrollmentsEntries.class);
		register(EnrollmentEntries.TYPE, EnrollmentEntries.class);
		register(ContentRepository.TYPE, ContentRepository.class);
		register(CertificateDetails.TYPE, CertificateDetails.class);		
	}

	private void registerTOs() {
		register(PeopleTO.TYPE, PeopleTO.class);
		register(PersonTO.TYPE, PersonTO.class);
		register(CoursesTO.TYPE, CoursesTO.class);
		register(CourseClassesTO.TYPE, CourseClassesTO.class);
		register(CourseClassTO.TYPE, CourseClassTO.class);		
		register(CourseVersionsTO.TYPE, CourseVersionsTO.class);
		register(CourseVersionTO.TYPE, CourseVersionTO.class);
		register(UserInfoTO.TYPE, UserInfoTO.class);
		register(UserHelloTO.TYPE, UserHelloTO.class);		
		register(RolesTO.TYPE, RolesTO.class);
		register(EnrollmentsTO.TYPE, EnrollmentsTO.class);		
		register(LibraryFilesTO.TYPE, LibraryFilesTO.class);
		register(UnreadChatThreadsTO.TYPE, UnreadChatThreadsTO.class);
		register(ChatThreadMessagesTO.TYPE, ChatThreadMessagesTO.class);
		register(InstitutionRegistrationPrefixesTO.TYPE,
				InstitutionRegistrationPrefixesTO.class);
		register(InstitutionHostNamesTO.TYPE, InstitutionHostNamesTO.class);
		register(KornellErrorTO.TYPE, KornellErrorTO.class);
		register(EnrollmentLaunchTO.TYPE, EnrollmentLaunchTO.class);
		// When auth filter sends 401, it adds the charset and we can't do
		// anything about it
		register(KornellErrorTO.TYPE + ";charset=utf-8", KornellErrorTO.class);
		register(TokenTO.TYPE, TokenTO.class);
		register(SimplePeopleTO.TYPE, SimplePeopleTO.class);
		register(SimplePersonTO.TYPE, SimplePersonTO.class);
		register(InstitutionEmailWhitelistTO.TYPE, InstitutionEmailWhitelistTO.class);
		register(EntityChangedEventsTO.TYPE, EntityChangedEventsTO.class);
		register(DashboardLeaderboardTO.TYPE, DashboardLeaderboardTO.class);
	}

	private void register(String type, Class<?> clazz) {
		type2class.put(type.toLowerCase(), clazz);
		class2type.put(clazz, type.toLowerCase());
	}

	public static MediaTypes get() {
		return instance;
	}

	public Class<?> classOf(String type) {
		return type2class.get(type);
	}

	public String typeOf(Class<?> clazz) {
		return class2type.get(clazz);
	}

	public boolean containsType(String type) {
		return type2class.containsKey(type);
	}

	public boolean containsClass(Class<?> clazz) {
		return class2type.containsKey(clazz);
	}
}
