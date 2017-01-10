package kornell.core.entity;

public interface CertificateDetails extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "certificateDetails+json";

	String getBgImage();
	void setBgImage(String bgImage);

	CertificateType getCertificateType();
	void setCertificateType(CertificateType certificateType);

	CourseDetailsEntityType getEntityType();
	void setEntityType(CourseDetailsEntityType entityType);

	String getEntityUUID();
	void setEntityUUID(String entityUUID);
}
