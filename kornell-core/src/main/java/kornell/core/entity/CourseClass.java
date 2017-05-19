package kornell.core.entity;

import java.math.BigDecimal;
import java.util.Date;

public interface CourseClass extends LearningEntity {
	public static String TYPE = EntityFactory.PREFIX + "courseClass+json";
	
	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);

	String getName();
	void setName(String name);

	String getCourseVersionUUID();
	void setCourseVersionUUID(String courseVersionUUID);
	
	BigDecimal getRequiredScore();
	void setRequiredScore(BigDecimal requiredScore);
	
	Boolean isPublicClass();
	void setPublicClass(Boolean publicClass);
	
	Boolean isOverrideEnrollments();
	void setOverrideEnrollments(Boolean overrideEnrollments);
	
	Boolean isInvisible();
	void setInvisible(Boolean invisible);
	
	Integer getMaxEnrollments();
	void setMaxEnrollments(Integer maxEnrollments);
	
	Date getCreatedAt();
	void setCreatedAt(Date createdAt);
	
	String getCreatedBy();
	void setCreatedBy(String createdBy);
	
	EntityState getState();
	void setState(EntityState state);
	
	RegistrationType getRegistrationType();
	void setRegistrationType(RegistrationType registrationType);
	
	String getInstitutionRegistrationPrefixUUID();
	void setInstitutionRegistrationPrefixUUID(String institutionRegistrationPrefixUUID);
	
	Boolean isCourseClassChatEnabled();
	void setCourseClassChatEnabled(Boolean courseClassChatEnabled);
	
	Boolean isChatDockEnabled();
	void setChatDockEnabled(Boolean chatDockEnabled);
	
	Boolean isAllowBatchCancellation();
	void setAllowBatchCancellation(Boolean allowBatchCancellation);
	
	Boolean isTutorChatEnabled();
	void setTutorChatEnabled(Boolean tutorChatEnabled);
	
	Boolean isApproveEnrollmentsAutomatically();
	void setApproveEnrollmentsAutomatically(Boolean approveEnrollmentsAutomatically);
	
	Date getStartDate();
	void setStartDate(Date startDate);
	
	String getPagseguroId();
	void setPagseguroId(String pagseguroId);
	
}
