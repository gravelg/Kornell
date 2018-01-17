package kornell.core.entity;

public interface Entity{
    public static String TYPE = EntityFactory.PREFIX + "entity+json";

    String getUUID();
    void setUUID(String UUID);
}
