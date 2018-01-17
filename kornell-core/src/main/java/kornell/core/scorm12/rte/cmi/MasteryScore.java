package kornell.core.scorm12.rte.cmi;

import static kornell.core.scorm12.rte.DataType.CMIDecimal;
import static kornell.core.scorm12.rte.SCOAccess.RO;

import kornell.core.scorm12.rte.DMElement;

public class MasteryScore extends DMElement {

    public static final MasteryScore dme = new MasteryScore();

    public MasteryScore() {
        super("mastery_score", false, CMIDecimal, RO);
    }
}
