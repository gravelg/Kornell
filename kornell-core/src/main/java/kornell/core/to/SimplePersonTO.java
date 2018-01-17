package kornell.core.to;

public interface SimplePersonTO {

    public static String TYPE = TOFactory.PREFIX + "simplePerson+json";

    String getPersonUUID();
    void setPersonUUID(String personUUID);

    String getFullName();
    void setFullName(String fullName);

    String getUsername();
    void setUsername(String username);
}
