package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ShowPacifierEvent extends GwtEvent<ShowPacifierEventHandler> {

    public static final Type<ShowPacifierEventHandler> TYPE = new Type<ShowPacifierEventHandler>();

    private boolean showPacifier;

    public ShowPacifierEvent(boolean showPacifier) {
        this.showPacifier = showPacifier;
    }

    @Override
    public Type<ShowPacifierEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ShowPacifierEventHandler handler) {
        handler.onShowPacifier(this);
    }

    public boolean isShowPacifier() {
        return showPacifier;
    }

    public void setShowPacifier(boolean showPacifier) {
        this.showPacifier = showPacifier;
    }
}