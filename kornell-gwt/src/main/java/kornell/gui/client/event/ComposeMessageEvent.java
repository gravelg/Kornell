package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ComposeMessageEvent extends GwtEvent<ComposeMessageEventHandler> {
    public static final Type<ComposeMessageEventHandler> TYPE = new Type<ComposeMessageEventHandler>();
    private boolean showingPlacePanel;

    public ComposeMessageEvent(boolean showingPlacePanel) {
        this.setShowingPlacePanel(showingPlacePanel);
    }

    @Override
    protected void dispatch(ComposeMessageEventHandler handler) {
        handler.onComposeMessage(this);
    }

    @Override
    public Type<ComposeMessageEventHandler> getAssociatedType() {
        return TYPE;
    }

    public boolean isShowingPlacePanel() {
        return showingPlacePanel;
    }

    public void setShowingPlacePanel(boolean showingPlacePanel) {
        this.showingPlacePanel = showingPlacePanel;
    }

}