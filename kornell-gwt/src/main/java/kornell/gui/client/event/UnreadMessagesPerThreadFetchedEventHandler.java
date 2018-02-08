package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UnreadMessagesPerThreadFetchedEventHandler extends EventHandler {

    void onUnreadMessagesPerThreadFetched(UnreadMessagesPerThreadFetchedEvent event);
}
