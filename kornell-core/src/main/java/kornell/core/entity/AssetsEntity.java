package kornell.core.entity;

public interface AssetsEntity extends Entity{
	public static String TYPE = EntityFactory.PREFIX + "assetsEntity+json";
	
	String getThumbUrl();
	void setThumbUrl(String thumbUrl);
	
}
