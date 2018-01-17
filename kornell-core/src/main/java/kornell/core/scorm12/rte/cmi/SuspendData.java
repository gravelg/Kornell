package kornell.core.scorm12.rte.cmi;

import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;
import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.DataType;
import kornell.core.scorm12.rte.SCOAccess;

public class SuspendData extends DMElement {

    public static final SuspendData dme = new SuspendData();

    private SuspendData() {
        super("suspend_data", true, DataType.CMIString4096, SCOAccess.RW);
    }

    @Override
    public Map<String, String> initializeMap(Map<String, String> entries, Person p, Enrollment enrollment,
            CourseClass courseClass) {
        return defaultTo(entries, "");
    }

}
