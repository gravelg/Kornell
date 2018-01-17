package kornell.core.to;

import java.util.List;

public interface CoursesTO extends Page {
    public static final String TYPE = TOFactory.PREFIX + "courses+json";

    List<CourseTO> getCourses();
    void setCourses(List<CourseTO> courses);

}
