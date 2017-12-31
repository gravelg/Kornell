package kornell.core.to;

import kornell.core.entity.TrackItem;

public interface TrackItemTO {

    public static String TYPE = TOFactory.PREFIX + "trackItem+json";

    TrackItem getTrackItem();
    void setTrackItem(TrackItem trackItem);

    CourseVersionTO getCourseVersionTO();
    void setCourseVersionTO(CourseVersionTO courseVersionTO);

    TrackItem getParent();
    void setParent(TrackItem trackItem);
}
