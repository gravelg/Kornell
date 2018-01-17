package kornell.core.scorm12.rte.knl;

import static kornell.core.util.StringUtils.isoNow;

import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;
import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.DataType;
import kornell.core.scorm12.rte.SCOAccess;

public class LaunchDate extends DMElement {

    public static final LaunchDate dme = new LaunchDate();

    public LaunchDate() {
        super("launch_date", false, DataType.CMIString255, SCOAccess.RO);
    }

    @Override
    public Map<String, String> initializeMap(Map<String, String> entries, Person p, Enrollment enrollment,
            CourseClass courseClass) {
        String isoNow = isoNow();
        Map<String, String> result = set(isoNow);
        return result;
    }
}