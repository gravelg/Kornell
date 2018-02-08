package kornell.api.client;

import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.entity.CourseDetailsSection;
import kornell.core.to.CourseDetailsSectionsTO;

public class CourseDetailsSectionsClient extends RESTClient {

    public CourseDetailsSectionsClient() {
    }

    public void findByEntityTypeAndUUID(CourseDetailsEntityType entityType, String entityUUID,
            Callback<CourseDetailsSectionsTO> cb) {
        GET("courseDetailsSections", entityType.toString(), entityUUID).go(cb);
    }

    public void create(CourseDetailsSection courseDetailsSection, Callback<CourseDetailsSection> callback) {
        POST("courseDetailsSections").withContentType(CourseDetailsSection.TYPE).withEntityBody(courseDetailsSection)
                .go(callback);
    }

}
