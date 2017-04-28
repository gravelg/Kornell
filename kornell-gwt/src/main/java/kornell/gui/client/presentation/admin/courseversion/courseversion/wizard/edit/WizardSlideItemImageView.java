package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemImage;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;

public class WizardSlideItemImageView extends Composite implements IWizardView {
	interface MyUiBinder extends UiBinder<Widget, WizardSlideItemImageView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	
	private String urlLabel;
	private KornellFormFieldWrapper url;
	private List<KornellFormFieldWrapper> fields;

	@UiField	
	FlowPanel slideItemWrapper;
	@UiField
	FlowPanel slideItemFields;	

	private String changedString = "(*) ";
	
	private WizardSlideItemView wizardSlideItemView;
	
	private WizardSlideItem wizardSlideItem;
	private WizardSlideItemImage wizardSlideItemImage;
	
	private KeyUpHandler refreshFormKeyUpHandler;

	private Presenter presenter;	

	public WizardSlideItemImageView(WizardSlideItem wizardSlideItem, WizardSlideItemView wizardSlideItemView, Presenter presenter) {
		this.presenter = presenter;
		this.wizardSlideItem = wizardSlideItem;
		this.wizardSlideItemView = wizardSlideItemView;
		String extra = wizardSlideItem.getExtra();
		extra = extra == null ? "{}" : extra;
		this.wizardSlideItemImage = AutoBeanCodex.decode(WizardUtils.WIZARD_FACTORY, WizardSlideItemImage.class, extra).as();
		initWidget(uiBinder.createAndBindUi(this));
		init();
	}

	public void init() {
		fields = new ArrayList<KornellFormFieldWrapper>();
		slideItemFields.clear();	
		
		refreshFormKeyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				wizardSlideItemView.refreshForm();
			}
		};

		urlLabel = "URL da imagem";
		url = new KornellFormFieldWrapper(urlLabel, formHelper.createTextBoxFormField(wizardSlideItemImage.getURL()), true);
		((TextBox)url.getFieldWidget()).addKeyUpHandler(refreshFormKeyUpHandler);
		fields.add(url);
		slideItemFields.add(url);		
	}

	@Override
	public void resetFormToOriginalValues(){	
		((TextBox)url.getFieldWidget()).setText(wizardSlideItemImage.getURL());

		presenter.valueChanged(wizardSlideItem, false);
		refreshForm();
	}

	@Override
	public boolean refreshForm(){
		boolean valueHasChanged = refreshFormElementLabel(url, urlLabel, wizardSlideItemImage.getURL());
		presenter.valueChanged(wizardSlideItem, valueHasChanged);
		validateFields();
		
		return valueHasChanged;
	}
	 
	private boolean refreshFormElementLabel(KornellFormFieldWrapper kornellFormFieldWrapper, String label, String originalValue){
		boolean valueHasChanged = !kornellFormFieldWrapper.getFieldPersistText().equals(originalValue);
		kornellFormFieldWrapper.setFieldLabelText((valueHasChanged ? changedString  : "") + label);
		return valueHasChanged;
	}

	@Override
	public boolean validateFields() {		
		formHelper.clearErrors(fields);

		if (!formHelper.isLengthValid(url.getFieldPersistText(), 2, 100)) {
			url.setError("Insira a URL");
		}

		return !formHelper.checkErrors(fields);
	}

	@Override
	public void updateWizard() {
		wizardSlideItemImage.setURL(url.getFieldPersistText());

		wizardSlideItem.setExtra(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(wizardSlideItemImage)).getPayload().toString());
		presenter.valueChanged(wizardSlideItemImage, false);	
		refreshForm();	
	}

	public WizardSlideItem getWizardSlideItem() {
		return wizardSlideItem;
	}

	public String getUrl() {
		return url.getFieldPersistText();
	}
}