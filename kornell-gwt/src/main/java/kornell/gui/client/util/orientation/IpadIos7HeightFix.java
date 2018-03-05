package kornell.gui.client.util.orientation;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.RootLayoutPanel;

public final class IpadIos7HeightFix implements IOrientationChangeHandler {

    public static void fixHeight() {
        RootLayoutPanel.get().setHeight(getWindowInnerHeight() + "px");
        Window.scrollTo(0, 0);
    }

    public static native int getWindowInnerHeight() /*-{
        return $wnd.innerHeight;
    }-*/;

    @Override
    public void onOrientationChange(final OrientationChangeEvent event) {
        // fixHeight();
    }

}
