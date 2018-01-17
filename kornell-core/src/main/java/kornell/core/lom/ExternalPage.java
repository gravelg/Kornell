package kornell.core.lom;

public interface ExternalPage extends Actom {
    String getTitle();
    void setTitle(String title);

    Integer getIndex();
    void setIndex(Integer index);

    String getURL();
    void setURL(String url);
}
