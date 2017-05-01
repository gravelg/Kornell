package kornell.gui.client.presentation.admin.assets;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.shared.GWT;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.AssetsEntity;
import kornell.core.entity.CertificateDetails;
import kornell.core.entity.CertificateType;
import kornell.core.entity.Course;
import kornell.core.entity.CourseClass;
import kornell.core.entity.CourseDetailsEntityType;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.EntityFactory;
import kornell.core.error.KornellErrorTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.ViewFactory;
import kornell.gui.client.event.ShowPacifierEvent;
import kornell.gui.client.util.forms.FormHelper;
import kornell.gui.client.util.view.KornellNotification;

@SuppressWarnings("static-access")
public class AdminAssetsPresenter implements AdminAssetsView.Presenter {
	public static final String CERTIFICATE_FILENAME = "certificate-bg.jpg";
	public static final String CERTIFICATE_DESCRIPTION = "Certificado (2000px X 1428px)";
	public static final String THUMB_DESCRIPTION = "Ícone (150px X 150px)";
	public static final String IMAGE_JPG = "image/jpg";
	public static final String THUMB_FILENAME = "thumb.jpg";
	public static final EntityFactory ENTITY_FACTORY = GWT.create(EntityFactory.class);
	Logger logger = Logger.getLogger(AdminAssetsPresenter.class.getName());
	private AdminAssetsView view;
	FormHelper formHelper;
	private static KornellSession session;
	private static EventBus bus;
	private PlaceController placeController;
	private ViewFactory viewFactory;
	private static AssetsEntity entity;
	private CourseDetailsEntityType courseDetailsEntityType;
	private static String entityName;
	private static  String entityUUID;
	private Map<String, String> messages;
	private static String filePath;
	private static CertificateDetails certificateDetails;
	private static String entityType;

	public AdminAssetsPresenter(KornellSession session, EventBus bus,
			PlaceController placeController, ViewFactory viewFactory) {
		this.session = session;
		this.bus = bus;
		this.placeController = placeController;
		this.viewFactory = viewFactory;
		formHelper = new FormHelper();
		
		init();
	}

	private void init() {
		view = getView();
		view.setPresenter(this);      
	}
	
	@Override
	public Widget asWidget() {
		return view.asWidget();
	}

	private AdminAssetsView getView() {
		return viewFactory.getAdminAssetsView();
	}

	@Override
	public void init(CourseDetailsEntityType entityType, AssetsEntity entity) {
		this.courseDetailsEntityType = entityType;
		this.entity = entity;
		this.entityUUID = entity.getUUID();
		String thumbSubTitle = null;
		String certificateDetailsSubTitle = null;
		
		switch (entityType) {
		case COURSE:
			this.entityName = "courses";
			this.entityType = Course.TYPE;
			thumbSubTitle = "Edite o ícone que aparecerá na listagem dos cursos na tela inicial do participante.";
			certificateDetailsSubTitle = "Edite o plano de fundo do certificado para este curso.";
			break;
		case COURSE_VERSION:
			this.entityName = "courseVersions";
			this.entityType = CourseVersion.TYPE;
			thumbSubTitle = "Edite o ícone que aparecerá na listagem dos cursos na tela inicial do participante. Este ícone será aplicado a todas as turmas desta versão do curso.";
			certificateDetailsSubTitle = "Edite o plano de fundo do certificado para todas as turmas desta versão do curso.";
			break;
		case COURSE_CLASS:
			this.entityName = "courseClasses";
			this.entityType = CourseClass.TYPE;
			thumbSubTitle = "Edite o ícone que aparecerá na listagem dos cursos na tela inicial do participante. Este ícone será aplicado somente a esta turma.";
			certificateDetailsSubTitle = "Edite o plano de fundo do certificado para esta turma.";
			break;
		}
		
		this.filePath = StringUtils.mkurl("repository", session.getInstitution().getAssetsRepositoryUUID(),
				"knl-institution",
				entityName, entity.getUUID());
		
		this.messages = new HashMap<>();
		messages.put("thumbSubTitle", thumbSubTitle);
		messages.put("certificateDetailsSubTitle", certificateDetailsSubTitle);
		
		
		view.initData();
		
		view.initThumb();
		
		session.certificatesDetails().findByEntityTypeAndUUID(entityType, entityUUID, new Callback<CertificateDetails>() {

			@Override
			public void ok(CertificateDetails to) {
				certificateDetails = to;
				view.initCertificateDetails(certificateDetails);
			}
			
			@Override
			public void notFound(KornellErrorTO kornellErrorTO){
				certificateDetails = ENTITY_FACTORY.newCertificateDetails().as();
				certificateDetails.setEntityType(entityType);
				certificateDetails.setEntityUUID(entityUUID);
				certificateDetails.setCertificateType(CertificateType.DEFAULT);
				certificateDetails.setBgImage(filePath);
				view.initCertificateDetails(certificateDetails);
			}
		});
	}

