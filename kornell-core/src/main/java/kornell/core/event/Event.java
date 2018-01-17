package kornell.core.event;

import java.util.Date;

public interface Event {

    String getUUID();
    void setUUID(String uuid);

    Date getEventFiredAt();
    void setEventFiredAt(Date eventFiredAt);
}
