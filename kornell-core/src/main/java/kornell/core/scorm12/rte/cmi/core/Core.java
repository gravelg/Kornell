package kornell.core.scorm12.rte.cmi.core;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.cmi.SessionTime;

public class Core extends DMElement {

    public static final Core dme = new Core();

    private Core() {
        super("core");
        addAll(LessonStatus.dme, LessonLocation.dme, LessonMode.dme, SessionTime.dme, Exit.dme, Score.dme,
                StudentName.dme);
    }
}
