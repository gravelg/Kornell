package kornell.gui.client.personnel;

import kornell.core.entity.ContentSpec;
import kornell.core.to.CourseClassTO;

public class Teachers {
	public static Teacher of(CourseClassTO courseClassTO) {
		ContentSpec spec = courseClassTO.getCourseVersionTO()
				.getCourseTO().getCourse().getContentSpec();
		switch (spec) {
		case KNL:
			return new KNLTeacher(courseClassTO);
		case SCORM12:
			return new SCORM12Teacher(courseClassTO);
		default: throw new IllegalArgumentException("Unknown content spec ["+spec+"]");
		}
	}

}
