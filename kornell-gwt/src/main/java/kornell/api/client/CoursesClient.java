package kornell.api.client;

import kornell.core.entity.Course;
import kornell.core.to.CoursesTO;

public class CoursesClient extends RESTClient {

	public void get(Callback<CoursesTO> callback) {
		get(true, ""+Integer.MAX_VALUE, "1", "", callback);
	}

	public void get(boolean fetchChildCourses, Callback<CoursesTO> callback) {
		get(fetchChildCourses, ""+Integer.MAX_VALUE, "1", "", callback);
	}

	public void get(boolean fetchChildCourses, String ps, String pn, String searchTerm, Callback<CoursesTO> callback) {
		GET("/courses?fetchChildCourses="+fetchChildCourses + "&ps=" + ps + "&pn=" + pn + "&searchTerm=" + searchTerm).go(callback);
	}
	
	public void create(Course course, Callback<Course> callback) {
		POST("/courses").withContentType(Course.TYPE).withEntityBody(course).go(callback);
	}
	
}
