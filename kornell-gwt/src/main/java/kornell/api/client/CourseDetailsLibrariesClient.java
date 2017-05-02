package kornell.api.client;

import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.entity.CourseDetailsLibrary;
import kornell.core.to.CourseDetailsLibrariesTO;

public class CourseDetailsLibrariesClient extends RESTClient {

	public CourseDetailsLibrariesClient() {
	}

	public void findByEntityTypeAndUUID(CourseDetailsEntityType entityType, String entityUUID, Callback<CourseDetailsLibrariesTO> cb) {
		GET("courseDetailsLibraries",entityType.toString(), entityUUID).go(cb);
	}
	
	public void create(CourseDetailsLibrary courseDetailsLibrary, Callback<CourseDetailsLibrary> callback) {
		POST("courseDetailsLibraries").withContentType(CourseDetailsLibrary.TYPE).withEntityBody(courseDetailsLibrary).go(callback);
	}
	
}
