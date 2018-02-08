package kornell.gui.client.event;

import com.google.gwt.event.shared.EventHandler;

import kornell.core.to.UserInfoTO;

public interface LoginEventHandler extends EventHandler {

    public void onLogin(UserInfoTO user);

}
