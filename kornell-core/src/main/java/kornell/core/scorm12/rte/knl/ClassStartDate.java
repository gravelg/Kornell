package kornell.core.scorm12.rte.knl;

import static kornell.core.scorm12.rte.DataType.CMIString255;
import static kornell.core.scorm12.rte.SCOAccess.RO;

import java.util.Date;
import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;
import kornell.core.scorm12.rte.DMElement;
import kornell.core.util.StringUtils;

public class ClassStartDate extends DMElement {

    public static final ClassStartDate dme = new ClassStartDate();

    public ClassStartDate() {
        super("class_start_date", CMIString255, RO);
    }

    @Override
    public Map<String, String> initializeMap(Map<String, String> entries, Person person, Enrollment enrollment,
            CourseClass courseClass) {
        Date startDate = courseClass.getStartDate();
        String startDateStr = StringUtils.toISO(startDate);
        return set(entries, startDateStr);
    }
}
