package kornell.core.lom;

//TODO: Remove Manual Polymorphism :(
public interface Content {
    ContentFormat getFormat();
    void setFormat(ContentFormat format);

    ExternalPage getExternalPage();
    void setExternalPage(ExternalPage page);

    Topic getTopic();
    void setTopic(Topic topic);
}
