package kornell.gui.client.presentation.admin.assets.generic;

import java.util.Map;

import com.github.gwtbootstrap.client.ui.Button;
import com.github.gwtbootstrap.client.ui.FileUpload;
import com.github.gwtbootstrap.client.ui.Tooltip;
import com.github.gwtbootstrap.client.ui.constants.Placement;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.CertificateDetails;
import kornell.core.entity.CourseDetailsHint;
import kornell.core.entity.CourseDetailsLibrary;
import kornell.core.entity.CourseDetailsSection;
import kornell.core.entity.EntityFactory;
import kornell.core.to.CourseDetailsHintsTO;
import kornell.core.to.CourseDetailsLibrariesTO;
import kornell.core.to.CourseDetailsSectionsTO;
import kornell.gui.client.presentation.admin.assets.AdminAssetsPresenter;
import kornell.gui.client.presentation.admin.assets.AdminAssetsView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.forms.formfield.PeopleMultipleSelect;

public class GenericAdminAssetsView extends Composite implements AdminAssetsView {
	interface MyUiBinder extends UiBinder<Widget, GenericAdminAssetsView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	
	PeopleMultipleSelect peopleMultipleSelect;

	@UiField
	Label thumbSubTitle;
	@UiField
	FlowPanel thumbFieldPanel;
	@UiField
	Label certificateDetailsSubTitle;
	@UiField
	FlowPanel certificateDetailsFieldPanel;
	@UiField
	Button sectionsAddButton;
	@UiField
	Label sectionsSubTitle;
	@UiField
	FlowPanel sectionsFieldPanel;
	@UiField
	Button hintsAddButton;
	@UiField
	Label hintsSubTitle;
	@UiField
	FlowPanel hintsFieldPanel;
	@UiField
	Button librariesAddButton;
	@UiField
	Label librariesSubTitle;
	@UiField
	FlowPanel librariesFieldPanel;

	private Presenter presenter;
	private Map<String, String> info;
	
	public GenericAdminAssetsView(KornellSession session, EventBus bus) {
		initWidget(uiBinder.createAndBindUi(this));
		WizardUtils.createIcon(sectionsAddButton, "fa-plus-circle");
		WizardUtils.createIcon(hintsAddButton, "fa-plus-circle");
		WizardUtils.createIcon(librariesAddButton, "fa-plus-circle");
	}

	@Override
	public void initData() {
		info = presenter.getInfo();
	}

	@Override
	public void initThumb(boolean exists) {
		thumbSubTitle.setText(info.get("thumbSubTitle"));
		thumbFieldPanel.clear();
		thumbFieldPanel.add(buildFileUploadPanel(AdminAssetsPresenter.THUMB_FILENAME, AdminAssetsPresenter.IMAGE_JPG, AdminAssetsPresenter.THUMB_DESCRIPTION, exists));
	}

	@Override
	public void initCertificateDetails(CertificateDetails certificateDetails) {
		certificateDetailsSubTitle.setText(info.get("certificateDetailsSubTitle"));
		certificateDetailsFieldPanel.clear();
		certificateDetailsFieldPanel.add(buildFileUploadPanel(AdminAssetsPresenter.CERTIFICATE_FILENAME, AdminAssetsPresenter.IMAGE_JPG, AdminAssetsPresenter.CERTIFICATE_DESCRIPTION, certificateDetails.getUUID() != null));
	}

	@Override
	public void initCourseDetailsSections(CourseDetailsSectionsTO courseDetailsSectionsTO) {
		sectionsSubTitle.setText(info.get("sectionsSubTitle"));
		sectionsFieldPanel.clear();
		for(CourseDetailsSection courseDetailsSection : courseDetailsSectionsTO.getCourseDetailsSections()){
			buildSectionItem(courseDetailsSection);
		}
	}

	private void buildSectionItem(CourseDetailsSection courseDetailsSection) {
	}

	@Override
	public void initCourseDetailsHints(CourseDetailsHintsTO courseDetailsHintsTO) {
		hintsSubTitle.setText(info.get("hintsSubTitle"));
		hintsFieldPanel.clear();
		for(CourseDetailsHint courseDetailsHint : courseDetailsHintsTO.getCourseDetailsHints()){
			buildHintItem(courseDetailsHint);
		}
	}

	private void buildHintItem(CourseDetailsHint courseDetailsHint) {
	}

	@Override
	public void initCourseDetailsLibraries(CourseDetailsLibrariesTO courseDetailsLibrariesTO) {
		librariesSubTitle.setText(info.get("librariesSubTitle"));
		librariesFieldPanel.clear();
		for(CourseDetailsLibrary courseDetailsLibrary : courseDetailsLibrariesTO.getCourseDetailsLibraries()){
			buildLibraryItem(courseDetailsLibrary);
		}
	}

	private void buildLibraryItem(CourseDetailsLibrary courseDetailsLibrary) {
	}

	private FlowPanel buildFileUploadPanel(final String fileName, final String contentType, String label, boolean exists) {
		// Create a FormPanel and point it at a service
	    final FormPanel form = new FormPanel();
	    final String elementId = fileName.replace('.', '-');

	    // Create a panel to hold all of the form widgets
		FlowPanel fieldPanelWrapper = new FlowPanel();
		fieldPanelWrapper.addStyleName("fieldPanelWrapper fileUploadPanel");
	    form.setWidget(fieldPanelWrapper);
		
	    // Create the label panel
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		Label lblLabel = new Label(label);
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		fieldPanelWrapper.add(labelPanel);

		// Create the FileUpload component
		FlowPanel fileUploadPanel = new FlowPanel();
		FileUpload fileUpload = new FileUpload();
		fileUpload.setName("uploadFormElement");
		fileUpload.setId(elementId);
		fileUploadPanel.add(fileUpload);
		fieldPanelWrapper.add(fileUpload);
		
	    // Add an ok button to the form
		Button btnOK = new Button();
		WizardUtils.createIcon(btnOK, "fa-floppy-o");
		btnOK.addStyleName("btnAction");
		btnOK.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				presenter.getUploadURL(contentType, elementId, fileName);
			}
		});
		Tooltip tooltipOK = new Tooltip("Salvar");
		tooltipOK.setPlacement(Placement.TOP);
		tooltipOK.add(btnOK);
		fieldPanelWrapper.add(tooltipOK);
		
		if(exists) {
		    // Add an delete button to the form
			Button btnDelete = new Button();
			WizardUtils.createIcon(btnDelete, "fa-trash");
			btnDelete.addStyleName("btnSelected");
			btnDelete.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.delete(fileName);
				}
			});
			Tooltip tooltipDelete = new Tooltip("Excluir");
			tooltipDelete.setPlacement(Placement.TOP);
			tooltipDelete.add(btnDelete);
			fieldPanelWrapper.add(tooltipDelete);
			
			Anchor anchor = new Anchor();
			anchor.setHTML("<icon class=\"fa fa-eye\"></i>");
			anchor.setTitle("Visualizar");
			anchor.setHref(presenter.getFileURL(fileName));
			anchor.setTarget("_blank");
			Tooltip tooltipView = new Tooltip("Visualizar");
			tooltipView.setPlacement(Placement.TOP);
			tooltipView.add(anchor);
			fieldPanelWrapper.add(tooltipView);
		}
	    
		return fieldPanelWrapper;
	}

	@Override
	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}
	
}