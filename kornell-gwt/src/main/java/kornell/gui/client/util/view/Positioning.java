package kornell.gui.client.util.view;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

public class Positioning {

    // TODO: fetch these dynamically
    // TODO: Extract view
    public static final int NORTH_BAR = 45;
    public static final int NORTH_BAR_PLUS = NORTH_BAR + 35;
    public static final int SOUTH_BAR = 35;
    public static final int BAR_WIDTH = 962;
    public static final int NOTES_MIN_HEIGHT = 300;
    public static final int BAR_ANIMATION_LENGTH = 600;
    public static final int MOBILE_CHAT_THRESHOLD = 568;

    public static void placeBetweenBars(FlowPanel pnl) {
        String width = Window.getClientWidth() + "px";
        String height = getClientHeightBetweenBars() + "px";
        pnl.getElement().setPropertyString("width", width);
        pnl.getElement().setPropertyString("height", height);
    }

    public static int getClientHeightBetweenBars() {
        return (Window.getClientHeight() - SOUTH_BAR - NORTH_BAR);
    }

    public static void betweenBars(final FlowPanel pnl) {
        Window.addResizeHandler(new ResizeHandler() {
            @Override
            public void onResize(ResizeEvent event) {
                Scheduler.get().scheduleDeferred(new Command() {
                    @Override
                    public void execute() {
                        placeBetweenBars(pnl);
                    }
                });
            }
        });
        placeBetweenBars(pnl);
    }
	
    public static native boolean hasPlaceBar() /*-{
        return $wnd.document.getElementsByClassName("placeBar")[0] != null 
            && $wnd.document.getElementsByClassName("placeBar")[0].getAttribute("aria-hidden") == null;
    }-*/;

}
