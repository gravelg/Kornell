package kornell.core.entity;

public interface Course extends LearningEntity {
    public static String TYPE = EntityFactory.PREFIX + "course+json";

    String getCode();
    void setCode(String code);

    String getDescription();
    void setDescription(String description);

    String getInfoJson();
    void setInfoJson(String infoJson);

    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);

    boolean isChildCourse();
    void setChildCourse(boolean childCourse);

    ContentSpec getContentSpec();
    void setContentSpec(ContentSpec contentSpec);
}
