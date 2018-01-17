package kornell.core.scorm12.rte.knl;

import kornell.core.scorm12.rte.DMElement;

public class KNL extends DMElement {

    public static final KNL dme = new KNL();

    private KNL() {
        super("knl", ClassStartDate.dme, FirstLaunch.dme, LaunchDate.dme);
    }
}
