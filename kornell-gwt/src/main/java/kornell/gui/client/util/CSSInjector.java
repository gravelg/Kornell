package kornell.gui.client.util;

import static kornell.core.util.StringUtils.mkurl;

import java.util.Date;

import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.JavaScriptObject;

public class CSSInjector {

    public static void injectCSS(String path, Callback<Void, Exception> callback) {
        JavaScriptObject styleElement = getStyleElement(path);
        if (callback != null) {
            attachListeners(styleElement, callback);
        }

        updateSkin(styleElement);
    }

    public static void updateSkin(String skinName, Callback<Void, Exception> callback) {
        String skinPath = mkurl(ClientConstants.CSS_PATH,
                "skin" + skinName + ".nocache.css?v=" + (new Date()).getTime());
        injectCSS(skinPath, callback);
    }

    private static native void updateSkin(JavaScriptObject styleElement) /*-{
        var oldLink = $wnd.document.getElementById('kornellSkin');
        if (oldLink) {
            $wnd.document.head.removeChild(oldLink);
        }

        // IE8 does not have document.head
        ($wnd.document.head || $wnd.document.getElementsByTagName("head")[0])
                .appendChild(styleElement);
    }-*/;

    private static native JavaScriptObject getStyleElement(String skinPath) /*-{
        var link = $wnd.document.createElement('link'), oldLink = $wnd.document
                .getElementById('kornellSkin');
        link.id = 'kornellSkin';
        link.rel = 'stylesheet';
        link.type = 'text/css';
        link.href = skinPath;
        if (oldLink) {
            $wnd.document.head.removeChild(oldLink);
        }
    
        return link;
    }-*/;

    private static native void attachListeners(JavaScriptObject scriptElement, Callback<Void, Exception> callback) /*-{
        function clearCallbacks() {
          scriptElement.onerror = scriptElement.onreadystatechange = scriptElement.onload = null;
        }
        scriptElement.onload = $entry(function() {
          clearCallbacks();
          if (callback) {
            callback.@com.google.gwt.core.client.Callback::onSuccess(Ljava/lang/Object;)(null);
          }
        });
        // or possibly more portable script_tag.addEventListener('error', function(){...}, true);
        scriptElement.onerror = $entry(function() {
          clearCallbacks();
          if (callback) {
            var ex = @com.google.gwt.core.client.CodeDownloadException::new(Ljava/lang/String;)("onerror() called.");
            callback.@com.google.gwt.core.client.Callback::onFailure(Ljava/lang/Object;)(ex);
          }
        });
        scriptElement.onreadystatechange = $entry(function() {
          if (/loaded|complete/.test(scriptElement.readyState)) {
            scriptElement.onload();
          }
        });
      }-*/;

}
