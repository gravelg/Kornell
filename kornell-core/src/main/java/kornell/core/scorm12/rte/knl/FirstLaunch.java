package kornell.core.scorm12.rte.knl;

import static kornell.core.scorm12.rte.DataType.CMIString255;
import static kornell.core.scorm12.rte.SCOAccess.RO;

import java.util.Map;

import kornell.core.scorm12.rte.DMElement;
import kornell.core.util.StringUtils;

public class FirstLaunch extends DMElement {

    public static final FirstLaunch dme = new FirstLaunch();

    public FirstLaunch() {
        super("first_launch", false, CMIString255, RO);
    }

    public Map<String, String> initializeMap(Map<String, String> entries) {
        String defaultValue = defaultValue();
        Map<String, String> defaultTo = defaultTo(entries, defaultValue);
        return defaultTo;
    }

    private String defaultValue() {
        return StringUtils.isoNow();
    }

}
