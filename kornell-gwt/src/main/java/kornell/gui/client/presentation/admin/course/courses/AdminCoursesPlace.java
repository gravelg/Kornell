package kornell.gui.client.presentation.admin.course.courses;

import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

import kornell.gui.client.presentation.admin.course.CoursePlace;

public class AdminCoursesPlace extends CoursePlace {
    public static final AdminCoursesPlace instance = new AdminCoursesPlace();

    public AdminCoursesPlace() {
    }

    @Prefix("a.courses")
    public static class Tokenizer implements PlaceTokenizer<AdminCoursesPlace> {
        public AdminCoursesPlace getPlace(String token) {
            return new AdminCoursesPlace();
        }

        public String getToken(AdminCoursesPlace place) {
            return "";
        }
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + ":" + new Tokenizer().getToken(this);
    }
}
