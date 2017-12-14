package kornell.gui.client.presentation.admin.courseversion.courseversion.generic;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.entity.CourseVersion;
import kornell.core.entity.InstitutionType;
import kornell.core.util.StringUtils;
import kornell.gui.client.presentation.admin.courseversion.courseversion.AdminCourseVersionContentView.Presenter;
import kornell.gui.client.util.view.Positioning;

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
			iframe.addClassName("externalContent");
			iframe.setAttribute("allowtransparency", "true");
			iframe.setAttribute("style", "background-color: transparent;height: calc(100vh - 222px);");
			//allowing html5 video player to work on fullscreen inside the iframe
			iframe.setAttribute("allowFullScreen", "true");
			iframe.setAttribute("webkitallowfullscreen", "true");
			iframe.setAttribute("mozallowfullscreen", "true");
			Event.sinkEvents(iframe, Event.ONLOAD);

			// Weird yet simple way of solving FF's weird behavior
			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					Scheduler.get().scheduleDeferred(new Command() {
						@Override
						public void execute() {
							placeIframe();
						}
					});
				}
			});
			
			wizardPanel.getElement().appendChild(iframe);
			
		}
		placeIframe();
	}

	private void placeIframe() {
		iframe.setPropertyString("width", "100%");
		int h = (Window.getClientHeight() - Positioning.NORTH_BAR);
		String height = h + "px";
		iframe.setPropertyString("height", height);
	}
}