package kornell.core.scorm12.rte.knl;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.DataType;
import kornell.core.scorm12.rte.SCOAccess;

public class Enrollment extends DMElement {

    public static final Enrollment dme = new Enrollment();

    public Enrollment() {
        super("enrollment", false, DataType.UUID, SCOAccess.RO);
    }

}
