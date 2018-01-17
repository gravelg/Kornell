package kornell.core.to;

import java.util.List;

import kornell.core.entity.CourseDetailsHint;

public interface CourseDetailsHintsTO {
    public static final String TYPE = TOFactory.PREFIX + "courseDetailsHints+json";

    List<CourseDetailsHint> getCourseDetailsHints();
    void setCourseDetailsHints(List<CourseDetailsHint> courseDetailsHints);

}
