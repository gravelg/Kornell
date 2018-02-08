package kornell.gui.client.util.validation;

import com.google.web.bindery.event.shared.Event;

public class ValidationChangedEvent extends Event<ValidationChangedHandler> {
    public static Type<ValidationChangedHandler> TYPE = new Type<ValidationChangedHandler>();

    @Override
    public Type<ValidationChangedHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(ValidationChangedHandler handler) {
        handler.onValidationChanged();
    }
}
