package kornell.gui.client.presentation.admin.course.course;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import kornell.gui.client.presentation.admin.course.CoursePlace;

public class AdminCoursePlace extends CoursePlace {
    private String courseUUID;

    public AdminCoursePlace(String courseUUID) {
        this.setCourseUUID(courseUUID);
    }

    @Prefix("a.course")
    public static class Tokenizer implements PlaceTokenizer<AdminCoursePlace> {
        public AdminCoursePlace getPlace(String token) {
            return new AdminCoursePlace(token);
        }

        public String getToken(AdminCoursePlace place) {
            return place.getCourseUUID();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }

    public String getCourseUUID() {
        return courseUUID;
    }

    public void setCourseUUID(String courseUUID) {
        this.courseUUID = courseUUID;
    }
}
