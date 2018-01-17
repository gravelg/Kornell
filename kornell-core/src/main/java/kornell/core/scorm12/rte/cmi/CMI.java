package kornell.core.scorm12.rte.cmi;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.cmi.core.Core;

public class CMI extends DMElement {

    public static final CMI dme = new CMI();

    private CMI() {
        super("cmi", Core.dme, StudentData.dme, SuspendData.dme);
    }
}
