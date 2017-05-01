package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemQuiz;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;

public class WizardSlideItemQuizQuestionView extends Composite implements IWizardView {
	
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	
	private KornellFormFieldWrapper url;
	private List<KornellFormFieldWrapper> fields;

	private FlowPanel slideItemWrapper;
	private FlowPanel slideItemFields;	
	
	private WizardSlideItem wizardSlideItem;
	private WizardSlideItemQuiz wizardSlideQuiz;

	private Presenter presenter;	

	public WizardSlideItemQuizQuestionView(WizardSlideItem wizardSlideItem, WizardSlideItemView wizardSlideItemView, Presenter presenter) {
		this.presenter = presenter;
		this.wizardSlideItem = wizardSlideItem;
		String extra = wizardSlideItem.getExtra() == null ? "{}" : wizardSlideItem.getExtra();
		this.wizardSlideQuiz = AutoBeanCodex.decode(WizardUtils.WIZARD_FACTORY, WizardSlideItemQuiz.class, extra).as();

		slideItemWrapper = new FlowPanel();
		slideItemFields = new FlowPanel();
		slideItemWrapper.add(slideItemFields);
		init();
	}

	public void init() {
		fields = new ArrayList<KornellFormFieldWrapper>();
		slideItemFields.clear();	
		/*
		refreshFormKeyUpHandler = new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent event) {
				wizardSlideItemView.refreshForm();
			}
		};

		
		urlLabel = "URL da imagem";
		
		*/
		/*url = new KornellFormFieldWrapper(urlLabel, formHelper.createTextBoxFormField(wizardSlideQuiz.getURL()), true);
		((TextBox)url.getFieldWidget()).addKeyUpHandler(refreshFormKeyUpHandler);
		fields.add(url);
		slideItemFields.add(url);		*/
	}

	@Override
	public void resetFormToOriginalValues(){	
		//((TextBox)url.getFieldWidget()).setText(wizardSlideQuiz.getURL());

		presenter.valueChanged(wizardSlideItem, false);
		refreshForm();
	}

	@Override
	public boolean refreshForm(){
		//boolean valueHasChanged = refreshFormElementLabel(url, urlLabel, wizardSlideQuiz.getURL());
		boolean valueHasChanged = false;
		presenter.valueChanged(wizardSlideItem, valueHasChanged);
		validateFields();
		
		return valueHasChanged;
	}
	 /*
	private boolean refreshFormElementLabel(KornellFormFieldWrapper kornellFormFieldWrapper, String label, String originalValue){
		boolean valueHasChanged = !kornellFormFieldWrapper.getFieldPersistText().equals(originalValue);
		kornellFormFieldWrapper.setFieldLabelText((valueHasChanged ? changedString  : "") + label);
		return valueHasChanged;
	}*/

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
		//wizardSlideQuiz.setURL(url.getFieldPersistText());

		wizardSlideItem.setExtra(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(wizardSlideQuiz)).getPayload().toString());
		presenter.valueChanged(wizardSlideQuiz, false);	
		refreshForm();	
	}

	public WizardSlideItem getWizardSlideItem() {
		return wizardSlideItem;
	}

	public String getUrl() {
		return url.getFieldPersistText();
	}
}