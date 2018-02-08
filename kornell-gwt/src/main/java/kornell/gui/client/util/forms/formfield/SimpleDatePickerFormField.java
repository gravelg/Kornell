package kornell.gui.client.util.forms.formfield;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class SimpleDatePickerFormField implements KornellFormField<TextBox> {

    SimpleDatePicker field;

    public SimpleDatePickerFormField(SimpleDatePicker field) {
        this.field = field;
    }

    @Override
    public Widget getFieldWidget() {
        return field;
    }

    @Override
    public String getDisplayText() {
        return field.getDisplayText();
    }

    @Override
    public String getPersistText() {
        return field.getPersistText();
    }

}
