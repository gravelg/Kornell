package kornell.core.to;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;

public interface CourseClassTO extends CourseDetailsTO {
	public static final String TYPE = TOFactory.PREFIX+"courseclass+json";
	
	CourseClass getCourseClass();
	void setCourseClass(CourseClass c);
	
	CourseVersionTO getCourseVersionTO();
	void setCourseVersionTO(CourseVersionTO t);
	
	String getRegistrationPrefix();
	void setRegistrationPrefix(String registrationPrefix);
	
	boolean isEnrolledOnCourseVersion();
	void setEnrolledOnCourseVersion(boolean enrolledOnCourseVersion);
	
	Enrollment getEnrollment();
	void setEnrollment(Enrollment e);
	
}
