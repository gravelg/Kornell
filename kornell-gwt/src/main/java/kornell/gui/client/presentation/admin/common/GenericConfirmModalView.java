package kornell.gui.client.presentation.admin.common;

import com.github.gwtbootstrap.client.ui.Modal;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import kornell.api.client.Callback;

public class GenericConfirmModalView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericConfirmModalView> {
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

	private Callback<Boolean> callback;


	public GenericConfirmModalView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void showModal(String message, Callback<Boolean> callback) {
		confirmText.setText(message);
		this.callback = callback;
		confirmModal.show();
		/*
		btnCancel.setText("Cancelar".toUpperCase());
		btnOK.setText("Salvar".toUpperCase());*/
	}
	
	@UiHandler("btnOK")
	void doOK(ClickEvent e) { 
		callback.ok(true);
		confirmModal.hide();
	}
	

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) { 
		callback.ok(false);
		confirmModal.hide();
	}

}