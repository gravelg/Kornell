package kornell.gui.client.presentation.admin.courseclass.courseclass;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import kornell.gui.client.presentation.admin.courseclass.CourseClassPlace;

public class AdminCourseClassPlace extends CourseClassPlace {
    private String courseClassUUID;

    public AdminCourseClassPlace(String courseClassUUID) {
        this.setCourseClassUUID(courseClassUUID);
    }

    @Prefix("a.courseClass")
    public static class Tokenizer implements PlaceTokenizer<AdminCourseClassPlace> {
        public AdminCourseClassPlace getPlace(String token) {
            return new AdminCourseClassPlace(token);
        }

        public String getToken(AdminCourseClassPlace place) {
            return place.getCourseClassUUID();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }

    public String getCourseClassUUID() {
        return courseClassUUID;
    }

    public void setCourseClassUUID(String courseClassUUID) {
        this.courseClassUUID = courseClassUUID;
    }
}
