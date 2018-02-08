package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class MenuLeftWelcomeEvent extends GwtEvent<MenuLeftWelcomeEventHandler> {

    public static final Type<MenuLeftWelcomeEventHandler> TYPE = new Type<MenuLeftWelcomeEventHandler>();

    private String menuLeftItemSelected;

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<MenuLeftWelcomeEventHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(MenuLeftWelcomeEventHandler handler) {
        handler.onItemSelected(this);
    }

    public String getMenuLeftItemSelected() {
        return menuLeftItemSelected;
    }

    public void setMenuLeftItemSelected(String menuLeftItemSelected) {
        this.menuLeftItemSelected = menuLeftItemSelected;
    }

}
