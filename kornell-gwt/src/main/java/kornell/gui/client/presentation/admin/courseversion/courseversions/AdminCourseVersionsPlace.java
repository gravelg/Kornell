package kornell.gui.client.presentation.admin.courseversion.courseversions;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import kornell.gui.client.presentation.admin.courseversion.CourseVersionPlace;

public class AdminCourseVersionsPlace extends CourseVersionPlace {
    public static final AdminCourseVersionsPlace instance = new AdminCourseVersionsPlace();

    public AdminCourseVersionsPlace() {
    }

    @Prefix("a.courseVersions")
    public static class Tokenizer implements PlaceTokenizer<AdminCourseVersionsPlace> {
        public AdminCourseVersionsPlace getPlace(String token) {
            return new AdminCourseVersionsPlace();
        }

        public String getToken(AdminCourseVersionsPlace place) {
            return "";
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }
}
