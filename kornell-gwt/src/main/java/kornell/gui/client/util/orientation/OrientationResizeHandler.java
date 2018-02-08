package kornell.gui.client.util.orientation;

import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.web.bindery.event.shared.EventBus;

public final class OrientationResizeHandler implements ResizeHandler {

    private static final OrientationChangeEvent LANDSCAPE_EVENT = new OrientationChangeEvent(Orientation.LANDSCAPE);

    private static final OrientationChangeEvent PORTRAIT_EVENT = new OrientationChangeEvent(Orientation.PORTRAIT);

    private Orientation orientation;

    private EventBus bus;

    public OrientationResizeHandler(EventBus bus) {
        this.bus = bus;
    }

    @Override
    public void onResize(final ResizeEvent event) {
        final Orientation o = event.getWidth() > event.getHeight() ? Orientation.LANDSCAPE : Orientation.PORTRAIT;
        if (orientation != o) {
            bus.fireEvent(o == Orientation.PORTRAIT ? PORTRAIT_EVENT : LANDSCAPE_EVENT);
            orientation = o;
        }
    }

}
