package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface UnreadMessagesFetchedEventHandler extends EventHandler {

    void onUnreadMessagesFetched(UnreadMessagesFetchedEvent event);
}