	@Override
	public void getUploadURL(final String contentType, final String elementId, final String fileName) {
		session.assets().getUploadURL(entityName, entityUUID, fileName, new Callback<String>() {
			@Override
			public void ok(String url) {
				getFile(elementId, contentType, url, fileName);
			}
		});
	}
	
	public static native void getFile(String elementId, String contentType, String url, String fileName) /*-{
		if ($wnd.document.getElementById(elementId).files.length != 1) {
	    	@kornell.gui.client.util.view.KornellNotification::showError(Ljava/lang/String;)("Por favor selecione uma imagem.");
		} else {
			@kornell.gui.client.presentation.admin.assets.AdminAssetsPresenter::showPacifier()();
			var file = $wnd.document.getElementById(elementId).files[0];
			if (file.name.indexOf(elementId.split("-")[1]) == -1) {
	        	@kornell.gui.client.util.view.KornellNotification::showError(Ljava/lang/String;)("Faça o upload de uma imagem do formato exigido.");
				@kornell.gui.client.presentation.admin.assets.AdminAssetsPresenter::hidePacifier()();
			} else {
				var req = new XMLHttpRequest();
				req.open('PUT', url);
				req.setRequestHeader("Content-type", contentType);
				req.onreadystatechange = function() {
					if (req.readyState == 4 && req.status == 200) {
	    				@kornell.gui.client.presentation.admin.assets.AdminAssetsPresenter::postProcessImageUpload(Ljava/lang/String;)(fileName);
					} else {
	    				@kornell.gui.client.presentation.admin.assets.AdminAssetsPresenter::hidePacifier()();
	    				@kornell.gui.client.util.view.KornellNotification::show(Ljava/lang/String;)("Erro ao atualizar imagem.");
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
	
	public static void postProcessImageUpload(String fileName){
		GWT.log("postProcessImageUpload " + fileName);
		if(THUMB_FILENAME.equals(fileName)){
			updateThumbnail(fileName);
		} else if(CERTIFICATE_FILENAME.equals(fileName)){
			upsertCertificateDetails();
		}
	}

	private static void updateThumbnail(String fileName) {
		GWT.log("updateThumbnail " + fileName);
		entity.setThumbUrl(StringUtils.mkurl(filePath, fileName));
		session.assets().updateThumbnail(entityName, entityUUID, entity, entityType, null);
	}

	private static void upsertCertificateDetails() {
		if(certificateDetails.getUUID() == null){
			session.certificatesDetails().create(certificateDetails, new Callback<CertificateDetails>() {
				@Override
				public void ok(CertificateDetails to) {
					hidePacifier();
					KornellNotification.show("Atualização de imagem completa.");
				}
			});
		} else {
			session.certificateDetails(certificateDetails.getUUID()).update(certificateDetails, new Callback<CertificateDetails>() {
				@Override
				public void ok(CertificateDetails to) {
					hidePacifier();
					KornellNotification.show("Atualização de imagem completa.");
				}
			});
		}
	}

	@Override
	public String getFileURL(String fileName) {
		return StringUtils.mkurl(filePath, fileName);
	}

	@Override
	public Map<String, String> getInfo() {
		return messages;
	}
}