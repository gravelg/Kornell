package kornell.gui.client.presentation.admin.courseversion.courseversion.generic;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Form;
import com.github.gwtbootstrap.client.ui.ListBox;
import com.github.gwtbootstrap.client.ui.Modal;
import com.github.gwtbootstrap.client.ui.Tab;
import com.github.gwtbootstrap.client.ui.TabPanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.ContentSpec;
import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.EntityFactory;
import kornell.core.entity.InstitutionType;
import kornell.core.to.CourseTO;
import kornell.core.to.CourseVersionTO;
import kornell.core.to.CourseVersionsTO;
import kornell.core.to.CoursesTO;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.assets.AdminAssetsPresenter;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionPlace;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionView;
import kornell.gui.client.presentation.admin.courseversion.courseversions.AdminCourseVersionsPlace;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;
import kornell.gui.client.util.forms.formfield.ListBoxFormField;

public class GenericAdminCourseVersionView extends Composite implements AdminCourseVersionView {

	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseVersionView> {
	}
	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	public static final EntityFactory entityFactory = GWT.create(EntityFactory.class);

	private KornellSession session;
	private PlaceController placeCtrl;
	private EventBus bus;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private boolean isCreationMode, isInstitutionAdmin, isPlatformAdmin;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;

	private Presenter presenter;

	@UiField
	TabPanel tabsPanel;
	@UiField
	Tab editTab;
	@UiField
	Tab contentsTab;
	@UiField
	FlowPanel contentsPanel;
	@UiField
	Tab assetsTab;
	@UiField
	FlowPanel assetsPanel;
	
	@UiField
	HTMLPanel titleEdit;
	@UiField
	HTMLPanel titleCreate;
	@UiField
	Form form;
	@UiField
	FlowPanel courseVersionFields;
	@UiField
	Button btnOK;
	@UiField
	Button btnCancel;
	
	@UiField
	Modal confirmModal;
	@UiField
	Label confirmText;
	@UiField
	Button btnModalOK;
	@UiField
	Button btnModalCancel;

	private CourseVersion courseVersion;

	private KornellFormFieldWrapper name, course, distributionPrefix, contentSpec, disabled, parentCourseVersion, instanceCount, label;
	
	private List<KornellFormFieldWrapper> fields;
	private String courseVersionUUID;
	private boolean initializing = false;
	private ViewFactory viewFactory;
	private AdminAssetsPresenter adminAssetsPresenter;
	
