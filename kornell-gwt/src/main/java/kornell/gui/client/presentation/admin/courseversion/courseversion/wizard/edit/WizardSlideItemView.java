package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.edit;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.Icon;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;

public class WizardSlideItemView extends Composite implements IWizardView {
	interface MyUiBinder extends UiBinder<Widget, WizardSlideItemView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	
	private String titleLabel, textLabel;
	private KornellFormFieldWrapper title, text;
	private List<KornellFormFieldWrapper> fields;
	private IWizardView extendedItemView;
	
	private Integer displayOrder;
	
	@UiField	
	FlowPanel slideItemWrapper;
	@UiField
	Icon slideItemIcon;
	@UiField
	Label slideItemLabel;
	@UiField
	Button btnDelete;
	@UiField
	Button btnMoveUp;
	@UiField
	Button btnMoveDown;

	private String changedString = "(*) ";
	
	private WizardSlideItem wizardSlideItem;
	private KeyUpHandler refreshFormKeyUpHandler;
	private Presenter presenter;
	private WizardSlideView wizardSlideView;
	FlowPanel slideItemFields;	
	Image slideItemPreviewImage;
    
	private boolean isQuizItem;

	public WizardSlideItemView(WizardSlideItem wizardSlideItem, Presenter presenter, WizardSlideView wizardSlideView) {
		this.presenter = presenter;
		this.wizardSlideItem = wizardSlideItem;
		this.displayOrder = wizardSlideItem.getOrder();
		this.wizardSlideView = wizardSlideView;
		initWidget(uiBinder.createAndBindUi(this));
		init();
	}
	
	public void init() {
		WizardUtils.createIcon(btnDelete, "fa-trash-o");
		WizardUtils.createIcon(btnMoveUp, "fa-arrow-up");
		WizardUtils.createIcon(btnMoveDown, "fa-arrow-down");
		
		slideItemIcon.addStyleName(WizardUtils.getClasForWizardSlideItemViewIcon(wizardSlideItem.getWizardSlideItemType()));
		slideItemLabel.setText(getItemLabelText());
		fields = new ArrayList<KornellFormFieldWrapper>();

		slideItemFields = new FlowPanel();
		
		isQuizItem = WizardSlideItemType.QUIZ.equals(wizardSlideItem.getWizardSlideItemType());
		
		if(isQuizItem){
		    slideItemFields.addStyleName("quizWrapper");
		    
			extendedItemView = new WizardSlideItemQuizView(wizardSlideItem, this, presenter);
			slideItemFields.add((WizardSlideItemQuizView)extendedItemView);	

			btnMoveUp.setVisible(false);
			btnMoveDown.setVisible(false);
			btnDelete.setVisible(false);
		} else {
		    slideItemFields.addStyleName("fieldsWrapper");
		    
			refreshFormKeyUpHandler = new KeyUpHandler() {
				@Override
				public void onKeyUp(KeyUpEvent event) {
					refreshForm();				
				}
			};

			titleLabel = "Título do Item";
			title = new KornellFormFieldWrapper(titleLabel, formHelper.createTextBoxFormField(wizardSlideItem.getTitle()), true);
			((TextBox)title.getFieldWidget()).addKeyUpHandler(refreshFormKeyUpHandler);
			fields.add(title);
			slideItemFields.add(title);	

			textLabel = "Texto";
			text = new KornellFormFieldWrapper(textLabel, formHelper.createTextAreaFormField(wizardSlideItem.getText(), 5), true);
			((TextArea)text.getFieldWidget()).addKeyUpHandler(refreshFormKeyUpHandler);
			text.addStyleName("heightAuto marginBottom25");
			fields.add(text);
			slideItemFields.add(text);	
			
			switch (wizardSlideItem.getWizardSlideItemType()) {
			case IMAGE:
				extendedItemView = new WizardSlideItemImageView(wizardSlideItem, this, presenter);
				slideItemFields.add((WizardSlideItemImageView)extendedItemView);
				break;
			case TEXT:
				break;
			case VIDEO_LINK:
				extendedItemView = new WizardSlideItemVideoLinkView(wizardSlideItem, this, presenter);
				slideItemFields.add((WizardSlideItemVideoLinkView)extendedItemView);	
				break;
			default:
				break;
			}

			btnMoveUp.setVisible(displayOrder > 0);
			btnMoveDown.setVisible(displayOrder < (((WizardSlide)presenter.getSelectedWizardElement()).getWizardSlideItems().size() -1));
			btnDelete.setVisible(true);
		}		
		
		slideItemWrapper.add(slideItemFields);
		updatePreviewImage();
	}
	
	@UiHandler("btnMoveDown")
	void doMoveDown(ClickEvent e) {
		wizardSlideView.moveDownItem(wizardSlideItem);
	}
	
	@UiHandler("btnMoveUp")
	void doMoveUp(ClickEvent e) {
		wizardSlideView.moveUpItem(wizardSlideItem);
	}
	
	@UiHandler("btnDelete")
	void doDelete(ClickEvent e) {
		wizardSlideView.showModal(WizardSlideView.MODAL_DELETE_SLIDE_ITEM, wizardSlideItem);
	}


