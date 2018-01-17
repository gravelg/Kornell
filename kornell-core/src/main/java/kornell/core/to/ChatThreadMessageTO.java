package kornell.core.to;

import java.util.Date;

import kornell.core.entity.RoleType;

public interface ChatThreadMessageTO {
    public static final String TYPE = TOFactory.PREFIX+"chatThreadMessage+json";

    String getSenderFullName();
    void setSenderFullName(String senderFullName);

    RoleType getSenderRole();
    void setSenderRole(RoleType senderRole);

    Date getSentAt();
    void setSentAt(Date sentAt);

    String getMessage();
    void setMessage(String message);

}