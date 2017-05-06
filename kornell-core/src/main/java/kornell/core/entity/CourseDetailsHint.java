package kornell.core.entity;

public interface CourseDetailsHint extends AssetEntity {
	public static String TYPE = EntityFactory.PREFIX + "courseDetailsHint+json";

	String getText();
	void setText(String text);
	
	String getFontAwesomeClassName();
	void setFontAwesomeClassName(String fontAwesomeClassName);
	
}
