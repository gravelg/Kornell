package kornell.gui.client.personnel;

import com.github.gwtbootstrap.client.ui.Alert;
import com.github.gwtbootstrap.client.ui.constants.AlertType;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.Callback;
import kornell.api.client.KornellSession;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;
import kornell.gui.client.KornellConstants;
import kornell.gui.client.event.ActomEnteredEvent;
import kornell.gui.client.event.ActomEnteredEventHandler;
import kornell.gui.client.event.LoginEvent;
import kornell.gui.client.event.LoginEventHandler;
import kornell.gui.client.util.view.Positioning;

public class Stalker implements ActomEnteredEventHandler, LoginEventHandler {

    private KornellConstants constants = GWT.create(KornellConstants.class);
    private KornellSession session;
    private String versionAPI = null;
    private Timer seuInacioTimer, heartbeatTimer;
    private PopupPanel popup;

    public Stalker(EventBus bus, KornellSession session) {
        this.session = session;

        startSeuInacioTimer();
        startHeartBeatTimer();
        initVersionPopup();

        bus.addHandler(ActomEnteredEvent.TYPE, this);
        bus.addHandler(LoginEvent.TYPE, this);
    }

    private void showVersionPopup() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                popup.setPopupPositionAndShow(new PopupPanel.PositionCallback() {
                    public void setPosition(int offsetWidth, int offsetHeight) {
                        int left = (Window.getClientWidth() - offsetWidth) / 2;
                        int top = Positioning.hasPlaceBar() ? Positioning.NORTH_BAR_PLUS : Positioning.NORTH_BAR;
                        popup.setPopupPosition(left, top);
                    }
                });
            }
        });
    }

    private void initVersionPopup() {
        popup = new PopupPanel();

        Alert alert = new Alert();
        alert.addStyleName("kornellMessage");
        alert.setType(AlertType.WARNING);
        String text = constants.newVersionAvailable();
        text += " <a onclick=\"location.reload()\"> " + constants.refresh() + "</a>.";
        alert.setHTML(text);

        popup.setWidget(alert);
    }

    private void startSeuInacioTimer() {
        scheduleAttendanceSheetSigning();

        seuInacioTimer = new Timer() {
            public void run() {
                scheduleAttendanceSheetSigning();
            }
        };

        // Schedule the timer to run daily
        seuInacioTimer.scheduleRepeating(24 * 60 * 60 * 1000);
    }

    private void scheduleAttendanceSheetSigning() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                signAttendanceSheet();
            }
        });
    }

    private void signAttendanceSheet() {
        if (session.isAnonymous())
            return;
        String institutionUUID = session.getInstitution().getUUID();
        String personUUID = session.getCurrentUser().getPerson().getUUID();
        session.events().attendanceSheetSigned(institutionUUID, personUUID).fire(new Callback<Void>() {
            @Override
            public void ok(Void to) {
                /* nothing to do */
            }
        });
    }

    private void startHeartBeatTimer() {
        scheduleHeartbeat();

        heartbeatTimer = new Timer() {
            public void run() {
                scheduleHeartbeat();
            }
        };

        // Schedule the timer to run every 10 minutes
        heartbeatTimer.scheduleRepeating(10 * 60 * 1000);
    }

    private void scheduleHeartbeat() {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                heartbeat();
            }
        });
    }

    private void heartbeat() {
        session.checkVersionAPI(new Callback<String>() {
            @Override
            public void ok(String text) {
                if (StringUtils.isSome(text)) {
                    if (StringUtils.isSome(versionAPI) && !text.equals(versionAPI)) {
                        showVersionPopup();
                    }
                    versionAPI = text;
                }
            }
        });
    }

    @Override
    public void onActomEntered(ActomEnteredEvent event) {
        session.events().actomEntered(event.getEnrollmentUUID(), event.getActomKey()).fire();
    }

    @Override
    public void onLogin(UserInfoTO user) {
        scheduleAttendanceSheetSigning();
    }
}
