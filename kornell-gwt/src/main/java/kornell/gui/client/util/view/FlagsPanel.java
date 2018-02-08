package kornell.gui.client.util.view;

import static kornell.core.util.StringUtils.mkurl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.MenuBar;

import kornell.core.util.StringUtils;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.util.ClientConstants;
import kornell.gui.client.util.ClientProperties;

public class FlagsPanel extends FlowPanel {

    private static KornellConstants constants = GWT.create(KornellConstants.class);
    private int oldPosition;

    public FlagsPanel() {

        String locale = ClientProperties.getLocaleCookie();

        String allowedLanguages = GenericClientFactoryImpl.KORNELL_SESSION.getInstitution().getAllowedLanguages();
        if (StringUtils.isSome(allowedLanguages) && allowedLanguages.contains(",")) {
            Map<String, String> localeToFlagsImage = new HashMap<String, String>();
            String blank = mkurl(ClientConstants.IMAGES_PATH, "blank.gif");
            localeToFlagsImage.put("pt_BR",
                    "<img src=\"" + blank + "\" class=\"flag flag-br\" alt=\"BR\" title=\"Português\"/>");
            localeToFlagsImage.put("en",
                    "<img src=\"" + blank + "\" class=\"flag flag-gb\" alt=\"EN\" title=\"English\" />");
            // localeToFlagsImage.put("fr", "<img src=\""+blank+"\" class=\"flag
            // flag-fr\" alt=\"FR\" title=\"Français\" />");
            // localeToFlagsImage.put("es", "<img src=\""+blank+"\" class=\"flag
            // flag-es\" alt=\"ES\" title=\"Español\" />");

            Map<String, Command> localeToCommand = new HashMap<String, Command>();
            localeToCommand.put("pt_BR", new Command() {
                public void execute() {
                    ClientProperties.setLocaleCookie("pt_BR");
                }
            });
            localeToCommand.put("en", new Command() {
                public void execute() {
                    ClientProperties.setLocaleCookie("en");
                }
            });
            localeToCommand.put("fr", new Command() {
                public void execute() {
                    ClientProperties.setLocaleCookie("fr");
                }
            });
            localeToCommand.put("es", new Command() {
                public void execute() {
                    ClientProperties.setLocaleCookie("es");
                }
            });

            MenuBar flagBar = new MenuBar(false);

            Iterator<Map.Entry<String, String>> entries = localeToFlagsImage.entrySet().iterator();
            while (entries.hasNext()) {
                Map.Entry<String, String> entry = entries.next();
                if (!entry.getKey().equals(locale)) {
                    flagBar.addItem(entry.getValue(), true, localeToCommand.get(entry.getKey()));
                }
            }
            flagBar.addItem("<span class=\"flagPopupText\">" + constants.selectLanguage() + "</span>", true,
                    new Command() {
                        public void execute() {
                        }
                    });

            MenuBar topBar = new MenuBar(true);
            topBar.setAutoOpen(true);
            topBar.setAnimationEnabled(true);
            topBar.addItem(localeToFlagsImage.get(locale), true, flagBar);

            // when the user scrolls down, the language bar should be closed
            oldPosition = topBar.getAbsoluteTop();
            Timer unreadMessagesCountPerThreadTimer = new Timer() {
                public void run() {
                    if (oldPosition != topBar.getAbsoluteTop() && flagBar.isVisible()) {
                        oldPosition = topBar.getAbsoluteTop();
                        topBar.closeAllChildren(true);
                    }
                }
            };
            // Schedule the timer to run every 1/4 second
            unreadMessagesCountPerThreadTimer.scheduleRepeating(250);

            this.add(topBar);
        }
        this.addStyleName("flagWrapper");
    }

}
