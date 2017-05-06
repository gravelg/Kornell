package kornell.core.entity;

public interface ThumbnailEntity extends Entity{
	public static String TYPE = EntityFactory.PREFIX + "thumbnailEntity+json";
	
	String getThumbUrl();
	void setThumbUrl(String thumbUrl);
	
}
