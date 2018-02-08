package kornell.gui.client.util.forms.formfield;

import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class ListBoxFormField implements KornellFormField<TextBox> {

    ListBox field;

    public ListBoxFormField(ListBox field) {
        this.field = field;
        field.addStyleName("selectField");
    }

    @Override
    public Widget getFieldWidget() {
        return field;
    }

    @Override
    public String getDisplayText() {
        return "-".equals(field.getValue()) ? "" : field.getItemText(field.getSelectedIndex());
    }

    @Override
    public String getPersistText() {
        return "-".equals(field.getValue()) ? null : field.getValue();
    }

}
