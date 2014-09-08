package kornell.core.entity;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

public interface EntityFactory extends AutoBeanFactory {
	public static String PREFIX = "application/vnd.kornell.v1.entity.";

	AutoBean<Person> newPerson();

	AutoBean<People> newPeople();

	AutoBean<Principal> newPrincipal();

	AutoBean<Course> newCourse();

	AutoBean<Enrollment> enrollment();

	AutoBean<Enrollments> newEnrollments();

	AutoBean<Institution> newInstitution();

	AutoBean<Registration> newRegistration();

	AutoBean<Registrations> newRegistrations();

	AutoBean<Role> newRole();

	AutoBean<Roles> newRoles();

	AutoBean<UserRole> newUserRole();

	AutoBean<PlatformAdminRole> newPlatformAdminRole();

	AutoBean<InstitutionAdminRole> newInstitutionAdminRole();

	AutoBean<CourseClassAdminRole> newCourseClassAdminRole();

	AutoBean<CourseVersion> newCourseVersion();
	
	AutoBean<CourseClass> newCourseClass();
	
	AutoBean<WebRepository> newWebReposiory();
	
	AutoBean<ActomEntries> newActomEntries();

	AutoBean<EnrollmentProgress> newEnrollmentProgress();
	
	AutoBean<ContentStore> newContentStore();
	
	AutoBean<ChatThread> newChatThread();
	
	AutoBean<ChatThreadParticipant> newChatThreadParticipant();
	
	AutoBean<ChatThreadMessage> newChatThreadMessage();
}