	@Override
	public void resetFormToOriginalValues(){	
		if(!isQuizItem){
			((TextBox)title.getFieldWidget()).setText(wizardSlideItem.getTitle());
			((TextArea)text.getFieldWidget()).setText(wizardSlideItem.getText());
		}

		btnMoveUp.setVisible(displayOrder > 0);
		btnMoveDown.setVisible(displayOrder < (((WizardSlide)presenter.getSelectedWizardElement()).getWizardSlideItems().size() -1));
		btnDelete.setVisible(true);
		
		if(extendedItemView != null){
			extendedItemView.resetFormToOriginalValues();
		}
		
		this.displayOrder = wizardSlideItem.getOrder();

		presenter.valueChanged(wizardSlideItem, false);
		refreshForm();
	}

	@Override
	public boolean refreshForm(){
		boolean valueHasChanged = !wizardSlideItem.getOrder().equals(displayOrder) ||
				refreshFormElementLabel(title, titleLabel, wizardSlideItem.getTitle()) || 
				refreshFormElementLabel(text, textLabel, wizardSlideItem.getText()) ||
				wizardSlideItem.getUUID().startsWith("new");
		if(extendedItemView != null){
			valueHasChanged = valueHasChanged || extendedItemView.refreshForm();			
			updatePreviewImage();
		}
		
		presenter.valueChanged(wizardSlideItem, valueHasChanged);
		String itemLabelText = getItemLabelText();
		slideItemLabel.setText((valueHasChanged ? changedString  : "") + itemLabelText);			
		
		validateFields();

		btnMoveUp.setVisible(displayOrder > 0);
		btnMoveDown.setVisible(displayOrder < (wizardSlideView.getWizardSlideItemViewCount()-1));
		btnDelete.setVisible(!isQuizItem);
		
		return valueHasChanged;
	}

	private void updatePreviewImage() {
		if(slideItemPreviewImage == null
				&& (WizardSlideItemType.VIDEO_LINK.equals(wizardSlideItem.getWizardSlideItemType()) || 
						WizardSlideItemType.IMAGE.equals(wizardSlideItem.getWizardSlideItemType())
					)
			){
			slideItemPreviewImage = new Image();
			slideItemPreviewImage.addStyleName("slideItemPreviewImage");
			slideItemWrapper.add(slideItemPreviewImage);
		}
		if(WizardSlideItemType.VIDEO_LINK.equals(wizardSlideItem.getWizardSlideItemType())){
			String url = ((WizardSlideItemVideoLinkView)extendedItemView).getUrl();
			String youtubeId = WizardUtils.stripIdFromVideoURL(url);
			slideItemPreviewImage.setUrl("http://img.youtube.com/vi/"+youtubeId+"/sddefault.jpg");
		} else if(WizardSlideItemType.IMAGE.equals(wizardSlideItem.getWizardSlideItemType())){
			String url = ((WizardSlideItemImageView)extendedItemView).getUrl();
			slideItemPreviewImage.setUrl(url);
		}
	}

	private String getItemLabelText() {
		String itemLabelText = WizardUtils.getItemNameByType(wizardSlideItem.getWizardSlideItemType()) +
				" - " +
				wizardSlideItem.getParentOrder() + "." + (wizardSlideItem.getOrder()+1);
		return itemLabelText;
	}
	 
	private boolean refreshFormElementLabel(KornellFormFieldWrapper kornellFormFieldWrapper, String label, String originalValue){
		if(isQuizItem){
			return false;
		}
		boolean valueHasChanged = !kornellFormFieldWrapper.getFieldPersistText().equals(originalValue);
		kornellFormFieldWrapper.setFieldLabelText((valueHasChanged ? changedString  : "") + label);
		return valueHasChanged;
	}

	@Override
	public boolean validateFields() {		
		formHelper.clearErrors(fields);

		if (WizardSlideItemType.TEXT.equals(wizardSlideItem.getWizardSlideItemType()) &&
				!formHelper.isLengthValid(title.getFieldPersistText(), 2)) {
			title.setError("Insira o título");
		}
		if (WizardSlideItemType.TEXT.equals(wizardSlideItem.getWizardSlideItemType()) &&
				!formHelper.isLengthValid(text.getFieldPersistText(), 2)) {
			text.setError("Insira o texto");
		}
		
		boolean extendedItemViewValidated = true;
		if(extendedItemView != null){
			extendedItemViewValidated = extendedItemView.validateFields();
		}
		
		return extendedItemViewValidated && !formHelper.checkErrors(fields);
	}

	@Override
	public void updateWizard() {		
		if(!isQuizItem){
			wizardSlideItem.setTitle(title.getFieldPersistText());
			wizardSlideItem.setText(text.getFieldPersistText());
		}
		wizardSlideItem.setOrder(displayOrder);
		
		if(extendedItemView != null){
			extendedItemView.updateWizard();
		}
		
		presenter.valueChanged(wizardSlideItem, false);	
		refreshForm();	
	}

	public WizardSlideItem getWizardSlideItem() {
		return wizardSlideItem;
	}

	public Integer getDisplayOrder() {
		return displayOrder;
	}

	public void setDisplayOrder(Integer displayOrder) {
		this.displayOrder = displayOrder;
		refreshForm();
	}
}