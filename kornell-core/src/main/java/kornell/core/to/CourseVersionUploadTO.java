package kornell.core.to;

public interface CourseVersionUploadTO {
    public static final String TYPE = TOFactory.PREFIX+"courseVersionUpload+json";

    String getUrl();
    void setUrl(String url);
}
