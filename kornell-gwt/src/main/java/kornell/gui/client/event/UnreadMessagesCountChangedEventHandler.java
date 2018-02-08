package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UnreadMessagesCountChangedEventHandler extends EventHandler {
    void onUnreadMessagesCountChanged(UnreadMessagesCountChangedEvent event);
}
