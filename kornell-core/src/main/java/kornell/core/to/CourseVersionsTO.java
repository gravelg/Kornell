package kornell.core.to;

import java.util.List;

public interface CourseVersionsTO extends Page {
    public static final String TYPE = TOFactory.PREFIX + "courseVersions+json";

    List<CourseVersionTO> getCourseVersionTOs();
    void setCourseVersionTOs(List<CourseVersionTO> courseVersionTOs);

}
