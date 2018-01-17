package kornell.core.entity;

public interface TrackEnrollment extends Entity {
    public static String TYPE = EntityFactory.PREFIX + "trackEnrollment+json";

    String getPersonUUID();
    void setPersonUUID(String personUUID);

    String getTrackUUID();
    void setTrackUUID(String trackUUID);
}
