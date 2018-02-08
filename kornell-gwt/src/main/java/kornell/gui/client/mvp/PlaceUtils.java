package kornell.gui.client.mvp;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceChangeRequestEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

public class PlaceUtils {

    public static void reloadCurrentPlace(EventBus eventBus, PlaceController placeController) {
        Place where = placeController.getWhere();
        // Taken from PlaceController.maybeGoTo()
        PlaceChangeRequestEvent willChange = new PlaceChangeRequestEvent(where);
        eventBus.fireEvent(willChange);
        String warning = willChange.getWarning();
        // Taken from PlaceController.goTo()
        if (warning == null) {
            eventBus.fireEvent(new PlaceChangeEvent(where));
        }
    }

}