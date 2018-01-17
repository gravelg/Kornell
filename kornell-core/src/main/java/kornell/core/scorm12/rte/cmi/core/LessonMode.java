package kornell.core.scorm12.rte.cmi.core;

import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;
import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.DataType;
import kornell.core.scorm12.rte.SCOAccess;

public class LessonMode extends DMElement {

    private static final String browse = "browse";
    private static final String normal = "normal";
    private static final String review = "review";
    public static LessonMode dme = new LessonMode();

    public LessonMode() {
        super("lesson_mode", false, DataType.CMIVocabulary(browse, normal, review), SCOAccess.RO);
    }

    @Override
    public Map<String, String> initializeMap(Map<String, String> entries, Person p, Enrollment enrollment,
            CourseClass courseClass) {
        // TODO: SCORM 1.2: Detect and initilialize in other modes
        return defaultTo(entries, normal);
    }

}
