package kornell.core.to;

import kornell.core.entity.ContentSpec;

public interface SimpleCVCTO {
	public static String TYPE = TOFactory.PREFIX + "simpleCVC+json";
	
	String getName();
	void setName(String name);
	
	String getDescription();
	void setDescription(String description);
	
	ContentSpec getContentSpec();
	void setContentSpec(ContentSpec contentSpec);
	
	String getCourseDuration();
	void setCourseDuration(String courseDuration);
	
	boolean isGenerateCertificate();
	void setGenerateCertificate(boolean generateCertificate);
	
	boolean isMultimediaContent();
	void setMultimediaContent(boolean multimediaContent);
	
	boolean isPublicClass();
	void setPublicClass(boolean publicClass);
	
	boolean isAutoApprove();
	void setAutoApprove(boolean autoApprove);
}
