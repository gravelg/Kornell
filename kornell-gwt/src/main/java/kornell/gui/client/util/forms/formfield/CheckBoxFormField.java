package kornell.gui.client.util.forms.formfield;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;

import kornell.gui.client.KornellConstants;

public class CheckBoxFormField implements KornellFormField<CheckBox> {
	
	private static KornellConstants constants = GWT.create(KornellConstants.class);

	CheckBox field;
	
	public CheckBoxFormField(CheckBox field) {
		this.field = field;
		field.addStyleName("input-switch");
	}

	@Override
	public Widget getFieldWidget() {
		return field;
	}

	@Override
	public String getDisplayText() {
		return field.getValue() ? constants.yes() : constants.no();
	}

	@Override
	public String getPersistText() {
		return field.getValue().toString();
	}

}
