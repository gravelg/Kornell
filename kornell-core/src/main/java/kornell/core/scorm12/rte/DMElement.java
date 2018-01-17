package kornell.core.scorm12.rte;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kornell.core.entity.CourseClass;
import kornell.core.entity.Enrollment;
import kornell.core.entity.Person;
import kornell.core.util.StringUtils;

/**
 * SCORM 1.2 Data Model Element
 * 
 * @author jfaerman
 */
public class DMElement {

    private DMElement parent = null;
    private List<DMElement> children = new ArrayList<>();
    private String key;
    private String fqkn;
    private DataType type;

    // private Object access;
    // private boolean mandatory;

    public DMElement() {
        this("");
    }

    public DMElement(String key, DMElement... children) {
        this.key = key;
        addAll(children);
    }

    protected void addAll(DMElement... children) {
        for (DMElement c : children) {
            add(c);
            c.parent = this;
        }
    }

    public DMElement(String key, DataType type, SCOAccess access) {
        this(key, false, type, access);
    }

    public DMElement(String key, boolean mandatory, DataType type, SCOAccess access) {
        this.key = key;
        this.type = type;
        // this.access = access;
        // this.mandatory = mandatory;
    }

    public DMElement(String key) {
        this(key, false, null, null);
    }

    public synchronized DMElement add(DMElement child) {
        if (child != null) {
            this.children.add(child);
            if (child.parent != null && child.parent != this) {
                child.parent.children.remove(child);
            }
            child.parent = this;
        }
        return this;
    }

    public List<DMElement> getChildren() {
        return children;
    }

    /**
     * The launch value is the value to be set to a given data model upon
     * launching the SCO
     */
    public Map<String, String> initializeMap(Map<String, String> entries, Person person, Enrollment enrollment,
            CourseClass courseClass) {
        return nothing();
    }

    /**
     * The finish value is the value to be set to a given when the LMSFinish is
     * called or the user navigates away
     */
    // TODO: map when the user navigates away reliably
    protected Map<String, String> finishMap(Map<String, String> entries) {
        return nothing();
    }

    /**
     * Fully Qualified Key Name (e.g. cmi.core.lesson_status)
     */
    public String getFQKN() {
        if (fqkn == null) {
            if (parent != null) {
                String pfqkn = parent.getFQKN();
                if (StringUtils.isSome(pfqkn))
                    fqkn = pfqkn + "." + key;
                else
                    fqkn = key;
            }
        }
        return fqkn;
    }

    public String get(Map<String, String> entries) {
        return entries.get(getFQKN());
    }

    public boolean is(Map<String, String> entries, String value) {
        String entryValue = get(entries);
        return (entryValue == null) ? false : entryValue.equals(value);
    }

    String dirty_flag = "_";

    public Map<String, String> set(final String value, final boolean dirty) {
        return set(null, value, dirty);
    }

    public Map<String, String> set(Map<String, String> entries, final String value, final boolean dirtyCheck) {
        if (entries == null) {
            entries = nothing();
            if (type != null && !type.check(value)) {
                throw new IllegalArgumentException("Type [" + type.toString() + "] does not contain [" + value + "]");
            }
        }
        boolean dirty = false;
        if (dirtyCheck) {
            String curr = get(entries);
            dirty = curr != null && curr.equals(value);
        }
        String key = dirty ? dirty(getFQKN()) : getFQKN();
        entries.put(key, value);
        return entries;
    }

    protected Map<String, String> set(final String value) {
        return set(null, value, true);
    }

    protected Map<String, String> set(Map<String, String> out, final String value) {
        return set(out, value, true);
    }

    private String dirty(String fqkn) {
        return dirty_flag + fqkn;
    }

    private Map<String, String> defaultTo(Map<String, String> entries, String defaultValue, boolean dirty) {
        if (entries != null) {
            String key = getFQKN();
            if (key.contains("launch")) {
                System.out.println("BREAK");
            }
            boolean isDefined = entries.containsKey(key);
            if (!isDefined) {
                Map<String, String> result = set(null, defaultValue, dirty);
                return result;
            }
        }
        return nothing();
    }

    public boolean isSome(Map<String, String> entries) {
        return StringUtils.isSome(entries.get(getFQKN()));
    }

    protected Map<String, String> nothing() {
        return new HashMap<>();
    }

    /**
     * Sets data model value and flags entry as dirty to be persisted before
     * launch
     */
    protected Map<String, String> setDefault(Map<String, String> entries, String value) {
        return defaultTo(entries, value, true);
    }

    /**
     * Sets data model default value, does not persist
     */
    protected Map<String, String> defaultTo(Map<String, String> entries, String value) {
        return defaultTo(entries, value, false);
    }

    public Integer asInt(Map<String, String> entries) {
        String str = get(entries);
        return Integer.parseInt(str);
    }

}