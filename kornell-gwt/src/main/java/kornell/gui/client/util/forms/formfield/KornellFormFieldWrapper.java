package kornell.gui.client.util.forms.formfield;

import static kornell.core.util.StringUtils.isNone;

import java.util.List;

import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.HasChangeHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

import kornell.api.client.Callback;
import kornell.gui.client.util.validation.ValidationChangedEvent;
import kornell.gui.client.util.validation.ValidationChangedHandler;
import kornell.gui.client.util.validation.Validator;

public class KornellFormFieldWrapper extends Composite {
	EventBus fieldBus = new SimpleEventBus();
	
	interface MyUiBinder extends UiBinder<Widget, KornellFormFieldWrapper> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	@UiField
	FlowPanel fieldPanelWrapper;
	@UiField
	FlowPanel labelPanel;
	@UiField
	Label fieldLabel;
	@UiField
	FlowPanel fieldPanel;

	TextBox fieldTextBox;
	ListBox fieldListBox;
	SimpleDatePicker fieldSimpleDatePicker;

	Label fieldError;
	Label fieldTxt;

	boolean isEditMode;
	KornellFormField<?> formField;

	private Validator validator;

	private Timer updateTimer;

	public KornellFormFieldWrapper(String label, KornellFormField<?> formField) {
		this(label, formField, true,null);
	}

	public KornellFormFieldWrapper(String label, KornellFormField<?> formField,
			boolean isEditMode){
		this(label, formField, isEditMode, null);
	}
	
	public KornellFormFieldWrapper(String label, KornellFormField<?> formField,
			boolean isEditMode, Validator validator) {
			this(label, formField, isEditMode, validator, null);
	}
	
	public KornellFormFieldWrapper(String label, KornellFormField<?> formField,
			boolean isEditMode, Validator validator, String tooltipText) {

		initWidget(uiBinder.createAndBindUi(this));
		fieldLabel.setText(label);
		if(tooltipText != null && isEditMode){
			Icon icon = new Icon();
			icon.addStyleName("fa fa-question-circle");
			Tooltip tooltip = new Tooltip(tooltipText);
			tooltip.setPlacement(Placement.RIGHT);
			tooltip.add(icon);
			labelPanel.add(tooltip);
		}
		this.formField = formField;
		this.isEditMode = isEditMode;
		this.validator = validator;
		initData(formField);

		updateTimer = new Timer() {
			@Override
			public void run() {
				validate();
			}
		};
	}

	public void initData(KornellFormField<?> formField) {
		fieldPanel.clear();
		this.formField = formField;
		if (!isEditMode) {
			fieldTxt = new Label();
			fieldTxt.addStyleName("lblValue");
			fieldTxt.setText(formField.getDisplayText());
			fieldPanel.add(fieldTxt);
		} else {
			final Widget fieldWidget = formField.getFieldWidget();
			fieldPanel.add(fieldWidget);

			if (fieldWidget instanceof HasKeyUpHandlers) {
				HasKeyUpHandlers ku = (HasKeyUpHandlers) fieldWidget;
				if(validator != null){
					ku.addKeyUpHandler(new KeyUpHandler() {
						@Override
						public void onKeyUp(KeyUpEvent event) {
							scheduleValidation();							
						}
					});
				}
			}

			if (fieldWidget instanceof HasChangeHandlers) {
				HasChangeHandlers ku = (HasChangeHandlers) fieldWidget;
				if(validator != null){
					ku.addChangeHandler(new ChangeHandler() {
						@Override
						public void onChange(ChangeEvent event) {
							scheduleValidation();							
						}
					});
				}
			}

			fieldError = new Label();
			fieldError.addStyleName("error");
			fieldPanel.add(fieldError);
		}
	}

	private void validate() {
		validator.getErrors(formField.getFieldWidget(), new Callback<List<String>>() {
			@Override
			public void ok(List<String> errors) {
				if(errors.isEmpty()){
					setError("");
				}else {
					showErrors(errors);
				}
				fieldBus.fireEvent(new ValidationChangedEvent());
				
			}
		});
	}
	
	public void scheduleValidation(){
		updateTimer.cancel();
		updateTimer.schedule(500);
	}

	private void showErrors(List<String> errorKeys) {
		StringBuilder buf = new StringBuilder();
		for(String e:errorKeys){
			buf.append(e);
		}
		setError(buf.toString());
	}

	public KornellFormField<?> getFormField() {
		return formField;
	}
	
	public void setFieldLabelText(String text) {
		fieldLabel.setText(text);
	}

	public Widget getFieldWidget() {
		return formField.getFieldWidget();
	}

	public String getFieldDisplayText() {
		return formField.getDisplayText();
	}

	public String getFieldPersistText() {
		return formField.getPersistText();
	}

	public void setError(String text) {
		if(fieldError != null)
			fieldError.setText(text);
	}

	public String getError() {
		return fieldError != null ? fieldError.getText() : "";
	}

	public void clearError() {
		if (fieldError != null) {
			fieldError.setText("");
		}
	}
	
	public void addStyleName(String styleName){
		fieldPanelWrapper.addStyleName(styleName);
	}
	
	public boolean isValid(){		
		return fieldError == null || isNone(fieldError.getText());
	}

	public void addValidationListener(ValidationChangedHandler handler) {
		fieldBus.addHandler(ValidationChangedEvent.TYPE, handler);
	}
	
}