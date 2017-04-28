package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import kornell.core.util.StringUtils;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemQuiz;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;

public class WizardSlideItemQuizView extends FlowPanel implements IWizardView {
	
	private FormHelper formHelper = GWT.create(FormHelper.class);
	
	private String countsTowardsCertificateLabel, givesInstantFeedbackLabel, redosCountLabel;
	private KornellFormFieldWrapper countsTowardsCertificate, givesInstantFeedback, redosCount;
	
	private List<KornellFormFieldWrapper> fields;

	private FlowPanel slideItemFields;	

	private String changedString = "(*) ";
	
	private WizardSlideItemView wizardSlideItemView;
	
	private WizardSlideItem wizardSlideItem;
	private WizardSlideItemQuiz wizardSlideQuiz;
	
	private KeyUpHandler refreshFormKeyUpHandler;

	private Presenter presenter;	

	public WizardSlideItemQuizView(WizardSlideItem wizardSlideItem, WizardSlideItemView wizardSlideItemView, Presenter presenter) {
		super();
		this.presenter = presenter;
		this.wizardSlideItem = wizardSlideItem;
		this.wizardSlideItemView = wizardSlideItemView;
		String extra = wizardSlideItem.getExtra() == null ? "{}" : wizardSlideItem.getExtra();
		this.wizardSlideQuiz = AutoBeanCodex.decode(WizardUtils.WIZARD_FACTORY, WizardSlideItemQuiz.class, extra).as();		
		if(wizardSlideQuiz.getRedosCount() == null){
			wizardSlideQuiz.setRedosCount(0);
		}

		this.addStyleName("slideItemWrapper extendedSlideItemWrapper left fillWidth");
		slideItemFields = new FlowPanel();
		slideItemFields.addStyleName("fieldsWrapper");
		this.add(slideItemFields);
		
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

		countsTowardsCertificateLabel = "Nota conta para o certificado";
		countsTowardsCertificate = new KornellFormFieldWrapper(countsTowardsCertificateLabel, formHelper.createCheckBoxFormField(wizardSlideQuiz.getCountsTowardsCertificate()), true, null,
				"Deixe essa opção desmarcada no caso desse quiz ser somente um exercício de aprendizado.");
		fields.add(countsTowardsCertificate);
		slideItemFields.add(countsTowardsCertificate);
		((CheckBox)countsTowardsCertificate.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				wizardSlideItemView.refreshForm();
			}
		});

		givesInstantFeedbackLabel = "Mostrar respostas corretas";
		givesInstantFeedback = new KornellFormFieldWrapper(givesInstantFeedbackLabel, formHelper.createCheckBoxFormField(wizardSlideQuiz.getGivesInstantFeedback()), true, null, 
				"Ao final de cada questão desse quiz, o participante terá acesso à resposta correta, além de uma nota opcional com mais explicações.");
		fields.add(givesInstantFeedback);
		slideItemFields.add(givesInstantFeedback);
		((CheckBox)givesInstantFeedback.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				wizardSlideItemView.refreshForm();
			}
		});

		redosCountLabel = "Quantas Tentativas";
		redosCount = new KornellFormFieldWrapper(redosCountLabel, formHelper.createTextBoxFormField(""+wizardSlideQuiz.getRedosCount()), true, null, 
				"Deixe Zero ou em branco para tentativas eliminadas. Caso definido um número, assim que o participante usar todas as tentativas, o quiz não poderá ser mais refeito.");
		((TextBox)redosCount.getFieldWidget()).addKeyUpHandler(refreshFormKeyUpHandler);
		((TextBox)redosCount.getFieldWidget()).getElement().setAttribute("type", "number");
		fields.add(redosCount);
		slideItemFields.add(redosCount);		
		
	}

	@Override
	public void resetFormToOriginalValues(){	
        ((CheckBox)countsTowardsCertificate.getFieldWidget()).setValue(wizardSlideQuiz.getCountsTowardsCertificate());
        ((CheckBox)givesInstantFeedback.getFieldWidget()).setValue(wizardSlideQuiz.getGivesInstantFeedback());
		((TextBox)redosCount.getFieldWidget()).setText(""+wizardSlideQuiz.getRedosCount());

		presenter.valueChanged(wizardSlideItem, false);
		refreshForm();
	}

	@Override
	public boolean refreshForm(){
		boolean valueHasChanged = refreshFormElementLabel(countsTowardsCertificate, countsTowardsCertificateLabel, ""+wizardSlideQuiz.getCountsTowardsCertificate())
				|| refreshFormElementLabel(givesInstantFeedback, givesInstantFeedbackLabel, ""+wizardSlideQuiz.getGivesInstantFeedback())
				|| refreshFormElementLabel(redosCount, redosCountLabel, ""+wizardSlideQuiz.getRedosCount());
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

		return !formHelper.checkErrors(fields);
	}

	@Override
	public void updateWizard() {	
		if(StringUtils.isNone(redosCount.getFieldPersistText())){
			wizardSlideQuiz.setRedosCount(0);
			((TextBox)redosCount.getFieldWidget()).setValue("0");
		}
		wizardSlideQuiz.setCountsTowardsCertificate(countsTowardsCertificate.getFieldPersistText().equals("true"));
		wizardSlideQuiz.setGivesInstantFeedback(givesInstantFeedback.getFieldPersistText().equals("true"));
		wizardSlideQuiz.setRedosCount(redosCount.getFieldPersistText().length() > 0 ?
                new Integer(redosCount.getFieldPersistText()) :
                    null);

		wizardSlideItem.setExtra(AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(wizardSlideQuiz)).getPayload().toString());
		presenter.valueChanged(wizardSlideQuiz, false);	
		refreshForm();	
	}

	public WizardSlideItem getWizardSlideItem() {
		return wizardSlideItem;
	}
}