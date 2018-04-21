package kornell.core.entity.role;

public interface CourseClassAdminRole extends Role {
    String getCourseClassUUID();
    void setCourseClassUUID(String courseClassUUID);
}
