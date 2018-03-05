package kornell.gui.client.personnel;

import static kornell.core.util.StringUtils.isSome;
import static kornell.core.util.StringUtils.mkurl;

import com.google.gwt.core.client.Callback;
import com.google.gwt.dom.client.Document;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.to.UnreadChatThreadTO;
import kornell.gui.client.event.LogoutEvent;
import kornell.gui.client.event.LogoutEventHandler;
import kornell.gui.client.event.UnreadMessagesCountChangedEvent;
import kornell.gui.client.event.UnreadMessagesCountChangedEventHandler;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEvent;
import kornell.gui.client.event.UnreadMessagesPerThreadFetchedEventHandler;
import kornell.gui.client.util.CSSInjector;
import kornell.gui.client.util.view.KornellMaintenance;

public class Dean implements LogoutEventHandler, UnreadMessagesPerThreadFetchedEventHandler,
        UnreadMessagesCountChangedEventHandler {

    private String ICON_NAME = "favicon.ico";
    private String DEFAULT_SITE_TITLE = "Kornell";
    private EventBus bus;
    private KornellSession session;
    private int totalCount;

    public Dean(EventBus bus, KornellSession session) {
        this.bus = bus;
        this.session = session;

        bus.addHandler(LogoutEvent.TYPE, this);
        bus.addHandler(UnreadMessagesPerThreadFetchedEvent.TYPE, this);
        bus.addHandler(UnreadMessagesCountChangedEvent.TYPE, this);

        initInstitutionAssets();
    }

    private void initInstitutionAssets() {
        showContentNative(false);

        setFavicon();
        setPageTitle();
        setSkin();
    }

    private void setFavicon() {
        String url = session.getInstitutionAssetsURL();
        if (url != null) {
            updateFaviconNative(mkurl(url, ICON_NAME));
        } else {
            updateFaviconNative(ICON_NAME);
        }
    }

    private void setSkin() {
        Callback<Void, Exception> callback = new Callback<Void, Exception>() {
            public void onFailure(Exception reason) {
                KornellMaintenance.show();
            }

            public void onSuccess(Void result) {
                showContentNative(true);
            }
        };

        String skinName = isSome(session.getInstitution().getSkin()) ? session.getInstitution().getSkin() : "";

        CSSInjector.updateSkin(skinName, callback);
    }

    private void setPageTitle() {
        String name = session.getInstitution().getFullName();
        String title = DEFAULT_SITE_TITLE;
        if (name != null) {
            title = name;
            if (totalCount > 0)
                title = "(" + totalCount + ") " + name;
        }
        Document.get().setTitle(title);
    }

    @Override
    public void onUnreadMessagesPerThreadFetched(UnreadMessagesPerThreadFetchedEvent event) {
        int count = 0;
        for (UnreadChatThreadTO unreadChatThreadTO : event.getUnreadChatThreadTOs()) {
            count = count + Integer.parseInt(unreadChatThreadTO.getUnreadMessages());
        }
        totalCount = count;
        setPageTitle();
    }

    @Override
    public void onUnreadMessagesCountChanged(UnreadMessagesCountChangedEvent event) {
        totalCount = event.isIncrement() ? totalCount + event.getCountChange() : totalCount - event.getCountChange();
        setPageTitle();
    }

    @Override
    public void onLogout() {
        showBodyNative(false);
    }

    private static native void updateFaviconNative(String url) /*-{
        var link = $wnd.document.createElement('link'), oldLink = $wnd.document
            .getElementById('icon');
        link.id = 'icon';
        link.rel = 'shortcut icon';
        link.type = 'image/x-icon';
        link.href = url + "?v=" + new Date().getTime();
        if (oldLink) {
            $wnd.document.head.removeChild(oldLink);
        }
        
        $wnd.document.getElementsByTagName('head')[0].appendChild(link);
    }-*/;

    public static native void showContentNative(boolean show) /*-{
        var menuBar = $wnd.document.getElementsByClassName('menuBar'),
            vScrollBar = $wnd.document.getElementsByClassName('vScrollBar'),
            activityBarWrapper = $wnd.document.getElementsByClassName('activityBarWrapper'),
            style = 'display: ' + (show ? 'block' : 'none');
        if(menuBar && menuBar.length){
            menuBar[0].setAttribute('style', style);
        }
        if(vScrollBar && vScrollBar.length){
            vScrollBar[0].setAttribute('style', style);
        }
        if(activityBarWrapper && activityBarWrapper.length){
            activityBarWrapper[0].setAttribute('style', style);
        }
    }-*/;

    public static native void showBodyNative(boolean show) /*-{
        $wnd.document.getElementsByTagName('body')[0].setAttribute('style',
            'display: ' + (show ? 'block' : 'none'));
    }-*/;

}
