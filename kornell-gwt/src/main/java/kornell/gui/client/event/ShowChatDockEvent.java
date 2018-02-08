package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowChatDockEvent extends GwtEvent<ShowChatDockEventHandler> {

    public static final Type<ShowChatDockEventHandler> TYPE = new Type<ShowChatDockEventHandler>();

    private boolean showChatDock;

    public ShowChatDockEvent(boolean showChatDock) {
        this.showChatDock = showChatDock;
    }

    @Override
    public Type<ShowChatDockEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowChatDockEventHandler handler) {
        handler.onShowChatDock(this);
    }

    public boolean isShowChatDock() {
        return showChatDock;
    }

    public void setShowChatDock(boolean showChatDock) {
        this.showChatDock = showChatDock;
    }
}