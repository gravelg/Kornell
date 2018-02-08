package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowDetailsEvent extends GwtEvent<ShowDetailsEventHandler> {

    public static final Type<ShowDetailsEventHandler> TYPE = new Type<ShowDetailsEventHandler>();

    private boolean showDetails;

    public ShowDetailsEvent(boolean showDetails) {
        this.showDetails = showDetails;
    }

    @Override
    public Type<ShowDetailsEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowDetailsEventHandler handler) {
        handler.onShowDetails(this);
    }

    public boolean isShowDetails() {
        return showDetails;
    }

    public void setShowDetails(boolean showDetails) {
        this.showDetails = showDetails;
    }
}