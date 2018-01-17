package kornell.core.to;

import kornell.core.entity.Course;

public interface CourseTO {
    public static final String TYPE = TOFactory.PREFIX + "course+json";

    Course getCourse();
    void setCourse(Course course);

    Integer getCourseVersionsCount();
    void setCourseVersionsCount(Integer courseVersionsCount);

}
