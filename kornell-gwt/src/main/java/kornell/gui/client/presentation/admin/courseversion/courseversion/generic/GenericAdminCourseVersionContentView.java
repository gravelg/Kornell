package kornell.gui.client.presentation.admin.courseversion.courseversion.generic;

import java.util.HashMap;
import java.util.List;

import com.github.gwtbootstrap.client.ui.FileUpload;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.forms.formfield.KornellFormFieldWrapper;

public class GenericAdminCourseVersionContentView extends Composite implements AdminCourseVersionContentView {
	interface MyUiBinder extends UiBinder<Widget, GenericAdminCourseVersionContentView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private static EventBus bus;
	private KornellSession session;
	boolean isCurrentUser, showContactDetails, isRegisteredWithCPF;
	private FormHelper formHelper = GWT.create(FormHelper.class);
	private List<Label> sideItems;
	private HashMap<String, Label> sidePanelItemsMap;
	private static String HIGHLIGHT_CLASS = "highlightText";
	private String PLAIN_CLASS = "plainDiscreteTextColor";
	private KornellFormFieldWrapper name;
	private List<KornellFormFieldWrapper> fields;

	@UiField
	FlowPanel courseVersionUpload;
	@UiField
	FlowPanel wizardContainer;

	private CourseVersion courseVersion;
	private Presenter presenter;
	
	private boolean isWizardVersion = false;

	private String nameLabel;
	private String changedString = "(*) ";

	private WizardView wizardView;

	public GenericAdminCourseVersionContentView(final KornellSession session, EventBus bus, PlaceController placeCtrl) {
		this.session = session;
		GenericAdminCourseVersionContentView.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));

		courseVersionUpload.addStyleName("fieldPanelWrapper fileUploadPanel");
		FlowPanel labelPanel = new FlowPanel();
		labelPanel.addStyleName("labelPanel");
		Label lblLabel = new Label("Atualização de versão");
		lblLabel.addStyleName("lblLabel");
		labelPanel.add(lblLabel);
		courseVersionUpload.add(labelPanel);

		// Create the FileUpload component
		FlowPanel fileUploadPanel = new FlowPanel();
		FileUpload fileUpload = new FileUpload();
		fileUpload.setName("uploadFormElement");
		fileUpload.setId("versionUpdate");
		fileUploadPanel.add(fileUpload);
		courseVersionUpload.add(fileUpload);
		
	    // Add a submit button to the form
		com.github.gwtbootstrap.client.ui.Button btnOK = new com.github.gwtbootstrap.client.ui.Button();
		FormHelper.createIcon(btnOK, "fa-floppy-o");
		btnOK.addStyleName("btnAction");
		btnOK.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				session.courseVersion(courseVersion.getUUID()).getContentUploadURL(new Callback<String>() {
					@Override
					public void ok(String url) {
						getFile(url);
					}
				});		
			}
		});
		courseVersionUpload.add(btnOK);
	}

	@Override
	public void init(CourseVersion courseVersion, boolean isWizard) {
		this.courseVersion = courseVersion;
		this.isWizardVersion = isWizard;
		wizardContainer.clear();
		if(isWizardVersion){
			wizardView = new WizardView(session, bus);
			wizardView.init(courseVersion, presenter);
			wizardContainer.add(wizardView);
		}
		courseVersionUpload.setVisible(!isWizardVersion);
	}
	
	public static native void getFile(String url) /*-{
		if ($wnd.document.getElementById("versionUpdate").files.length != 1) {
        	@kornell.gui.client.util.view.KornellNotification::showError(Ljava/lang/String;)("Por favor selecione um arquivo");
		} else {
			@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.GenericAdminCourseVersionContentView::showPacifier()();
			var file = $wnd.document.getElementById("versionUpdate").files[0];
			if (file.name.indexOf(".zip") == -1) {
	        	@kornell.gui.client.util.view.KornellNotification::showError(Ljava/lang/String;)("Faça o upload de um arquivo zip");
				@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.GenericAdminCourseVersionContentView::hidePacifier()();
			} else {
				var req = new XMLHttpRequest();
				req.open('PUT', url);
				req.setRequestHeader("Content-type", "application/zip");
				req.onreadystatechange = function() {
    				if (req.readyState == 4 && req.status == 200) {
        				@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.GenericAdminCourseVersionContentView::hidePacifier()();
        				@kornell.gui.client.util.view.KornellNotification::show(Ljava/lang/String;)("Atualização de versão completa");
    				}
				}
				req.send(file);
			}
		}
	}-*/;

	public static void showPacifier(){
		bus.fireEvent(new ShowPacifierEvent(true));
	}
	
	public static void hidePacifier(){
		bus.fireEvent(new ShowPacifierEvent(false));
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	@Override
	public WizardView getWizardView() {
		return wizardView;
	}

}