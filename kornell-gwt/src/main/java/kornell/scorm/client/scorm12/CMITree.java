package kornell.scorm.client.scorm12;

import java.util.HashMap;
import java.util.Map;

public abstract class CMITree {
	static final String SEPARATOR = ".";
	static final String TRUE = "true";

	abstract String getValue(String param);

	abstract String setValue(String key, String value);

	abstract boolean isLeaf();
	abstract boolean isDirty();
	abstract void scrub();

	public static final CMITree empty() {
		return new CMINode();
	}
	
	public static final CMITree create(Map<String, String> entries) {
		CMITree tree = CMITree.empty();
		for(Map.Entry<String, String> e:entries.entrySet()){
			tree.setValue(e.getKey(), e.getValue());
		}
		tree.scrub();
		return tree;
	}

	public static final Map<String, String> collectValues(CMITree tree) {
		Map<String, String> acc = new HashMap<String, String>();
		collectValues("", tree, acc, false);
		return acc;
	}

	private static void collectValues(String key, CMITree tree,
			Map<String, String> acc, boolean dirtyOnly) {
		if (tree.isLeaf()) {
			CMILeaf leaf = (CMILeaf) tree;
			if ((dirtyOnly && leaf.isDirty()) || !dirtyOnly) {
				acc.put(key, tree.getValue(null));
			}
		} else {
			CMINode node = (CMINode) tree;
			for (Map.Entry<String, CMITree> child : node.children.entrySet()) {
				String mapKey = key.isEmpty() ?
						child.getKey() :
						key + SEPARATOR + child.getKey();
				collectValues(mapKey, child.getValue(), acc, dirtyOnly);
			}
		}
	}

	public static Map<String, String> collectDirty(CMITree tree) {
		Map<String, String> acc = new HashMap<String, String>();
		collectValues("", tree, acc, true);
		for (Map.Entry<String, String> entry : acc.entrySet()) {
			if (entry.getKey().startsWith("knl.action.")) {
				acc.remove(entry.getKey());
			}
		}
		return acc;
	}
}
