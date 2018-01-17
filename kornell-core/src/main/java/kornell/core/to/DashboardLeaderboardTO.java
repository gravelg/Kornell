package kornell.core.to;

import java.util.List;

public interface DashboardLeaderboardTO {
    public static final String TYPE = TOFactory.PREFIX + "dashboardLeaderboard+json";

    List<DashboardLeaderboardItemTO> getDashboardLeaderboardItems();
    void setDashboardLeaderboardItems(List<DashboardLeaderboardItemTO> dashboardLeaderboardItems);

}
