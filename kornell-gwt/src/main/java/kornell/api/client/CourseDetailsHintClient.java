package kornell.api.client;

import kornell.core.entity.CourseDetailsHint;

public class CourseDetailsHintClient extends RESTClient {

    private String courseDetailsHintUUID;

    public CourseDetailsHintClient(String courseDetailsHintUUID) {
        this.courseDetailsHintUUID = courseDetailsHintUUID;
    }

    public void update(CourseDetailsHint courseDetailsHint, Callback<CourseDetailsHint> cb) {
        PUT("courseDetailsHints", courseDetailsHint.getUUID()).withContentType(CourseDetailsHint.TYPE)
                .withEntityBody(courseDetailsHint).go(cb);
    }

    public void delete(Callback<CourseDetailsHint> cb) {
        DELETE("courseDetailsHints", courseDetailsHintUUID).go(cb);
    }

}
