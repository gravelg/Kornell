package kornell.core.to;

import java.util.List;

import kornell.core.entity.CourseDetailsLibrary;

public interface CourseDetailsLibrariesTO {
    public static final String TYPE = TOFactory.PREFIX + "courseDetailsLibraries+json";

    List<CourseDetailsLibrary> getCourseDetailsLibraries();
    void setCourseDetailsLibraries(List<CourseDetailsLibrary> courseDetailsLibraries);

}
