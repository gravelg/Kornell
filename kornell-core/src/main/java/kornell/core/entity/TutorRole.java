package kornell.core.entity;

public interface TutorRole extends Role {
    String getCourseClassUUID();
    void setCourseClassUUID(String courseClassUUID);
}
