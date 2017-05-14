package kornell.gui.client.presentation.admin.common;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

public class ConfirmModalView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, ConfirmModalView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	static
	Modal confirmModal;
	@UiField
    Label confirmText;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	private Callback<Void, Void> callback;


	public ConfirmModalView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void showModal(String message, Callback<Void, Void> callback) {
		confirmText.setText(message);
		this.callback = callback;
		confirmModal.show();
	}
	
	@UiHandler("btnOK")
	void doOK(ClickEvent e) { 
		callback.onSuccess(null);
		confirmText.setText("");
		this.callback = null;
		confirmModal.hide();
	}
	

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) { 
		hide();
	}

	public void hide() {
		if(callback != null){
			callback.onFailure(null);
		}
		confirmModal.hide();
	}

}