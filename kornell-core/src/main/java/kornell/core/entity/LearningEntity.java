package kornell.core.entity;

public interface LearningEntity extends Named {
	public static String TYPE = EntityFactory.PREFIX + "learningEntity+json";
	
	String getThumbUrl();
	void setThumbUrl(String thumbUrl);
	
	EntityState getState();
	void setState(EntityState state);
	
}
