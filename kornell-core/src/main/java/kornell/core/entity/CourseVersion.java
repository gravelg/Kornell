package kornell.core.entity;

import java.util.Date;

public interface CourseVersion extends AssetsEntity {
    public static String TYPE = EntityFactory.PREFIX + "courseVersion+json";
    
	String getCourseUUID();
	void setCourseUUID(String courseUUID);
	
	String getName();
	void setName(String name);
	
	String getDistributionPrefix();
	void setDistributionPrefix(String distributionPrefix);
	
	Date getVersionCreatedAt();
	void setVersionCreatedAt(Date versionCreatedAt);
	
	ContentSpec getContentSpec();
	void setContentSpec(ContentSpec contentSpec);
	
	boolean isDisabled();
	void setDisabled(boolean disabled);
	
	String getParentVersionUUID();
	void setParentVersionUUID(String parentVersionUUID);
	
	Integer getInstanceCount();
	void setInstanceCount(Integer instanceCount);
	
	String getLabel();
	void setLabel(String label);
	
}
