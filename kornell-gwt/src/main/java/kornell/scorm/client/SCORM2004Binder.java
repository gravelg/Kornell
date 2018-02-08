package kornell.scorm.client;

public class SCORM2004Binder {
    public static native void bind(SCORM2004Adapter api) /*-{
        console.debug("Binding SCORM 2004 adapter to window");
        var API_1484_11 = $wnd.API_1484_11 || {};

        $wnd.API_1484_11 = API_1484_11;
        console.debug("SCORM 2004 adapter bound.");
    }-*/;
}
