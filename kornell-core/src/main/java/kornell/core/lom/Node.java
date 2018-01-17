package kornell.core.lom;

import java.util.List;

public interface Node {
    List<Content> getChildren();
    void setChildren(List<Content> children);
}
