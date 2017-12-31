package kornell.core.to;

import java.util.List;

import kornell.core.entity.Track;

public interface TrackTO {

    public static String TYPE = TOFactory.PREFIX + "track+json";

    Track getTrack();
    void setTrack(Track track);

    List<TrackItemTO> getTrackItems();
    void setTrackItems(List<TrackItemTO> trackItems);
}
