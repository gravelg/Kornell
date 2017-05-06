package kornell.gui.client.presentation.admin.assets.generic;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.entity.CourseDetailsHint;
import kornell.core.entity.CourseDetailsLibrary;
import kornell.core.entity.CourseDetailsSection;
import kornell.core.error.KornellErrorTO;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.assets.AdminAssetsPresenter;
import kornell.gui.client.presentation.admin.assets.AdminAssetsView.Presenter;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.view.KornellNotification;

public class GenericAssetsFormView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericAssetsFormView> {
	}


	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private EventBus bus;
	private FormHelper formHelper;

	@UiField
	Modal assetModal;
	@UiField
	FlowPanel assetFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;

	private KornellFormFieldWrapper title, text, fontAwesomeClassName;
	private List<KornellFormFieldWrapper> fields;
	private Presenter presenter;
	private String assetType;
	private CourseDetailsHint courseDetailsHint;
	private CourseDetailsSection courseDetailsSection;
	private CourseDetailsLibrary courseDetailsLibrary;
	private KornellSession session;
	private String saveMode;


	public GenericAssetsFormView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void initializeModal(EventBus bus, KornellSession session, Presenter presenter) {
		this.bus = bus;
		this.session = session;
		this.presenter = presenter;
		formHelper = new FormHelper();
		fields = new ArrayList<KornellFormFieldWrapper>();
		btnCancel.setText("Cancelar".toUpperCase());
		btnOK.setText("Salvar".toUpperCase());
	}

	private boolean validateFields() {
		
		if(!formHelper.isLengthValid(title.getFieldPersistText(), 2)){
			title.setError("Insira o título");
		} else {
			if(AdminAssetsPresenter.SECTION.equals(assetType)){
				for(CourseDetailsSection courseDetailsSection : presenter.getCourseDetailsSectionsTO().getCourseDetailsSections()){
					if(courseDetailsSection.getTitle().equals(title.getFieldPersistText()) && courseDetailsSection.getUUID() != this.courseDetailsSection.getUUID()){
						title.setError("Já existe uma seção com esse título.");
					}
				}
				
			} else if(AdminAssetsPresenter.HINT.equals(assetType)){
				for(CourseDetailsHint courseDetailsHint : presenter.getCourseDetailsHintsTO().getCourseDetailsHints()){
					if(courseDetailsHint.getTitle().equals(title.getFieldPersistText())){
						title.setError("Já existe uma dica com esse título.");
					}
				}
				
			} else if(AdminAssetsPresenter.LIBRARY.equals(assetType)){
				for(CourseDetailsLibrary courseDetailsLibrary : presenter.getCourseDetailsLibrariesTO().getCourseDetailsLibraries()){
					if(courseDetailsLibrary.getTitle().equals(title.getFieldPersistText())){
						title.setError("Já existe um arquivo com esse título.");
					}
				}
				
			} else {
				title.setError("");
			}
		}
		
		if(AdminAssetsPresenter.SECTION.equals(assetType) || AdminAssetsPresenter.HINT.equals(assetType)){
			if(!formHelper.isLengthValid(text.getFieldPersistText(), 2)){
				text.setError("Insira o conteúdo");
			} else text.setError("");
		}
		
		if(AdminAssetsPresenter.HINT.equals(assetType) || AdminAssetsPresenter.LIBRARY.equals(assetType)){
			if(!formHelper.isLengthValid(fontAwesomeClassName.getFieldPersistText(), 2)){
				fontAwesomeClassName.setError("Insira o conteúdo");
			} else fontAwesomeClassName.setError("");
		}
		return !checkErrors();
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) { 
		formHelper.clearErrors(fields);

		if(validateFields()){
			bus.fireEvent(new ShowPacifierEvent(true));

			Callback<CourseDetailsSection> courseDetailsSectionCallback = new Callback<CourseDetailsSection>() {
				@Override
				public void ok(CourseDetailsSection to) {
					presenter.initCourseDetailsSections();
					KornellNotification.show("Seção salva com sucesso.");
					assetModal.hide();
					bus.fireEvent(new ShowPacifierEvent(false));
				}
				@Override
				public void internalServerError(KornellErrorTO kornellErrorTO) {
					KornellNotification.show("Erro ao tentar salvar a seção.", AlertType.ERROR);
					assetModal.hide();
					bus.fireEvent(new ShowPacifierEvent(false));
				}
			};
			Callback<CourseDetailsHint> courseDetailsHintCallback = new Callback<CourseDetailsHint>() {
				@Override
				public void ok(CourseDetailsHint to) {
					presenter.initCourseDetailsHints();
					KornellNotification.show("Dica salva com sucesso.");
					assetModal.hide();
					bus.fireEvent(new ShowPacifierEvent(false));
				}
				@Override
				public void internalServerError(KornellErrorTO kornellErrorTO) {
					KornellNotification.show("Erro ao tentar salvar a dica.", AlertType.ERROR);
					assetModal.hide();
					bus.fireEvent(new ShowPacifierEvent(false));
				}
			};
			Callback<CourseDetailsLibrary> courseDetailsLibraryCallback = new Callback<CourseDetailsLibrary>() {
				@Override
				public void ok(CourseDetailsLibrary to) {
					presenter.initCourseDetailsLibraries();
					KornellNotification.show("Arquivo da biblioteca salvo com sucesso.");
					assetModal.hide();
					bus.fireEvent(new ShowPacifierEvent(false));
				}
				@Override
				public void internalServerError(KornellErrorTO kornellErrorTO) {
					KornellNotification.show("Erro ao tentar salvar o arquivo da biblioteca.", AlertType.ERROR);
					assetModal.hide();
					bus.fireEvent(new ShowPacifierEvent(false));
				}
			};
			
			if(assetType.equals(AdminAssetsPresenter.SECTION)){
				getCourseDetailsSectionFromForm();
				if(saveMode.equals(AdminAssetsPresenter.ADD)){
					session.courseDetailsSections().create(courseDetailsSection, courseDetailsSectionCallback);
				} else if(saveMode.equals(AdminAssetsPresenter.EDIT)){
					session.courseDetailsSection(courseDetailsSection.getUUID()).update(courseDetailsSection, courseDetailsSectionCallback);					
				}
			} else if(assetType.equals(AdminAssetsPresenter.HINT)){
				getCourseDetailsHintFromForm();
				if(saveMode.equals(AdminAssetsPresenter.ADD)){
					session.courseDetailsHints().create(courseDetailsHint, courseDetailsHintCallback);
				} else if(saveMode.equals(AdminAssetsPresenter.EDIT)){
					session.courseDetailsHint(courseDetailsHint.getUUID()).update(courseDetailsHint, courseDetailsHintCallback);
				}
			} else if(assetType.equals(AdminAssetsPresenter.LIBRARY)){
				getCourseDetailsLibraryFromForm();
				if(saveMode.equals(AdminAssetsPresenter.ADD)){
					session.courseDetailsLibraries().create(courseDetailsLibrary, courseDetailsLibraryCallback);
				} else if(saveMode.equals(AdminAssetsPresenter.EDIT)){
					session.courseDetailsLibrary(courseDetailsLibrary.getUUID()).update(courseDetailsLibrary, courseDetailsLibraryCallback);
				}
			}
		}
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		assetModal.hide();
	}

	private boolean checkErrors() {
		for (KornellFormFieldWrapper field : fields) 
			if(!"".equals(field.getError()))
				return true;		
		return false;
	}

	public void showModal(String saveMode, String assetType) {
		this.assetType = assetType;
		this.saveMode = saveMode;
		
		String modalTitle = AdminAssetsPresenter.ADD.equals(saveMode) ? "Adicionar " : "Editar";
		modalTitle += AdminAssetsPresenter.SECTION.equals(assetType) ? "Seção" : (AdminAssetsPresenter.HINT.equals(assetType) ? "Dica" : "Arquivo à Biblioteca");
		assetModal.setTitle(modalTitle);
		
		fields.clear();
		assetFields.clear();
		
		assetModal.show();
	}

	public void newSection(CourseDetailsEntityType courseDetailsEntityType, String entityUUID) {
		CourseDetailsSection courseDetailsSection = GenericClientFactoryImpl.ENTITY_FACTORY.newCourseDetailsSection().as();
		courseDetailsSection.setEntityType(courseDetailsEntityType);
		courseDetailsSection.setEntityUUID(entityUUID);
		courseDetailsSection.setIndex(presenter.getCourseDetailsSectionsTO().getCourseDetailsSections().size());
		initSection(AdminAssetsPresenter.ADD, courseDetailsSection);
	}

	public void initSection(String saveMode, CourseDetailsSection courseDetailsSection) {
		this.courseDetailsSection = courseDetailsSection;
		
		showModal(saveMode, AdminAssetsPresenter.SECTION);
		
		title = new KornellFormFieldWrapper("Título", formHelper.createTextBoxFormField(courseDetailsSection.getTitle()) , true);
		fields.add(title);
		assetFields.add(title);

		text = new KornellFormFieldWrapper("Conteúdo", formHelper.createTextAreaFormField(courseDetailsSection.getText()) , true);
		text.addStyleName("heightAuto");
		fields.add(text);
		assetFields.add(text);

		assetFields.add(formHelper.getImageSeparator());
	}

	private CourseDetailsSection getCourseDetailsSectionFromForm() {
		courseDetailsSection.setTitle(title.getFieldPersistText());
		courseDetailsSection.setText(text.getFieldPersistText());
		return courseDetailsSection;
	}

	public void newHint(CourseDetailsEntityType courseDetailsEntityType, String entityUUID) {
		CourseDetailsHint courseDetailsHint = GenericClientFactoryImpl.ENTITY_FACTORY.newCourseDetailsHint().as();
		courseDetailsHint.setEntityType(courseDetailsEntityType);
		courseDetailsHint.setEntityUUID(entityUUID);
		courseDetailsHint.setFontAwesomeClassName("fa-info-circle");
		courseDetailsHint.setIndex(presenter.getCourseDetailsHintsTO().getCourseDetailsHints().size());
		initHint(AdminAssetsPresenter.ADD, courseDetailsHint);
	}

	public void initHint(String saveMode, CourseDetailsHint courseDetailsHint) {
		this.courseDetailsHint = courseDetailsHint;
		
		showModal(saveMode, AdminAssetsPresenter.HINT);
		
		title = new KornellFormFieldWrapper("Título", formHelper.createTextBoxFormField(courseDetailsHint.getTitle()) , true);
		fields.add(title);
		assetFields.add(title);

		text = new KornellFormFieldWrapper("Conteúdo", formHelper.createTextAreaFormField(courseDetailsHint.getText()) , true);
		text.addStyleName("heightAuto");
		text.addStyleName("marginBottom25");
		fields.add(text);
		assetFields.add(text);

		fontAwesomeClassName = new KornellFormFieldWrapper("Ícone", formHelper.createTextBoxFormField(courseDetailsHint.getFontAwesomeClassName()));
		fields.add(fontAwesomeClassName);
		assetFields.add(fontAwesomeClassName);
		
		assetFields.add(formHelper.getImageSeparator());
	}

	private CourseDetailsHint getCourseDetailsHintFromForm() {
		courseDetailsHint.setTitle(title.getFieldPersistText());
		courseDetailsHint.setText(text.getFieldPersistText());
		courseDetailsHint.setFontAwesomeClassName(fontAwesomeClassName.getFieldPersistText());
		return courseDetailsHint;
	}

	public void newLibrary(CourseDetailsEntityType courseDetailsEntityType, String entityUUID) {
		CourseDetailsLibrary courseDetailsLibrary = GenericClientFactoryImpl.ENTITY_FACTORY.newCourseDetailsLibrary().as();
		courseDetailsLibrary.setEntityType(courseDetailsEntityType);
		courseDetailsLibrary.setEntityUUID(entityUUID);
		courseDetailsLibrary.setFontAwesomeClassName("fa-info-circle");
		courseDetailsLibrary.setIndex(presenter.getCourseDetailsLibrariesTO().getCourseDetailsLibraries().size());
		initLibrary(AdminAssetsPresenter.ADD, courseDetailsLibrary);
	}

	public void initLibrary(String saveMode, CourseDetailsLibrary courseDetailsLibrary) {
		this.courseDetailsLibrary = courseDetailsLibrary;
		
		showModal(saveMode, AdminAssetsPresenter.LIBRARY);
		
		title = new KornellFormFieldWrapper("Título", formHelper.createTextBoxFormField(courseDetailsLibrary.getTitle()) , true);
		fields.add(title);
		assetFields.add(title);

		fontAwesomeClassName = new KornellFormFieldWrapper("Ícone", formHelper.createTextBoxFormField(courseDetailsLibrary.getFontAwesomeClassName()) , true);
		fields.add(fontAwesomeClassName);
		assetFields.add(fontAwesomeClassName);
		
		assetFields.add(formHelper.getImageSeparator());
	}

	private CourseDetailsLibrary getCourseDetailsLibraryFromForm() {
		courseDetailsLibrary.setTitle(title.getFieldPersistText());
		courseDetailsLibrary.setFontAwesomeClassName(fontAwesomeClassName.getFieldPersistText());
		return courseDetailsLibrary;
	}

}