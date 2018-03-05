package kornell.api.client;

import kornell.core.entity.Course;
import kornell.core.to.CourseClassTO;
import kornell.core.to.CoursesTO;

public class CoursesClient extends RESTClient {

    public void get(Callback<CoursesTO> callback) {
        get(true, ""+Integer.MAX_VALUE, "1", "", callback);
    }

    public void get(boolean fetchChildCourses, Callback<CoursesTO> callback) {
        get(fetchChildCourses, ""+Integer.MAX_VALUE, "1", "", callback);
    }

    public void get(boolean fetchChildCourses, String ps, String pn, String searchTerm, Callback<CoursesTO> callback) {
        get(fetchChildCourses, ps, pn, searchTerm, "c.name", "true", callback);
    }

    public void get(boolean fetchChildCourses, String ps, String pn, String searchTerm, String orderBy, String isAscending, Callback<CoursesTO> callback) {
        GET("/courses?fetchChildCourses="+fetchChildCourses + "&ps=" + ps + "&pn=" + pn + "&searchTerm=" + searchTerm + "&orderBy=" + orderBy + "&asc=" + isAscending).go(callback);
    }

    public void create(Course course, Callback<CourseClassTO> callback) {
        POST("/courses").withContentType(Course.TYPE).withEntityBody(course).go(callback);
    }

}
