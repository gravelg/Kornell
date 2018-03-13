package kornell.gui.client.presentation.admin.courseversion.courseversion.generic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.entity.Course;
import kornell.core.entity.CourseVersion;
import kornell.core.util.StringUtils;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.util.view.KornellNotification;

public class WizardView extends Composite {
    interface MyUiBinder extends UiBinder<Widget, WizardView> {
    }

    private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

    private KornellSession session;
    private EventBus bus;
    private Presenter presenter;
    private CourseVersion courseVersion;
    private Course course;
    private IFrameElement iframe;

    @UiField
    FlowPanel wizardPanel;

    public WizardView(final KornellSession session, EventBus bus) {
        this.session = session;
        this.bus = bus;
        initWidget(uiBinder.createAndBindUi(this));
    }

    public void init(CourseVersion courseVersion, Course course, Presenter presenter) {
        this.courseVersion = courseVersion;
        this.course = course;
        this.presenter = presenter;
        createIFrame();
    }

    private void createIFrame() {
        if (iframe == null) {
            String iframeSrc = "/angular/knl.html#!/wizard";
            if(Window.Location.getHostName().indexOf("localhost") >= 0){
                iframeSrc = "http://localhost:8008" + iframeSrc;
            }
            iframe = Document.get().createIFrameElement();
            iframe.setSrc(iframeSrc);
            iframe.setId("angularFrame");
            iframe.addClassName("externalContent");
            iframe.setAttribute("allowtransparency", "true");
            iframe.setAttribute("style", "background-color: transparent;width: 100%;height: calc(100vh - 169px);");
            //allowing html5 video player to work on fullscreen inside the iframe
            iframe.setAttribute("allowFullScreen", "true");
            iframe.setAttribute("webkitallowfullscreen", "true");
            iframe.setAttribute("mozallowfullscreen", "true");
            Event.sinkEvents(iframe, Event.ONLOAD);
            wizardPanel.getElement().appendChild(iframe);
            injectEventListener(this);
        }
    }

    public void iframeIsReady(String message) {
        if (StringUtils.isSome(courseVersion.getClassroomJson())){
            sendIFrameMessage("classroomJsonLoad", courseVersion.getClassroomJson());
        } else if(StringUtils.isSome(courseVersion.getClassroomJsonPublished())){
            sendIFrameMessage("classroomJsonLoad", courseVersion.getClassroomJsonPublished());
        } else {
            sendIFrameMessage("classroomJsonNew", course.getName());
        }
    }

    public void saveWizard(String wizardData) {
        courseVersion.setClassroomJson(wizardData);
        session.courseVersion(courseVersion.getUUID()).update(courseVersion, new Callback<CourseVersion>() {
            @Override
            public void ok(CourseVersion courseVersionSaved) {
                courseVersion = courseVersionSaved;
                sendIFrameMessage("classroomJsonSaved", "");
            }
        });
    }

    public void publishWizard(String wizardData) {
        courseVersion.setClassroomJson(null);
        courseVersion.setClassroomJsonPublished(wizardData);
        session.courseVersion(courseVersion.getUUID()).update(courseVersion, new Callback<CourseVersion>() {
            @Override
            public void ok(CourseVersion courseVersionSaved) {
                courseVersion = courseVersionSaved;
                sendIFrameMessage("classroomJsonSaved", "");
                KornellNotification.show("Publicação feita com sucesso.");
            }
        });
    }

    public void discardWizard() {
        courseVersion.setClassroomJson(null);
        session.courseVersion(courseVersion.getUUID()).update(courseVersion, new Callback<CourseVersion>() {
            @Override
            public void ok(CourseVersion courseVersionSaved) {
                courseVersion = courseVersionSaved;
                sendIFrameMessage("classroomJsonLoad", courseVersion.getClassroomJsonPublished());
                KornellNotification.show("Alterações descartadas com sucesso.");
            }
        });
    }

    public void requestUploadPath(String filename) {
        session.course(courseVersion.getCourseUUID()).getWizardContentUploadURL(filename, new Callback<String>() {
            @Override
            public void ok(String url) {
                sendIFrameMessage("responseUploadPath", url);
            }
        });
    }

    private native void sendIFrameMessage(String type, String message) /*-{
	    var domain = $wnd.location;
	    if(domain.host.indexOf('localhost:') >= 0){
	    	domain = '*';
	    }
	    var iframe = $wnd.document.getElementById('angularFrame').contentWindow;
	    var data = { type: type, message: message};
	    iframe.postMessage(data, domain);
	}-*/;

    private native void injectEventListener(WizardView v) /*-{
	    function postMessageListener(e) {
	        var curUrl = $wnd.location;
	        if(e.data.type === "wizardReady"){
	        	v.@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView::iframeIsReady(Ljava/lang/String;)(e.data.message);
	        } else if(e.data.type === "wizardSave"){
                v.@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView::saveWizard(Ljava/lang/String;)(e.data.message);
            } else if(e.data.type === "wizardPublish"){
                v.@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView::publishWizard(Ljava/lang/String;)(e.data.message);
            } else if(e.data.type === "wizardDiscard"){
                v.@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView::discardWizard()();
            } else if(e.data.type === "requestUploadPath"){
                v.@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView::requestUploadPath(Ljava/lang/String;)(e.data.message);
            } else if(e.data.type === "kornellNotification"){
                if(e.data.notificationType === "success"){
                    @kornell.gui.client.util.view.KornellNotification::show(Ljava/lang/String;)(e.data.message);
                } else {
                    @kornell.gui.client.util.view.KornellNotification::showError(Ljava/lang/String;)(e.data.message);
                }
            }
	    }
	    // Listen to message from child window
	    if ($wnd.addEventListener) {
	        $wnd.addEventListener("message", postMessageListener, false);
	    } else if ($wnd.attachEvent) {
	        $wnd.attachEvent("onmessage", postMessageListener, false);
	    } else {
	    	$wnd["onmessage"] = postMessageListener;
	    }
	}-*/;
}