package kornell.core.scorm12.rte.cmi.core;

import static kornell.core.scorm12.rte.DataType.CMIBlank;
import static kornell.core.scorm12.rte.DataType.CMIDecimal;
import static kornell.core.scorm12.rte.DataType.EITHER;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.SCOAccess;

public class Raw extends DMElement {

    public static final Raw dme = new Raw();

    private Raw() {
        super("raw", true, EITHER(CMIDecimal, CMIBlank), SCOAccess.RW);
    }

}
