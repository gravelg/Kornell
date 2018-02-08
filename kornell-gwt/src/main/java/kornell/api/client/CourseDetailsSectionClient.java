package kornell.api.client;

import kornell.core.entity.CourseDetailsSection;

public class CourseDetailsSectionClient extends RESTClient {

    private String courseDetailsSectionUUID;

    public CourseDetailsSectionClient(String courseDetailsSectionUUID) {
        this.courseDetailsSectionUUID = courseDetailsSectionUUID;
    }

    public void update(CourseDetailsSection courseDetailsSection, Callback<CourseDetailsSection> cb) {
        PUT("courseDetailsSections", courseDetailsSection.getUUID()).withContentType(CourseDetailsSection.TYPE)
                .withEntityBody(courseDetailsSection).go(cb);
    }

    public void delete(Callback<CourseDetailsSection> cb) {
        DELETE("courseDetailsSections", courseDetailsSectionUUID).go(cb);
    }

}
