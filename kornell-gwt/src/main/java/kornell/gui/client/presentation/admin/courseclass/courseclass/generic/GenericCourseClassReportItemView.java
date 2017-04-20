package kornell.gui.client.presentation.admin.courseclass.courseclass.generic;

import java.util.ArrayList;
import java.util.List;

import com.github.gwtbootstrap.client.ui.CheckBox;
import com.github.gwtbootstrap.client.ui.Collapse;
import com.github.gwtbootstrap.client.ui.CollapseTrigger;
import com.github.gwtbootstrap.client.ui.TextArea;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.github.gwtbootstrap.client.ui.event.HiddenEvent;
import com.github.gwtbootstrap.client.ui.event.HiddenHandler;
import com.github.gwtbootstrap.client.ui.event.ShownEvent;
import com.github.gwtbootstrap.client.ui.event.ShownHandler;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.error.KornellErrorTO;
import kornell.core.to.CourseClassTO;
import kornell.core.to.SimplePeopleTO;
import kornell.core.to.SimplePersonTO;
import kornell.core.to.TOFactory;
import kornell.core.util.StringUtils;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.util.ClientConstants;
import kornell.gui.client.util.view.KornellNotification;

public class GenericCourseClassReportItemView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, GenericCourseClassReportItemView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);
	private String ADMIN_IMAGES_PATH = StringUtils.mkurl(ClientConstants.IMAGES_PATH, "admin/");
	private String LIBRARY_IMAGES_PATH = StringUtils.mkurl(ClientConstants.IMAGES_PATH, "courseLibrary/");
	private KornellSession session;
	private EventBus bus;
	private CourseClassTO currentCourseClass;
	private String type;
	private String name;
	private String description;
	public static final TOFactory toFactory = GWT.create(TOFactory.class);
	
	private CheckBox checkBox;
	
	private HandlerRegistration downloadHandler;
	
	public static final String COURSE_CLASS_INFO = "courseClassInfo";
	public static final String CERTIFICATE = "certificate";
	public static final String COURSE_CLASS_AUDIT = "courseClassAudit";
	
	@UiField
	Image certificationIcon;
	@UiField
	Label lblName;
	@UiField
	Label lblDescription;
	@UiField
	FlowPanel optionPanel;
	@UiField
	Anchor lblGenerate;
	@UiField
	Anchor lblDownload;
	private TextArea usernamesTextArea;


	public GenericCourseClassReportItemView(EventBus bus, KornellSession session, CourseClassTO currentCourseClass,
			String type) {
		this.session = session;
		this.bus = bus;
		this.currentCourseClass = currentCourseClass;
		this.type = type;
		initWidget(uiBinder.createAndBindUi(this));
		display();
	}
	
	private void display() {
		if(CERTIFICATE.equals(this.type))
			displayCertificate();
		else if(COURSE_CLASS_INFO.equals(this.type))
			displayCourseClassInfo();
		else if(COURSE_CLASS_AUDIT.equals(this.type))
			displayCourseClassAudit();
	}

	private void displayCourseClassAudit() {
		this.name = "Relatório de auditoria da turma";
		this.description = "Geração do relatório de histórico de alteração de matrículas e de transferências.";

		certificationIcon.setUrl(StringUtils.mkurl(ADMIN_IMAGES_PATH, type + ".png"));
		lblName.setText(name);
		lblDescription.setText(description);
		lblGenerate.setText("Gerar");
		lblGenerate.addStyleName("cursorPointer");

		lblDownload.setText("-");
		lblDownload.removeStyleName("cursorPointer");
		lblDownload.addStyleName("anchorToLabel");
		lblDownload.setEnabled(false);
		
		lblGenerate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				KornellNotification.show("Aguarde um instante...", AlertType.WARNING, 2000);
				session.report().locationAssign("/report/courseClassAudit",
						"?courseClassUUID=" + currentCourseClass.getCourseClass().getUUID());
			}
		});
  }

	private void displayCourseClassInfo() {
	  this.name = "Relatório de detalhes da turma";
		this.description = "Geração do relatório de detalhes da turma e de suas matrículas. Por padrão ele é gerado em PDF contendo somente matriculas ativas.";

		certificationIcon.setUrl(StringUtils.mkurl(ADMIN_IMAGES_PATH, type + ".png"));
		lblName.setText(name);
		lblDescription.setText(description);
		lblGenerate.setText("Gerar");
		lblGenerate.addStyleName("cursorPointer");

		lblDownload.setText("-");
		lblDownload.removeStyleName("cursorPointer");
		lblDownload.addStyleName("anchorToLabel");
		lblDownload.setEnabled(false);

		Image img = new Image(StringUtils.mkurl(LIBRARY_IMAGES_PATH, "xls.png"));
		checkBox = new CheckBox("Gerar em formato Excel (inclui matrículas canceladas)");
		
		optionPanel.add(img);
		optionPanel.add(checkBox);
		
		img.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				checkBox.setValue(!checkBox.getValue());				
			}
		});
		
		lblGenerate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				KornellNotification.show("Aguarde um instante...", AlertType.WARNING, 2000);
				session.report().locationAssign("/report/courseClassInfo",
						"?courseClassUUID=" + currentCourseClass.getCourseClass().getUUID() + 
						"&fileType=" + (checkBox.getValue() ? "xls" : "pdf"));
			}
		});
  }

	private void displayCertificate() {
	  this.name = "Certificados de conclusão de curso";
		this.description = "Geração do certificado de todos os alunos desta turma que concluíram o curso. A geração pode chegar a levar a alguns minutos, dependendo do tamanho da turma. Assim que o relatório for gerado, ele estará disponível para ser baixado aqui.";

		certificationIcon.setUrl(StringUtils.mkurl(ADMIN_IMAGES_PATH, type + ".png"));
		lblName.setText(name);
		lblDescription.setText(description);
		lblGenerate.setText("Gerar");
		lblGenerate.addStyleName("cursorPointer");
		
		CollapseTrigger trigger = new CollapseTrigger();
		final Collapse collapse = new Collapse();
		trigger.setTarget("#toggleCertUsernames");
		collapse.setId("toggleCertUsernames");

		Image img = new Image(StringUtils.mkurl(LIBRARY_IMAGES_PATH, "pdf.png"));
		checkBox = new CheckBox("Gerar somente para um conjunto de participantes dessa turma");
		
		FlowPanel triggerPanel = new FlowPanel();
		triggerPanel.add(img);
		triggerPanel.add(checkBox);
		trigger.add(triggerPanel);
		
		FlowPanel collapsePanel = new FlowPanel();
		Label infoLabel = new Label("Digite os usuários, cada um em uma linha. Só serão gerados os certificados dos participantes matriculados nessa turma e que terminaram o curso.");
		usernamesTextArea = new TextArea();
		collapsePanel.add(infoLabel);
		collapsePanel.add(usernamesTextArea);
		collapse.add(collapsePanel);
		
		optionPanel.add(trigger);
		optionPanel.add(collapse);
		
		img.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
	    		checkBox.setValue(!checkBox.getValue());		
			}
		});
		
		collapse.addShownHandler(new ShownHandler() {
			@Override
			public void onShown(ShownEvent shownEvent) {
				checkBox.setValue(true);
			}
		});
		
		collapse.addHiddenHandler(new HiddenHandler() {
			@Override
			public void onHidden(HiddenEvent hiddenEvent) {
				checkBox.setValue(false);
			}
		});
		
		lblGenerate.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				displayCertificateActionCell(null);
				bus.fireEvent(new ShowPacifierEvent(true));
				SimplePeopleTO simplePeopleTO = buildSimplePeopleTO();
				session.report().generateCourseClassCertificate(currentCourseClass.getCourseClass().getUUID(), simplePeopleTO, new Callback<String>() {
					
					@Override
					public void ok(String url) {
						KornellNotification.show("Os certificados foram gerados.", AlertType.WARNING, 2000);
						displayCertificateActionCell(url);
						bus.fireEvent(new ShowPacifierEvent(false));
					}
					
					@Override
					public void internalServerError(KornellErrorTO kornellErrorTO) {
						KornellNotification.show("Erro na geração dos certificados. Certifique-se que existem alunos que concluíram o curso nessa turma.", AlertType.ERROR, 3000);
						displayCertificateActionCell(null);
						bus.fireEvent(new ShowPacifierEvent(false));
					}
				});
			}

			private SimplePeopleTO buildSimplePeopleTO() {
				SimplePeopleTO simplePeopleTO = toFactory.newSimplePeopleTO().as();
				
				if(checkBox.getValue()){
					String usernames = usernamesTextArea.getValue();
					String[] usernamesArr = usernames.trim().split("\n");
					List<SimplePersonTO> simplePeopleTOList = new ArrayList<SimplePersonTO>();
					SimplePersonTO simplePersonTO;
					String username;
					for (int i = 0; i < usernamesArr.length; i++) {
						username = usernamesArr[i].trim();
						if(username.length() > 0){
							simplePersonTO = toFactory.newSimplePersonTO().as();
							simplePersonTO.setUsername(username);
							simplePeopleTOList.add(simplePersonTO);
						}
					}
					simplePeopleTO.setSimplePeopleTO(simplePeopleTOList);
				}
				return simplePeopleTO;
			}
		});

		session.report().courseClassCertificateExists(currentCourseClass.getCourseClass().getUUID(), new Callback<String>() {
			@Override
			public void ok(String str) {
				displayCertificateActionCell(str);
			}
			
			@Override
			public void internalServerError(KornellErrorTO kornellErrorTO) {
				displayCertificateActionCell(null);
			}
		});
  }

	private void displayCertificateActionCell(final String url) {
	  if(url != null && !"".equals(url)) {
			lblDownload.setText("Baixar");
			lblDownload.addStyleName("cursorPointer");
			lblDownload.removeStyleName("anchorToLabel");
			downloadHandler = lblDownload.addClickHandler(new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					Window.open(url,"","");
				}
			});
		} else {
			lblDownload.setText("Não disponível");
			lblDownload.removeStyleName("cursorPointer");
			lblDownload.addStyleName("anchorToLabel");
			lblDownload.setEnabled(false);
			if(downloadHandler != null){
				downloadHandler.removeHandler();
			}
		}
  }

}
