package kornell.api.client;

import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.entity.CourseDetailsHint;
import kornell.core.to.CourseDetailsHintsTO;

public class CourseDetailsHintsClient extends RESTClient {

	public CourseDetailsHintsClient() {
	}

	public void findByEntityTypeAndUUID(CourseDetailsEntityType entityType, String entityUUID, Callback<CourseDetailsHintsTO> cb) {
		GET("courseDetailsHints",entityType.toString(), entityUUID).go(cb);
	}
	
	public void create(CourseDetailsHint courseDetailsHint, Callback<CourseDetailsHint> callback) {
		POST("courseDetailsHints").withContentType(CourseDetailsHint.TYPE).withEntityBody(courseDetailsHint).go(callback);
	}
	
}
