package kornell.core.entity;

public interface CourseDetailsSection extends AssetEntity {
	public static String TYPE = EntityFactory.PREFIX + "courseDetailsSection+json";

	String getText();
	void setText(String text);
	
}
