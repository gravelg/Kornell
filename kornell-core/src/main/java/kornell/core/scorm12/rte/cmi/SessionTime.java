package kornell.core.scorm12.rte.cmi;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.scorm12.rte.DataType;
import kornell.core.scorm12.rte.SCOAccess;

public class SessionTime extends DMElement {

    public static final SessionTime dme = new SessionTime();

    // TODO: SCORM 1.2: Client Behavior
    /*
     * Additional Behavior: A SCO is able, in a single running, to perform
     * multiple sets of the cmi.core.session_time. When the SCO issues the
     * LMSFinish() or the user navigates away, the LMS should take the last
     * cmi.core.session_time that the SCO set (if there was a set) and
     * accumulate this time to the cmi.core.total_time. Upon subsequent launch
     * of the SCO, and a LMSGetValue() call for cmi.core.total_time, the LMS
     * should return the accumulated time. LMS’s should not accumulate multiple
     * time sent to the LMS by the LMSSetValue() call for cmi.core.session_time.
     * If multiple calls to LMSSetValue() for cmi.core.session_time are made the
     * LMS should overwrite any existing value that it is persisting for
     * cmi.core.session_time.
     */
    public SessionTime() {
        super("session_time", true, DataType.CMITimespan, SCOAccess.WO);
    }
}
