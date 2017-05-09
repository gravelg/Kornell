package kornell.core.to;

import kornell.core.entity.Course;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseVersion;

public interface SimpleCVCResponseTO {
	public static String TYPE = TOFactory.PREFIX + "simpleCVC+json";
	
	Course getCourse();
	void setCourse(Course course);
	
	CourseVersion getCourseVersion();
	void setCourseVersion(CourseVersion courseVersion);
	
	CourseClass getCourseClass();
	void setCourseClass(CourseClass courseClass);
}
