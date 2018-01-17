package kornell.core.scorm12.rte.cmi.core;

import static kornell.core.scorm12.rte.DataType.CMIIdentifier;
import static kornell.core.scorm12.rte.SCOAccess.RO;

import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;
import kornell.core.scorm12.rte.DMElement;

public class StudentName extends DMElement {

    public static final StudentName dme = new StudentName();

    public StudentName() {
        super("student_name", true, CMIIdentifier, RO);
    }

    @Override
    public Map<String, String> initializeMap(Map<String, String> entries, Person person, Enrollment enrollment,
            CourseClass courseClass) {
        return set(person.getFullName());
    }
}
