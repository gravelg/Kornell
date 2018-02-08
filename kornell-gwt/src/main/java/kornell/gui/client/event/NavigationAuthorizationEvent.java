package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class NavigationAuthorizationEvent extends GwtEvent<NavigationAuthorizationEventHandler> {
    public static final Type<NavigationAuthorizationEventHandler> TYPE = new Type<NavigationAuthorizationEventHandler>();

    public enum Forecast {
        NEXT_OK, NEXT_NOT_OK, PREV_OK, PREV_NOT_OK;

        public NavigationAuthorizationEvent get() {
            return new NavigationAuthorizationEvent(this);
        }
    }

    public NavigationAuthorizationEvent(Forecast forecast) {
        this.forecast = forecast;
    }

    Forecast forecast;

    private static final NavigationAuthorizationEvent NEXT_OK = new NavigationAuthorizationEvent(Forecast.NEXT_OK);
    private static final NavigationAuthorizationEvent NEXT_NOT_OK = new NavigationAuthorizationEvent(
            Forecast.NEXT_NOT_OK);
    private static final NavigationAuthorizationEvent PREV_OK = new NavigationAuthorizationEvent(Forecast.PREV_OK);
    private static final NavigationAuthorizationEvent PREV_NOT_OK = new NavigationAuthorizationEvent(
            Forecast.PREV_NOT_OK);

    @Override
    public Type<NavigationAuthorizationEventHandler> getAssociatedType() {
        return TYPE;
    }

    public static NavigationAuthorizationEvent next(boolean isOK) {
        return isOK ? NEXT_OK : NEXT_NOT_OK;
    }

    public static NavigationAuthorizationEvent prev(boolean isOK) {
        return isOK ? PREV_OK : PREV_NOT_OK;
    }

    @Override
    protected void dispatch(NavigationAuthorizationEventHandler handler) {
        switch (forecast) {
        case NEXT_OK:
            handler.onNextActivityOK(this);
            break;
        case NEXT_NOT_OK:
            handler.onNextActivityNotOK(this);
            break;
        case PREV_OK:
            handler.onPrevActivityOK(this);
            break;
        case PREV_NOT_OK:
            handler.onPrevActivityNotOK(this);
            break;
        }
    }

}
