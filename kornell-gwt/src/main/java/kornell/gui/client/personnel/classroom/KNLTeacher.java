package kornell.gui.client.personnel.classroom;

import kornell.core.to.CourseClassTO;

public class KNLTeacher extends LegacyTeacher implements Teacher {

    public KNLTeacher(CourseClassTO courseClassTO) {
        super(courseClassTO);
    }

}
