package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface ProgressEventHandler extends EventHandler {

    void onProgress(ProgressEvent event);
}
