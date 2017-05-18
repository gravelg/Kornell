package kornell.api.client;

import kornell.core.entity.Course;

public class CourseClient extends RESTClient {

	private String courseUUID;

	public CourseClient(String courseUUID) {
		this.courseUUID = courseUUID;
	}

	public void get(Callback<Course> cb) {
		GET("courses",courseUUID).go(cb);
	}

	public void update(Course course, Callback<Course> cb) {
		PUT("courses",course.getUUID()).withContentType(Course.TYPE).withEntityBody(course).go(cb);
	}

	public void delete(Callback<Course> cb) {
		DELETE("courses",courseUUID).go(cb);
	}

	public void copy(Callback<Course> cb) {
		POST("courses",courseUUID,"copy").go(cb);
	}
	
}
