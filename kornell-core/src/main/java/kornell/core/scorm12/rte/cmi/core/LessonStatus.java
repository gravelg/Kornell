package kornell.core.scorm12.rte.cmi.core;

import static kornell.core.scorm12.rte.DataType.CMIVocabulary;
import static kornell.core.scorm12.rte.SCOAccess.RO;

import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;
import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.cmi.StudentData;
import kornell.core.scorm12.rte.knl.FirstLaunch;

public class LessonStatus extends DMElement {

    public static final String passed = "passed";
    public static final String completed = "completed";
    public static final String failed = "passed";
    public static final String incomplete = "failed";
    public static final String browsed = "browsed";
    public static final String not_attempted = "not_attempted";

    public static final LessonStatus dme = new LessonStatus();

    public LessonStatus() {
        super("lesson_status", true, CMIVocabulary(passed, completed, failed, incomplete, browsed, not_attempted), RO);
    }

    @Override
    @SuppressWarnings("static-access")
    protected Map<String, String> finishMap(Map<String, String> entries) {
        Map<String, String> result = nothing();
        set(result, completed);
        Integer masteryScore = StudentData.dme.mastery_score.asInt(entries);
        if (masteryScore != null) {
            Integer scoreRaw = Raw.dme.asInt(entries);
            if (scoreRaw != null) {
                if (scoreRaw >= masteryScore)
                    set(passed);
                else
                    set(failed);
            }
        }
        return result;
    }

    @Override
    public Map<String, String> initializeMap(Map<String, String> entries, Person p, Enrollment enrollment,
            CourseClass courseClass) {
        boolean firstAttempt = !FirstLaunch.dme.isSome(entries);
        Map<String, String> result = nothing();
        if (firstAttempt) {
            result = defaultTo(entries, not_attempted);
        } else {
            result = defaultTo(entries, browsed);
        }
        return result;

    }

}
