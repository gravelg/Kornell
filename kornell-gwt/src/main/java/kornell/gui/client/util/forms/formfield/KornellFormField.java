package kornell.gui.client.util.forms.formfield;

import com.google.gwt.user.client.ui.Widget;

public interface KornellFormField<T> {
    public Widget getFieldWidget();

    public String getDisplayText();

    public String getPersistText();
}