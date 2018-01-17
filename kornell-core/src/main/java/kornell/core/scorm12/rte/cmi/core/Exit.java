package kornell.core.scorm12.rte.cmi.core;

import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;
import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.DataType;
import kornell.core.scorm12.rte.SCOAccess;

public class Exit extends DMElement {

    public static final Exit dme = new Exit();

    // TODO: SCORM 1.2:
    /*
     * Additional behavior: A SCO has the option of setting the cmi.core.exit to
     * one of four values. Based on the value, the LMS behavior should be as
     * follows: If the SCO set the cmi.core.exit to “time-out” the LMS should
     * set cmi.core.entry to “” (empty) upon the next launching of the SCO. If
     * the SCO set the cmi.core.exit to “suspend” the LMS should set the
     * cmi.core.entry to “resume” upon the next launching of the SCO. If the SCO
     * set the cmi.core.exit to “logout” the LMS should set the cmi.core.entry
     * to “” (empty) upon the next launching of the SCO. In addition, the LMS
     * should log the student out of the course when the SCO that set the
     * cmi.core.exit to “logout” has issued the LMSFinish() or the user
     * navigates away. If the SCO set the cmi.core.exit to “” (empty) the LMS
     * should set the cmi.core.entry to “” (empty) upon the next launching of
     * the SCO. If the SCO did not set the cmi.core.exit to any value that LMS
     * should set cmi.core.entry to “” (empty) upon the next launching of the
     * SCO.
     */
    private Exit() {
        super("exit", true, DataType.CMIVocabulary("time-out", "suspend", "logout", ""), SCOAccess.WO);
    }

    @Override
    public Map<String, String> initializeMap(Map<String, String> entries, Person p, Enrollment enrollment,
            CourseClass courseClass) {
        return defaultTo(entries, "");
    }
}
