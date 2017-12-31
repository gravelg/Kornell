package kornell.gui.client.presentation.admin.courseversion.courseversion.generic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseVersion;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;

public class WizardView extends Composite {
	interface MyUiBinder extends UiBinder<Widget, WizardView> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private EventBus bus;
	private Presenter presenter;
	private CourseVersion courseVersion;
	private IFrameElement iframe;
	
	@UiField
	FlowPanel wizardPanel;

	public WizardView(final KornellSession session, EventBus bus) {
		this.bus = bus;
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void init(CourseVersion courseVersion, Presenter presenter) {
		this.courseVersion = courseVersion;
		this.presenter = presenter;
		createIFrame();
	}

	private void createIFrame() {
		if (iframe == null) {
			iframe = Document.get().createIFrameElement();
			iframe.setSrc("/angular/knl.html#!/wizard");
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
	    sendIFrameMessage(courseVersion.getClassroomJson());
	}
	
	public void saveWizard(String wizardData) {
	    courseVersion.setClassroomJson(wizardData);
	    presenter.upsertCourseVersion(courseVersion, false);
	}
	
	private native void sendIFrameMessage(String message) /*-{
	    var domain = $wnd.location;
	    var iframe = $wnd.document.getElementById('angularFrame').contentWindow;
	    iframe.postMessage(message, domain);
	}-*/;
	
	private native void injectEventListener(WizardView v) /*-{
	    function postMessageListener(e) {
	        var curUrl = $wnd.location;
	        if(e.data.type === "wizardReady"){
	        	v.@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView::iframeIsReady(Ljava/lang/String;)(e.data.message);
	        } else if(e.data.type === "wizardSave"){
	        	v.@kornell.gui.client.presentation.admin.courseversion.courseversion.generic.WizardView::saveWizard(Ljava/lang/String;)(e.data.message);
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