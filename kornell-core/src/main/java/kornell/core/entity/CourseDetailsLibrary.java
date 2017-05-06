package kornell.core.entity;

import java.util.Date;

public interface CourseDetailsLibrary extends AssetEntity {
	public static String TYPE = EntityFactory.PREFIX + "courseDetailsLibrary+json";

	String getDescription();
	void setDescription(String description);
	
	Integer getSize();
	void setSize(Integer size);
	
	String getPath();
	void setPath(String path);
	
	Date getUploadDate();
	void setUploadDate(Date uploadDate);
	
	String getFontAwesomeClassName();
	void setFontAwesomeClassName(String fontAwesomeClassName);
}
