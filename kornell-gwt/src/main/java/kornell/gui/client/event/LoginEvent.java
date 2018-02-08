package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

import kornell.core.to.UserInfoTO;

public class LoginEvent extends GwtEvent<LoginEventHandler> {
    public static final Type<LoginEventHandler> TYPE = new Type<LoginEventHandler>();
    private UserInfoTO user;

    public LoginEvent(UserInfoTO user) {
        this.user = user;
    }

    @Override
    public Type<LoginEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(LoginEventHandler handler) {
        handler.onLogin(user);
    }

}
