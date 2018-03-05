package kornell.scorm.client.scorm12;

public class CMILeaf extends CMITree {
    String value;
    boolean dirty = false;

    @Override
    public String getValue(String param) {
        return value;
    }

    @Override
    public String setValue(String key, String value) {
        boolean valueChanged = true;
        if (this.value == null) {
            valueChanged = value != null;
        } else {
            valueChanged = !this.value.equals(value);
        }
        this.dirty = valueChanged;
        this.value = value;
        return TRUE;
    }

    @Override
    boolean isLeaf() {
        return true;
    }

    @Override
    public String toString() {
        return (dirty ? "* " : "") + (value != null ? value.toString() : null);
    }

    public boolean isDirty() {
        return dirty;
    }

    @Override
    void scrub() {
        dirty = false;
    }

}
