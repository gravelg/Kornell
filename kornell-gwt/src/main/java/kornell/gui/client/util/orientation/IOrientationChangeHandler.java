package kornell.gui.client.util.orientation;

import com.google.gwt.event.shared.EventHandler;

public interface IOrientationChangeHandler extends EventHandler {

    void onOrientationChange(OrientationChangeEvent event);

}
