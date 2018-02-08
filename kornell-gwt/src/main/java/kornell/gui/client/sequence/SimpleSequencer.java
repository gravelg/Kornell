package kornell.gui.client.sequence;

import java.util.List;
import java.util.logging.Logger;

import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.lom.Actom;
import kornell.core.util.StringUtils;
import kornell.gui.client.event.ActomEnteredEvent;

public abstract class SimpleSequencer implements Sequencer {
    static final Logger logger = Logger.getLogger(SimpleSequencer.class.getName());
    protected KornellSession session;
    protected EventBus bus;
    private String enrollmentUUID;
    protected Actom currentActom;
    protected List<Actom> actoms;
    protected int currentIndex;

    public SimpleSequencer(EventBus bus, KornellSession session) {
        this.session = session;
        this.bus = bus;
        bus.addHandler(NavigationRequest.TYPE, this);
    }

    protected void dropBreadcrumb() {
        session.setItem(getBreadcrumbKey(), currentKey());
        String key = "";
        if (currentActom != null) {
            key = currentActom.getKey();
            currentActom.setVisited(true);
        }
        if (StringUtils.isNone(key))
            logger.warning("Could not drop breadcrumb for empty key");
        bus.fireEvent(new ActomEnteredEvent(enrollmentUUID, key));
        fireProgressEvent();
    }

    protected String getBreadcrumbKey() {
        return "sequencer." + enrollmentUUID + ".CURRENT_KEY";
    }

    protected String currentKey() {
        return currentActom != null ? currentActom.getKey() : "";
    }

    protected void setEnrollmentUUID(String enrollmentUUID) {
        this.enrollmentUUID = enrollmentUUID;
    }

    protected int lookupCurrentIndex(String currentKey) {
        int currentIndex = 0;
        if (currentKey != null && !currentKey.isEmpty()) {
            for (int i = 0; i < actoms.size(); i++) {
                Actom actom = actoms.get(i);
                if (currentKey.equals(actom.getKey())) {
                    return i;
                }
            }
        }
        return currentIndex;
    }
}
