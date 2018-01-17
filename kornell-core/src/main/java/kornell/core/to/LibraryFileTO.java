package kornell.core.to;


public interface LibraryFileTO {
    public static final String TYPE = TOFactory.PREFIX+"libraryfile+json";

    String getURL();
    void setURL(String url);

    String getFileType();
    void setFileType(String fileType);

    String getFileName();
    void setFileName(String fileName);

    String getFileDescription();
    void setFileDescription(String fileDescription);

    String getFileSize();
    void setFileSize(String fileSize);

    String getPublishingDate();
    void setPublishingDate(String publishingDate);

}