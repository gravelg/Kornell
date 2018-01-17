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
 * The maximum score that the student could have achieved.
 * The cmi.core.score.max must be a normalized value between 0 and 100. 
 */
public class Max extends DMElement {

    public static final Max dme = new Max();

    private Max() {
        super("max", false, EITHER(CMIDecimal, CMIBlank), SCOAccess.RW);
    }

    @Override
    public Map<String, String> initializeMap(Map<String, String> entries, Person p, Enrollment enrollment,
            CourseClass courseClass) {
        return defaultTo(entries, "");
    }

}
