package kornell.api.client;

import kornell.core.entity.CourseVersion;
import kornell.core.to.CourseVersionTO;

public class CourseVersionClient extends RESTClient {

	private String courseVersionUUID;

	public CourseVersionClient(String courseVersionUUID) {
		this.courseVersionUUID = courseVersionUUID;
	}

	public void get(Callback<CourseVersionTO> cb) {
		GET("courseVersions",courseVersionUUID).go(cb);
	}

	public void update(CourseVersion courseVersion, Callback<CourseVersion> cb) {
		PUT("courseVersions",courseVersion.getUUID()).withContentType(CourseVersion.TYPE).withEntityBody(courseVersion).go(cb);
	}

	public void delete(Callback<CourseVersion> cb) {
		DELETE("courseVersions",courseVersionUUID).go(cb);
	}
	
	public void getContentUploadURL(Callback<String> callback) {
		GET("courseVersions", courseVersionUUID, "contentUploadUrl").go(callback);
	}
}
