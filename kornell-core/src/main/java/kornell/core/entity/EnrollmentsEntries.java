package kornell.core.entity;

import java.util.Map;

public interface EnrollmentsEntries {
    public static String TYPE = EntityFactory.PREFIX + "enrollmentsentries+json";

    String getEnrollmentUUID();
    void setEnrollmentUUID(String enrollmentUUID);

    Map<String,EnrollmentEntries> getEnrollmentEntriesMap();
    void setEnrollmentEntriesMap(Map<String,EnrollmentEntries> enrollmentsEntries);
}
