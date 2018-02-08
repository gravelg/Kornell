package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ViewReadyEvent extends GwtEvent<ViewReadyEventHandler> {
    public static final Type<ViewReadyEventHandler> TYPE = new Type<ViewReadyEventHandler>();

    @Override
    public Type<ViewReadyEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ViewReadyEventHandler handler) {
        handler.onViewReady(this);
    }

}