package kornell.core.to;

import java.util.List;

import kornell.core.entity.CourseDetailsHint;
import kornell.core.entity.CourseDetailsLibrary;
import kornell.core.entity.CourseDetailsSection;

public interface CourseDetailsTO {

    List<CourseDetailsHint> getCourseDetailsHints();
    void setCourseDetailsHints(List<CourseDetailsHint> courseDetailsHints);

    List<CourseDetailsSection> getCourseDetailsSections();
    void setCourseDetailsSections(List<CourseDetailsSection> courseDetailsSection);

    List<CourseDetailsLibrary> getCourseDetailsLibraries();
    void setCourseDetailsLibraries(List<CourseDetailsLibrary> courseDetailsLibraries);
}
