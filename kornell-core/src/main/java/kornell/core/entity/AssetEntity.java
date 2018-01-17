package kornell.core.entity;

public interface AssetEntity extends Entity {
    public static String TYPE = EntityFactory.PREFIX + "assetEntity+json";

    String getTitle();
    void setTitle(String title);

    CourseDetailsEntityType getEntityType();
    void setEntityType(CourseDetailsEntityType entityType);

    String getEntityUUID();
    void setEntityUUID(String entityUUID);

    Integer getIndex();
    void setIndex(Integer index);

}
