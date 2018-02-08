package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class UnreadMessagesCountChangedEvent extends GwtEvent<UnreadMessagesCountChangedEventHandler> {
    public static final Type<UnreadMessagesCountChangedEventHandler> TYPE = new Type<UnreadMessagesCountChangedEventHandler>();

    private int countChange;

    private boolean increment;

    public UnreadMessagesCountChangedEvent(int unreadMessagesCount) {
        this(unreadMessagesCount, false);
    }

    public UnreadMessagesCountChangedEvent(int countChange, boolean isIncrement) {
        this.countChange = countChange;
        this.setIncrement(isIncrement);
    }

    @Override
    protected void dispatch(UnreadMessagesCountChangedEventHandler handler) {
        handler.onUnreadMessagesCountChanged(this);
    }

    @Override
    public Type<UnreadMessagesCountChangedEventHandler> getAssociatedType() {
        return TYPE;
    }

    public int getCountChange() {
        return countChange;
    }

    public void setCountChange(int countChange) {
        this.countChange = countChange;
    }

    public boolean isIncrement() {
        return increment;
    }

    public void setIncrement(boolean increment) {
        this.increment = increment;
    }

}