package kornell.core.to;

import kornell.core.entity.ChatThreadType;


public interface UnreadChatThreadTO {
    public static final String TYPE = TOFactory.PREFIX+"unreadChatThread+json";

    String getUnreadMessages();
    void setUnreadMessages(String unreadMessages);

    String getChatThreadUUID();
    void setChatThreadUUID(String chatThreadUUID);

    String getChatThreadCreatorName();
    void setChatThreadCreatorName(String chatThreadCreatorName);

    String getEntityUUID();
    void setEntityUUID(String entityUUID);

    String getEntityName();
    void setEntityName(String entityName);

    ChatThreadType getThreadType();
    void setThreadType(ChatThreadType threadType);
}