package kornell.core.entity;

public interface PostbackConfig extends Entity {
	public static final String TYPE = EntityFactory.PREFIX + "postbackConfig+json";

	String getInstitutionUUID();
	void setInstitutionUUID(String institutionUUID);
	
	PostbackType getPostbackType();
	void setPostbackType(PostbackType postbackType);
	
	String getContents();
	void setContents(String contents);
}
