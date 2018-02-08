package kornell.gui.client.util.validation;

import java.util.List;

import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.Callback;

public interface Validator {

    void getErrors(Widget widget, Callback<List<String>> cb);

}
