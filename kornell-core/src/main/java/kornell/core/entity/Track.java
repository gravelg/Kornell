package kornell.core.entity;

public interface Track extends Entity {
    public static String TYPE = EntityFactory.PREFIX + "track+json";

    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);

    String getName();
    void setName(String name);
}
