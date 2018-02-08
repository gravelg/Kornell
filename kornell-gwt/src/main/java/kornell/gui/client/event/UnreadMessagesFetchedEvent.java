package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class UnreadMessagesFetchedEvent extends GwtEvent<UnreadMessagesFetchedEventHandler> {
    public static final Type<UnreadMessagesFetchedEventHandler> TYPE = new Type<UnreadMessagesFetchedEventHandler>();

    private String unreadMessagesCount;

    public UnreadMessagesFetchedEvent(String unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }

    @Override
    protected void dispatch(UnreadMessagesFetchedEventHandler handler) {
        handler.onUnreadMessagesFetched(this);
    }

    @Override
    public Type<UnreadMessagesFetchedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getUnreadMessagesCount() {
        return unreadMessagesCount;
    }

    public void setUnreadMessagesCount(String unreadMessagesCount) {
        this.unreadMessagesCount = unreadMessagesCount;
    }

}