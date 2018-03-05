package kornell.gui.client.sequence;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.web.bindery.event.shared.Event;

public class NavigationRequest extends GwtEvent<NavigationRequest.Handler> {
    public static final Type<NavigationRequest.Handler> TYPE = new Type<NavigationRequest.Handler>();
    public String destination;
    private long ctime;

    private NavigationRequest(String destination) {
        this.ctime = System.currentTimeMillis();
        this.destination = destination;
    }

    public interface Handler extends EventHandler {
        void onContinue(NavigationRequest event);

        void onPrevious(NavigationRequest event);

        void onDirect(NavigationRequest event);
    }

    @Override
    public Type<Handler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(Handler handler) {
        if ("continue".equals(destination))
            handler.onContinue(this);
        else if ("previous".equals(destination))
            handler.onPrevious(this);
        else
            handler.onDirect(this);
    }

    private static final NavigationRequest NEXT = new NavigationRequest("continue");
    private static final NavigationRequest PREV = new NavigationRequest("previous");

    public static NavigationRequest direct(String key) {
        return new NavigationRequest(key);
    }

    public static NavigationRequest next() {
        return NEXT;
    }

    public static NavigationRequest prev() {
        return PREV;
    }

    public static Event<?> valueOf(String string) {
        return new NavigationRequest(string);
    }

    @Override
    public String toString() {
        return "NavigationRequest[" + destination + ";" + ctime + "]";
    }

    public String getDestination() {
        return destination;
    }
}
