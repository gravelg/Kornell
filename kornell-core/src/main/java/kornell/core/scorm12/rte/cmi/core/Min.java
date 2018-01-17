package kornell.core.scorm12.rte.cmi.core;

import static kornell.core.scorm12.rte.DataType.CMIBlank;
import static kornell.core.scorm12.rte.DataType.CMIDecimal;
import static kornell.core.scorm12.rte.DataType.EITHER;

import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;
import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.SCOAccess;

/*
 * The minimum score that the student could have achieved.
 * The cmi.core.score.min must be a normalized value between 0 and 100. 
 */
public class Min extends DMElement {

    public static final Min dme = new Min();

    private Min() {
        super("min", false, EITHER(CMIDecimal, CMIBlank), SCOAccess.RW);
    }

    @Override
    public Map<String, String> initializeMap(Map<String, String> entries, Person p, Enrollment enrollment,
            CourseClass courseClass) {
        return defaultTo(entries, "");
    }
}
