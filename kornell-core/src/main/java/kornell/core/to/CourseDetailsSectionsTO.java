package kornell.core.to;

import java.util.List;

import kornell.core.entity.CourseDetailsSection;

public interface CourseDetailsSectionsTO {
    public static final String TYPE = TOFactory.PREFIX + "courseDetailsSections+json";

    List<CourseDetailsSection> getCourseDetailsSections();
    void setCourseDetailsSections(List<CourseDetailsSection> courseDetailsSections);

}
