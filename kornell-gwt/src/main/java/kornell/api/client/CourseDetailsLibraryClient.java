package kornell.api.client;

import kornell.core.entity.CourseDetailsLibrary;

public class CourseDetailsLibraryClient extends RESTClient {

    private String courseDetailsLibraryUUID;

    public CourseDetailsLibraryClient(String courseDetailsLibraryUUID) {
        this.courseDetailsLibraryUUID = courseDetailsLibraryUUID;
    }

    public void update(CourseDetailsLibrary courseDetailsLibrary, Callback<CourseDetailsLibrary> cb) {
        PUT("courseDetailsLibraries", courseDetailsLibrary.getUUID()).withContentType(CourseDetailsLibrary.TYPE)
                .withEntityBody(courseDetailsLibrary).go(cb);
    }

    public void delete(Callback<CourseDetailsLibrary> cb) {
        DELETE("courseDetailsLibraries", courseDetailsLibraryUUID).go(cb);
    }

}
