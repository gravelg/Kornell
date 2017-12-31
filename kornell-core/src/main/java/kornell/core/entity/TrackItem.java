package kornell.core.entity;

import java.util.Date;

public interface TrackItem extends Entity {
	public static String TYPE = EntityFactory.PREFIX + "trackItem+json";

	String getCourseVersionUUID();
	void setCourseVersionUUID(String courseVersionUUID);

	String getTrackUUID();
	void setTrackUUID(String trackUUID);

	String getParentUUID();
	void setParentUUID(String parentUUID);

	Integer getOrder();
	void setOrder(Integer order);

	Boolean isHavingPreRequirements();
	void setHavingPreRequirements(Boolean havingPreRequirements);

	Date getStartDate();
	void setStartDate(Date startDate);
}
