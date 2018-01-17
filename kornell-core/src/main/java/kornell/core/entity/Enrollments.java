package kornell.core.entity;

import java.util.List;

public interface Enrollments {
    public static final String TYPE = EntityFactory.PREFIX + "enrollments+json";
    List<Enrollment> getEnrollments();
    void setEnrollments(List<Enrollment> enrollments);

}
