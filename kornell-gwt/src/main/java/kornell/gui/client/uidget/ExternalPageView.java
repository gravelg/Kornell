package kornell.gui.client.uidget;

import java.util.logging.Logger;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.IFrameElement;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.place.shared.PlaceChangeEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.EventListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FlowPanel;

import kornell.api.client.KornellSession;
import kornell.core.entity.InstitutionType;
import kornell.core.lom.ExternalPage;
import kornell.core.util.StringUtils;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.event.ShowChatDockEvent;
import kornell.gui.client.event.ShowChatDockEventHandler;
import kornell.gui.client.util.view.Positioning;

public class ExternalPageView extends Uidget implements ShowChatDockEventHandler {
    private static final Logger logger = Logger.getLogger(ExternalPageView.class.getName());
    private IFrameElement iframe;

    FlowPanel panel = new FlowPanel();
    private KornellSession session;

    public ExternalPageView(ExternalPage page) {
        GenericClientFactoryImpl.EVENT_BUS.addHandler(ShowChatDockEvent.TYPE, this);
        session = GenericClientFactoryImpl.KORNELL_SESSION;
        createIFrame();
        panel.setStyleName("contentWrapper");
        panel.getElement().appendChild(iframe);
        String url = page.getURL();
        String key = page.getKey();
        setSrc(url, key);
        initWidget(panel);

        GenericClientFactoryImpl.EVENT_BUS.addHandler(PlaceChangeEvent.TYPE, new PlaceChangeEvent.Handler() {
            @Override
            public void onPlaceChange(PlaceChangeEvent event) {
                placeIframe();
            }
        });
    }

    private void createIFrame() {
        if (iframe == null) {
            iframe = Document.get().createIFrameElement();
            iframe.addClassName("externalContent");
            iframe.setAttribute("allowtransparency", "true");
            iframe.setAttribute("style", "background-color: transparent;");
            // allowing html5 video player to work on fullscreen inside the iframe
            iframe.setAttribute("allowFullScreen", "true");
            iframe.setAttribute("webkitallowfullscreen", "true");
            iframe.setAttribute("mozallowfullscreen", "true");
            Event.sinkEvents(iframe, Event.ONLOAD);
            Event.setEventListener(iframe, new EventListener() {

                @Override
                public void onBrowserEvent(Event event) {
                    fireViewReady();

                }
            });
        }
        placeIframe();

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
    }

    private void placeIframe() {
        iframe.setPropertyString("width", "100%");
        int h = (Window.getClientHeight() - Positioning.NORTH_BAR);
        if (session.getCurrentCourseClass() != null
                && !(InstitutionType.DASHBOARD.equals(session.getInstitution().getInstitutionType()))) {
            h -= Positioning.SOUTH_BAR;
        }
        String height = h + "px";
        iframe.setPropertyString("height", height);
    }

    public void setSrc(final String src, final String actomKey) {
        // TODO: Check if src exists
        String mkurl = StringUtils.mkurl("/", src);
        logger.info("Iframe source set to [" + mkurl + "]");
        iframe.setSrc(mkurl);
    }

    @Override
    public void onShowChatDock(ShowChatDockEvent event) {
        if (event.isShowChatDock()) {
            panel.addStyleName("chatDocked");
        } else {
            panel.removeStyleName("chatDocked");
        }
    }

}