package kornell.gui.client.util;

public class Console {
    public static native void log(String msg) /*-{
        console.log(msg);
    }-*/;
}
