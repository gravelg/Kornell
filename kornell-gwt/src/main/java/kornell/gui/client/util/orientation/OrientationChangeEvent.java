package kornell.gui.client.util.orientation;

import com.google.gwt.event.shared.GwtEvent;

public final class OrientationChangeEvent extends GwtEvent<IOrientationChangeHandler> {

    public static Type<IOrientationChangeHandler> TYPE = new Type<IOrientationChangeHandler>();

    private Orientation orientation;

    public OrientationChangeEvent(final Orientation orientation) {
        this.orientation = orientation;
    }

    @Override
    public Type<IOrientationChangeHandler> getAssociatedType() {
        return TYPE;
    }

    public Orientation getOrientation() {
        return orientation;
    }

    @Override
    protected void dispatch(final IOrientationChangeHandler handler) {
        handler.onOrientationChange(this);
    }

}
