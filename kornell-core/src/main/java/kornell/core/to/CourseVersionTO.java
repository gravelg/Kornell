package kornell.core.to;

import kornell.core.entity.CourseVersion;

public interface CourseVersionTO {
	public static final String TYPE = TOFactory.PREFIX+"courseVersion+json";
	
	CourseVersion getCourseVersion();
	void setCourseVersion(CourseVersion c);
	
	CourseTO getCourseTO();
	void setCourseTO(CourseTO c);
	
	String getDistributionURL();
	void setDistributionURL(String getDistributionURL);
	
	Integer getCourseClassesCount();	
	void setCourseClassesCount(Integer courseClassesCount);
}