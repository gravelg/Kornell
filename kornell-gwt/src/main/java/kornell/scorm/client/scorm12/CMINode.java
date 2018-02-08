package kornell.scorm.client.scorm12;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import kornell.core.util.StringUtils;

public class CMINode extends CMITree {
    SortedMap<String, CMITree> children = new TreeMap<String, CMITree>();

    @Override
    public String getValue(String path) {
        String[] parts = path.split("\\.", 2);
        String key = parts[0];
        String subParam = null;
        if (parts.length > 1) {
            subParam = parts[1];
        }

        if (children.containsKey(key)) {
            CMITree cmiTree = children.get(key);
            return cmiTree.getValue(subParam);
        }
        return null;
    }

    @Override
    public String setValue(String path, String value) {
        String[] parts = path.split("\\.", 2);
        String key = parts[0];
        String subPath = null;
        if (parts.length > 1) {
            subPath = parts[1];
        }
        CMITree cmiTree = children.get(key);
        if (cmiTree == null) {
            if (StringUtils.isSome(subPath)) {
                cmiTree = new CMINode();
            } else {
                cmiTree = new CMILeaf();
            }
            children.put(key, cmiTree);
        }
        return cmiTree.setValue(subPath, value);
    }

    @Override
    boolean isLeaf() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append("{\n");
        for (Map.Entry<String, CMITree> child : children.entrySet()) {
            buf.append(" " + child.getKey() + " = " + child.getValue() + "\n");
        }
        buf.append("}\n");
        return buf.toString();
    }

    // TODO: Consider complexity / DRY
    @Override
    boolean isDirty() {
        for (CMITree child : children.values()) {
            if (child.isDirty())
                return true;
        }
        return false;
    }

    @Override
    void scrub() {
        for (CMITree child : children.values()) {
            child.scrub();
        }
    }

}
