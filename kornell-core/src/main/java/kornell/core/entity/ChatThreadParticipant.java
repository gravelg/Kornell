package kornell.core.entity;

import java.util.Date;

public interface ChatThreadParticipant extends Entity {

    public static String TYPE = EntityFactory.PREFIX + "chatThreadParticipant+json";

    String getThreadUUID();
    void setThreadUUID(String threadUUID);

    String getPersonUUID();
    void setPersonUUID(String personUUID);

    Date getLastReadAt();
    void setLastReadAt(Date lastReadAt);

    Boolean isActive();
    void setActive(Boolean active);

    Date getLastJoinDate();
    void setLastJoinDate(Date lastJoinDate);

    Integer getUnreadCount();
    void setUnreadCount(Integer unreadCount);
}
