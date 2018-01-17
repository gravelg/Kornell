package kornell.core.event;


public interface ActomEntered extends Event {
    public static final String TYPE = EventFactory.PREFIX+"ActomEntered+json";

    String getEnrollmentUUID();
    void setEnrollmentUUID(String enrollmentUUID);

    String getActomKey();
    void setActomKey(String actomKey);
}
