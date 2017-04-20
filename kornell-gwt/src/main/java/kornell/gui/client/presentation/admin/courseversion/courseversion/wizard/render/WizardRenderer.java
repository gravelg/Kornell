package kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.render;

import com.github.gwtbootstrap.client.ui.Image;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.FrameElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.util.StringUtils;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.Wizard;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardElement;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlide;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItem;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemImage;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemType;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardSlideItemVideoLink;
import kornell.gui.client.presentation.admin.courseversion.courseversion.autobean.wizard.WizardTopic;
import kornell.gui.client.presentation.admin.courseversion.courseversion.wizard.WizardUtils;
import kornell.gui.client.util.view.KornellNotification;
import kornell.gui.client.util.view.Positioning;

public class WizardRenderer extends Composite {
	interface MyUiBinder extends UiBinder<Widget, WizardRenderer> {
	}

	private static MyUiBinder uiBinder = GWT.create(MyUiBinder.class);

	private EventBus bus;
	private KornellSession session;
	private Wizard wizard;

	@UiField
	FlowPanel wizardRendererPanel;

	private WizardElement selectedWizardElement;

	public WizardRenderer(final KornellSession session, EventBus bus, Wizard wizard) {
		this.session = session;
		this.bus = bus;
		this.wizard = wizard;
		initWidget(uiBinder.createAndBindUi(this));
	}
	
	public void render(WizardElement wizardElement){
		this.selectedWizardElement = wizardElement;
		wizardRendererPanel.clear();
		if(selectedWizardElement instanceof WizardTopic){
			renderTopic();
		} else if(selectedWizardElement instanceof WizardSlide){
			renderSlide();
		} else {
			KornellNotification.show("Não foi possível visualizar esse componente.", AlertType.ERROR);
		}
	}

	private void renderTopic() {
		WizardTopic topic = (WizardTopic) selectedWizardElement;
		if(StringUtils.isSome(topic.getTitle())){
			Label slideTitleLabel = new Label(topic.getTitle());
			slideTitleLabel.setStyleName("topicTitleLabel highlightText");
			wizardRendererPanel.add(slideTitleLabel);
		}
	}

	private void renderSlide() {
		WizardSlide slide = (WizardSlide) selectedWizardElement;
		if(StringUtils.isSome(slide.getTitle())){
			Label slideTitleLabel = new Label(slide.getTitle());
			slideTitleLabel.setStyleName("slideTitleLabel highlightText");
			wizardRendererPanel.add(slideTitleLabel);
		}
		for(WizardSlideItem wizardSlideItem : slide.getWizardSlideItems()){
			renderSlideItem(wizardSlideItem);
		}
	}

	private void renderSlideItem(WizardSlideItem wizardSlideItem) {
		FlowPanel slideItemPanel = new FlowPanel();
		slideItemPanel.addStyleName("slideItemPanel");
		if(StringUtils.isSome(wizardSlideItem.getTitle())){
			Label slideItemTitleLabel = new Label(wizardSlideItem.getTitle());
			slideItemTitleLabel.setStyleName("slideItemTitleLabel highlightText");
			slideItemPanel.add(slideItemTitleLabel);
		}
		if(WizardSlideItemType.VIDEO_LINK.equals(wizardSlideItem.getWizardSlideItemType())){
			WizardSlideItemVideoLink wizardSlideItemVideoLink = AutoBeanCodex.decode(WizardUtils.WIZARD_FACTORY, WizardSlideItemVideoLink.class, wizardSlideItem.getExtra()).as();
			
			FlowPanel slideItemVideoLinkPanel = new FlowPanel();
			slideItemVideoLinkPanel.setStyleName("slideItemVideoLinkPanel");
			Frame frame = new Frame("https://www.youtube.com/embed/"+WizardUtils.stripIdFromVideoURL(wizardSlideItemVideoLink.getURL()));
			FrameElement iframe = frame.getElement().cast();
			iframe.addClassName("youtube-player");
			iframe.setAttribute("type", "text/html");
			placeIframe(iframe);
			iframe.setAttribute("frameborder", "0");
			//allowing html5 video player to work on fullscreen inside the iframe
			iframe.setAttribute("allowFullScreen", "true");
			iframe.setAttribute("webkitallowfullscreen", "true");
			iframe.setAttribute("mozallowfullscreen", "true");
			slideItemVideoLinkPanel.add(frame);
			

			Window.addResizeHandler(new ResizeHandler() {
				@Override
				public void onResize(ResizeEvent event) {
					Scheduler.get().scheduleDeferred(new Command() {
						@Override
						public void execute() {
							placeIframe(iframe);
						}
					});
				}
			});
			
			slideItemPanel.add(slideItemVideoLinkPanel);
		}
		if(WizardSlideItemType.IMAGE.equals(wizardSlideItem.getWizardSlideItemType())){
			WizardSlideItemImage wizardSlideItemImage = AutoBeanCodex.decode(WizardUtils.WIZARD_FACTORY, WizardSlideItemImage.class, wizardSlideItem.getExtra()).as();
			
			FlowPanel slideItemImagePanel = new FlowPanel();
			slideItemImagePanel.setStyleName("slideItemImagePanel");
			Image image = new Image(wizardSlideItemImage.getURL());
			slideItemImagePanel.add(image);
			
			slideItemPanel.add(slideItemImagePanel);
		}
		if(StringUtils.isSome(wizardSlideItem.getText())){
			Label slideItemTitleLabel = new Label(wizardSlideItem.getText());
			slideItemTitleLabel.setStyleName("slideItemTextLabel niceTextColor");
			slideItemPanel.add(slideItemTitleLabel);
		}

		wizardRendererPanel.add(slideItemPanel);
	}

	private void placeIframe(FrameElement iframe) {
		int height = Positioning.getClientHeightBetweenBars() * 50 / 100;
		iframe.setAttribute("width", "" + (height * 164 / 100));
		iframe.setAttribute("height", "" + height);
	}
}