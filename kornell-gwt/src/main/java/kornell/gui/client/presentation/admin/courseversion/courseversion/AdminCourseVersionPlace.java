package kornell.gui.client.presentation.admin.courseversion.courseversion;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import kornell.gui.client.presentation.admin.courseversion.CourseVersionPlace;

public class AdminCourseVersionPlace extends CourseVersionPlace {
    private String courseVersionUUID;

    public AdminCourseVersionPlace(String courseVersionUUID) {
        this.setCourseVersionUUID(courseVersionUUID);
    }

    @Prefix("a.courseVersion")
    public static class Tokenizer implements PlaceTokenizer<AdminCourseVersionPlace> {
        public AdminCourseVersionPlace getPlace(String token) {
            return new AdminCourseVersionPlace(token);
        }

        public String getToken(AdminCourseVersionPlace place) {
            return place.getCourseVersionUUID();
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }

    public String getCourseVersionUUID() {
        return courseVersionUUID;
    }

    public void setCourseVersionUUID(String courseVersionUUID) {
        this.courseVersionUUID = courseVersionUUID;
    }
}
