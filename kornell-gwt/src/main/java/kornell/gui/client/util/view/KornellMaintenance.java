package kornell.gui.client.util.view;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;

import kornell.gui.client.KornellConstants;

public class KornellMaintenance {
    private static KornellConstants constants = GWT.create(KornellConstants.class);

    public static PopupPanel show() {
        final PopupPanel popup = new PopupPanel();
        Alert alert = new Alert();
        alert.addStyleName("kornellMessage");
        alert.setType(AlertType.ERROR);
        alert.setText(constants.maintenanceMessage());
        popup.setWidget(alert);

        popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
            public void setPosition(int offsetWidth, int offsetHeight) {
                int left = (Window.getClientWidth() - offsetWidth) / 2;
                int top = (Window.getClientHeight() - offsetHeight) / 2;
                popup.setPopupPosition(left, top);
            }
        });

        return popup;
    }

    private static native boolean hasPlaceBar() /*-{
        return $wnd.document.getElementsByClassName("placeBar")[0] != null 
            && $wnd.document.getElementsByClassName("placeBar")[0].getAttribute("aria-hidden") == null;
    }-*/;
}