	public GenericAdminCourseVersionView(final KornellSession session, EventBus bus, final PlaceController placeCtrl, ViewFactory viewFactory) {
		this.session = session;
		this.placeCtrl = placeCtrl;
		this.viewFactory = viewFactory;
		this.isPlatformAdmin = session.isPlatformAdmin();
		this.isInstitutionAdmin = session.isInstitutionAdmin();
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));

		// i18n
		btnOK.setText("Salvar".toUpperCase());
		btnCancel.setText("Cancelar".toUpperCase());	
		
		btnModalOK.setText("OK".toUpperCase());
		btnModalCancel.setText("Cancelar".toUpperCase());
		
		bus.addHandler(PlaceChangeEvent.TYPE,
				new PlaceChangeEvent.Handler() {
					@Override
					public void onPlaceChange(PlaceChangeEvent event) {
						if(event.getNewPlace() instanceof AdminCourseVersionPlace && !initializing){
							init();
						}
					}
				});
		
		init();
	}
	
	@Override
	public void init(){
		if(initializing) return;
		initializing = true;
		asWidget().setVisible(false);
		
		if(placeCtrl.getWhere() instanceof AdminCourseVersionPlace && ((AdminCourseVersionPlace)placeCtrl.getWhere()).getCourseVersionUUID() != null){
			this.courseVersionUUID = ((AdminCourseVersionPlace)placeCtrl.getWhere()).getCourseVersionUUID();
			isCreationMode = false;
			session.courseVersion(courseVersionUUID).get(new Callback<CourseVersionTO>() {
				@Override
				public void ok(CourseVersionTO to) {
					courseVersion = to.getCourseVersion();
					initData();
				}
			});
		} else {
			isCreationMode = true;
			courseVersion = entityFactory.newCourseVersion().as();
			initData();	
		}
	}

	public void initData() {

		if(!isCreationMode && ContentSpec.WIZARD.equals(courseVersion.getContentSpec()))
			presenter.buildContentView(courseVersion);
		

		if (isPlatformAdmin) {			
			contentsTab.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					presenter.buildContentView(courseVersion);
				}
			});
		} else {
			FormHelper.hideTab(contentsTab);
		}
		
		assetsTab.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				buildAssetsView();
			}
		});
		
		courseVersionFields.setVisible(false);
		this.fields = new ArrayList<KornellFormFieldWrapper>();
		courseVersionFields.clear();
		

		titleEdit.setVisible(!isCreationMode);
		titleCreate.setVisible(isCreationMode);
		
		btnOK.setVisible(isInstitutionAdmin|| isCreationMode);
		btnCancel.setVisible(isInstitutionAdmin);		

		session.courses().get(new Callback<CoursesTO>() {
				@Override
				public void ok(CoursesTO to) {
					createCoursesField(to);
					asWidget().setVisible(true);
					if(!isCreationMode)
						((ListBox)course.getFieldWidget()).setSelectedValue(courseVersion.getCourseUUID());
				}
			});

		name = new KornellFormFieldWrapper("Nome", formHelper.createTextBoxFormField(courseVersion.getName()), isInstitutionAdmin);
		fields.add(name);
		courseVersionFields.add(name);

		distributionPrefix = new KornellFormFieldWrapper("Prefixo de Distribuição", formHelper.createTextBoxFormField(courseVersion.getDistributionPrefix()), isInstitutionAdmin);
		fields.add(distributionPrefix);
		courseVersionFields.add(distributionPrefix);
		
		final ListBox contentSpecTypes = new ListBox();
		contentSpecTypes.addItem("KNL", ContentSpec.KNL.toString());
		contentSpecTypes.addItem("SCORM12", ContentSpec.SCORM12.toString());
		//contentSpecTypes.addItem("WIZARD", ContentSpec.WIZARD.toString());
		if (!isCreationMode) {
			contentSpecTypes.setSelectedValue(courseVersion.getContentSpec().toString());
		}
		contentSpec = new KornellFormFieldWrapper("Tipo", new ListBoxFormField(contentSpecTypes), isInstitutionAdmin);
		fields.add(contentSpec);
		courseVersionFields.add(contentSpec);

		disabled = new KornellFormFieldWrapper("Desabilitar?", formHelper.createCheckBoxFormField(courseVersion.isDisabled()), isInstitutionAdmin);
		fields.add(disabled);
		courseVersionFields.add(disabled);
		((CheckBox)disabled.getFieldWidget()).addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				if(event.getValue()){
				}
			}
		});
		
		if(InstitutionType.DASHBOARD.equals(session.getInstitution().getInstitutionType())){
			if (isCreationMode || isInstitutionAdmin) {
		  		session.courseVersions().get(new Callback<CourseVersionsTO>() {
		  			@Override
		  			public void ok(CourseVersionsTO to) {
						createCourseVersionsField(to);
		  			}
		  		});
			} else {
				createCourseVersionsField(null);
			}

			String instanceCountStr = courseVersion.getInstanceCount() == null ? "" : courseVersion.getInstanceCount().toString();
			instanceCount = new KornellFormFieldWrapper("Quantidade de Instâncias", formHelper.createTextBoxFormField(instanceCountStr), isInstitutionAdmin);
			fields.add(instanceCount);
			courseVersionFields.add(instanceCount);

			label = new KornellFormFieldWrapper("Rótulo", formHelper.createTextBoxFormField(courseVersion.getLabel()), isInstitutionAdmin);
			fields.add(label);
			courseVersionFields.add(label);
		}
		
		courseVersionFields.add(formHelper.getImageSeparator());

		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
			@Override
			public void execute() {
				showNavBar(!isCreationMode);
				courseVersionFields.setVisible(true);
			}
		});

	}

	private static native void showNavBar(boolean show) /*-{
		if($wnd.document.getElementsByClassName("nav-tabs") && $wnd.document.getElementsByClassName("nav-tabs")[0]){
			$wnd.document.getElementsByClassName("nav-tabs")[0].style.display = (show?"block":"none");
		}
	}-*/;


	public void buildAssetsView() {
		adminAssetsPresenter = new AdminAssetsPresenter(session,bus,viewFactory);
		assetsPanel.clear();
		adminAssetsPresenter.init(CourseDetailsEntityType.COURSE_VERSION, courseVersion);
		assetsPanel.add(adminAssetsPresenter.asWidget());
	}
	
	private void createCourseVersionsField(CourseVersionsTO to) {
		final ListBox courseVersions = new ListBox();
		if(to != null){
			courseVersions.addItem("Nenhuma", "null");
			for (CourseVersion courseVersion : to.getCourseVersions()) {
				courseVersions.addItem(courseVersion.getName(), courseVersion.getUUID());
			}
		} else {
			courseVersions.addItem(courseVersion.getParentVersionUUID(), courseVersion.getParentVersionUUID());
		}
		if(parentCourseVersion != null && courseVersionFields.getElement().isOrHasChild(parentCourseVersion.getElement())){
			fields.remove(parentCourseVersion);
			courseVersionFields.getElement().removeChild(parentCourseVersion.getElement());
		}
		if (!isCreationMode) {
			courseVersions.setSelectedValue(courseVersion.getParentVersionUUID() == null? "null" : courseVersion.getParentVersionUUID());
		}
		parentCourseVersion = new KornellFormFieldWrapper("Versão Pai do Curso", new ListBoxFormField(courseVersions), (isCreationMode || isInstitutionAdmin));
		
		fields.add(parentCourseVersion);
		courseVersionFields.insert(parentCourseVersion, 5);
		
		
		courseVersionFields.setVisible(true);
	}

	private void createCoursesField(CoursesTO to) {
		final ListBox courses = new ListBox();
		if(to != null){
			for (CourseTO courseTO : to.getCourses()) {
				courses.addItem(courseTO.getCourse().getTitle(), courseTO.getCourse().getUUID());
			}
		}/* else {
			courses.addItem(courseVersion.getCourse().getTitle(), courseVersion.getCourse().getUUID());
		}*/
		courses.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
			}
		});
		if (!isCreationMode) {
			courses.setSelectedValue(courseVersion.getCourseUUID());
		}
		course = new KornellFormFieldWrapper("Curso", new ListBoxFormField(courses), isInstitutionAdmin);
				

		if(course != null && courseVersionFields.getElement().isOrHasChild(course.getElement())){
			fields.remove(course);
			courseVersionFields.getElement().removeChild(course.getElement());
		}
		fields.add(course);
		courses.setSelectedIndex(0);
		courseVersionFields.insert(course, 0);
		initializing = false;
	}
	
	private boolean validateFields() {		
		if (!formHelper.isLengthValid(name.getFieldPersistText(), 2, 100)) {
			name.setError("Insira o nome da versão");
		}
		if (!formHelper.isListBoxSelected((ListBox)course.getFieldWidget())) {
			course.setError("Escolha o curso");
		}
		if (!formHelper.isLengthValid(distributionPrefix.getFieldPersistText(), 2, 200)) {
			distributionPrefix.setError("Insira o prefixo de distribuição");
		}
		if (!formHelper.isLengthValid(contentSpec.getFieldPersistText(), 2, 20)) {
			contentSpec.setError("Insira o tipo");
		} else {
			try {
				ContentSpec.valueOf(contentSpec.getFieldPersistText());
	    } catch (Exception e) {
				contentSpec.setError("Tipo inválido.");
	    }
		}
		if(InstitutionType.DASHBOARD.equals(session.getInstitution().getInstitutionType())){
			if (!formHelper.isValidNumber(instanceCount.getFieldPersistText()) || !formHelper.isNumberRangeValid(Integer.parseInt(instanceCount.getFieldPersistText()), 1, 100)) {
				instanceCount.setError("Insira a um número entre 1 e 100.");
			}
		}
		
		return !formHelper.checkErrors(fields);
	}

	@UiHandler("btnOK")
	void doOK(ClickEvent e) {
		formHelper.clearErrors(fields);
		if (isInstitutionAdmin && validateFields()) {
			bus.fireEvent(new ShowPacifierEvent(true));
			CourseVersion courseVersion = getCourseVersionInfoFromForm();
			presenter.upsertCourseVersion(courseVersion);
		}
	}

	private CourseVersion getCourseVersionInfoFromForm() {
		CourseVersion version = courseVersion;
		version.setName(name.getFieldPersistText());
		version.setCourseUUID(course.getFieldPersistText());
		version.setDistributionPrefix(distributionPrefix.getFieldPersistText());
		version.setContentSpec(ContentSpec.valueOf(contentSpec.getFieldPersistText()));
		version.setDisabled(disabled.getFieldPersistText().equals("true"));
		if(InstitutionType.DASHBOARD.equals(session.getInstitution().getInstitutionType())){
			String parentVersionUUID = (parentCourseVersion.getFieldPersistText().equals("null") ? null : parentCourseVersion.getFieldPersistText());
			version.setParentVersionUUID(parentVersionUUID);
			version.setInstanceCount(instanceCount.getFieldPersistText().length() > 0 ?
					Integer.parseInt(instanceCount.getFieldPersistText()) :
						null);
			version.setLabel(label.getFieldPersistText());
		}
		return version;
	}

	@UiHandler("btnCancel")
	void doCancel(ClickEvent e) {
		this.placeCtrl.goTo(new AdminCourseVersionsPlace());
	}
	
	@Override
  public void setPresenter(Presenter presenter) {
	  this.presenter = presenter;
  }

	@Override
	public Presenter getPresenter() {
		return presenter;
	}

	@Override
	public void addContentPanel(AdminCourseVersionContentView adminCourseVersionContentView) {
		contentsPanel.clear();
		contentsPanel.add(adminCourseVersionContentView);		
	}
}