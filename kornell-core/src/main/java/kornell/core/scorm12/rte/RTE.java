package kornell.core.scorm12.rte;

import kornell.core.scorm12.rte.cmi.CMI;
import kornell.core.scorm12.rte.knl.KNL;

public class RTE extends DMElement {

    // TODO: Lazy instantiation of Data Model Elements
    public static final DMElement root = new DMElement("", CMI.dme, KNL.dme);

}
