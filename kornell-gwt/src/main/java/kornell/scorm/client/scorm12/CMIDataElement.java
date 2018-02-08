package kornell.scorm.client.scorm12;

public class CMIDataElement {
    String key;
    String defaultValue;
    Action onSet;

    public CMIDataElement(String key, String defaultValue, Action onSet) {
        this.key = key;
        this.defaultValue = defaultValue;
        this.onSet = onSet;
    }

    public CMIDataElement(String key, String defaultValue) {
        this(key, defaultValue, null);
    }

    public CMIDataElement(String key) {
        this(key, "", null);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public Action getOnSet() {
        return onSet;
    }
}
