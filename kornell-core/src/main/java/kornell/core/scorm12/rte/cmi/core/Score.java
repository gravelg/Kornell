package kornell.core.scorm12.rte.cmi.core;

import kornell.core.scorm12.rte.DMElement;

public class Score extends DMElement {

    public static final Score dme = new Score();

    public Score() {
        super("score");
        addAll(Raw.dme, Min.dme, Max.dme);
    }
}
