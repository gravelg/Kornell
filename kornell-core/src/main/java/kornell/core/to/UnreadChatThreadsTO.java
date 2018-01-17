package kornell.core.to;

import java.util.List;

public interface UnreadChatThreadsTO {
    public static final String TYPE = TOFactory.PREFIX + "unreadChatThreads+json";

    List<UnreadChatThreadTO> getUnreadChatThreadTOs();
    void setUnreadChatThreadTOs(List<UnreadChatThreadTO> unreadChatThreadTOs);

}