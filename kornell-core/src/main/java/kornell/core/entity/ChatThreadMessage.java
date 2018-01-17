package kornell.core.entity;

import java.util.Date;

public interface ChatThreadMessage extends Entity {
    public static String TYPE = EntityFactory.PREFIX + "chatThreadMessage+json";

    String getThreadUUID();
    void setThreadUUID(String threadUUID);

    Date getSentAt();
    void setSentAt(Date sentAt);

    String getThreadRecipientUUID();
    void setThreadRecipientUUID(String threadRecipientUUID);

    String getMessageText();
    void setMessageText(String messageText);

}
