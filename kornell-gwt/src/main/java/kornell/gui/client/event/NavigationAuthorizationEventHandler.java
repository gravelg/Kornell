package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface NavigationAuthorizationEventHandler extends EventHandler {

    void onNextActivityOK(NavigationAuthorizationEvent evt);

    void onNextActivityNotOK(NavigationAuthorizationEvent evt);

    void onPrevActivityOK(NavigationAuthorizationEvent evt);

    void onPrevActivityNotOK(NavigationAuthorizationEvent evt);
}
