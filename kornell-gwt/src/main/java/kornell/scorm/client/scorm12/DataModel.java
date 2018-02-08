package kornell.scorm.client.scorm12;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class DataModel {
    static Logger logger = Logger.getLogger(DataModel.class.getName());
    static final Map<String, CMIDataElement> elements = new HashMap<String, CMIDataElement>();

    protected Map<String, String> entries = new HashMap<String, String>();

    protected static void register(CMIDataElement element) {
        logger.info("Registering [" + element.getKey() + "]");
        elements.put(element.getKey(), element);
    }
}
