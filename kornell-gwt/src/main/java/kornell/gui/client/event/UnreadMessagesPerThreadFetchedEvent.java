package kornell.gui.client.event;

import java.util.List;

import com.google.gwt.event.shared.GwtEvent;

import kornell.core.to.UnreadChatThreadTO;

public class UnreadMessagesPerThreadFetchedEvent extends GwtEvent<UnreadMessagesPerThreadFetchedEventHandler> {
    public static final Type<UnreadMessagesPerThreadFetchedEventHandler> TYPE = new Type<UnreadMessagesPerThreadFetchedEventHandler>();

    private List<UnreadChatThreadTO> unreadChatThreadTOs;

    public UnreadMessagesPerThreadFetchedEvent(List<UnreadChatThreadTO> unreadChatThreadTO) {
        this.unreadChatThreadTOs = unreadChatThreadTO;
    }

    @Override
    protected void dispatch(UnreadMessagesPerThreadFetchedEventHandler handler) {
        handler.onUnreadMessagesPerThreadFetched(this);
    }

    @Override
    public Type<UnreadMessagesPerThreadFetchedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public List<UnreadChatThreadTO> getUnreadChatThreadTOs() {
        return unreadChatThreadTOs;
    }

}