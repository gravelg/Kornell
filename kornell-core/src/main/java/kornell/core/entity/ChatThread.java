package kornell.core.entity;

import java.util.Date;

public interface ChatThread extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "chatThread+json";
	
	Date getCreatedAt();
	void setCreatedAt(Date createdAt);

	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
	
	String getCourseClassUUID();
	void setCourseClassUUID(String courseClassUUID);
	
	String getPersonUUID();
	void setPersonUUID(String personUUID);
	
	String getThreadType();
	void setThreadType(String threadType);
	
	Boolean isActive();
	void setActive(Boolean active);
	
	Date getLastSentAt();
	void setLastSentAt(Date lastSentAt);
}
