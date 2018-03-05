package kornell.gui.client.util.forms.formfield;

import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class TextAreaFormField implements KornellFormField<TextBox> {

    public static final String CPF = "cpf";

    TextArea field;

    public TextAreaFormField(TextArea field) {
        this.field = field;
        field.addStyleName("field");
        field.addStyleName("textField");

    }

    @Override
    public Widget getFieldWidget() {
        return field;
    }

    @Override
    public String getDisplayText() {
        return field.getValue();
    }

    @Override
    public String getPersistText() {
        return field.getValue();
    }

}
