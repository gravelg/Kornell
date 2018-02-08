package kornell.gui.client.personnel;

import kornell.core.to.CourseClassTO;
import kornell.core.to.UserInfoTO;

public class LegacyTeacher implements Teacher {

    protected CourseClassTO courseClassTO;

    public LegacyTeacher(CourseClassTO courseClassTO) {
        this.courseClassTO = courseClassTO;
    }

    @Override
    public Student student(UserInfoTO userInfoTO) {
        return new StudentImpl(courseClassTO, userInfoTO);
    }

}
