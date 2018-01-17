package kornell.core.to;

public interface DashboardLeaderboardItemTO {

    public static String TYPE = TOFactory.PREFIX + "dashboardLeaderboardItem+json";

    String getPersonUUID();
    void setPersonUUID(String personUUID);

    String getFullName();
    void setFullName(String fullName);

    String getAttribute();
    void setAttribute(String attribute);
}
