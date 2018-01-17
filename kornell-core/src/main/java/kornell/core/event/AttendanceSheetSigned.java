package kornell.core.event;


public interface AttendanceSheetSigned extends Event {
    public static final String TYPE = EventFactory.PREFIX+"AttendanceSheetSigned+json";

    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);

    String getPersonUUID();
    void setPersonUUID(String personUUID);

}
