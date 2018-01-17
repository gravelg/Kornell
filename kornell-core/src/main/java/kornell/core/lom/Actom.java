package kornell.core.lom;

public interface Actom {
    String getKey();
    void setKey(String key);

    boolean isVisited();
    void setVisited(boolean visited);
}
