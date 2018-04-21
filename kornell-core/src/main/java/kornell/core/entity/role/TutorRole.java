package kornell.core.entity.role;

public interface TutorRole extends Role {
    String getCourseClassUUID();
    void setCourseClassUUID(String courseClassUUID);
}
