package kornell.core.to;

import kornell.core.entity.Enrollment;

public interface EnrollmentTO {
    public static String TYPE = TOFactory.PREFIX + "enrollment+json";

    Enrollment getEnrollment();
    void setEnrollment(Enrollment enrollment);

    String getPersonUUID();
    void setPersonUUID(String personUUID);

    String getFullName();
    void setFullName(String fullName);

    String getUsername();
    void setUsername(String username);

}
