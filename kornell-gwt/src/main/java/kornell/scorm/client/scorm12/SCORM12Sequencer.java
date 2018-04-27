package kornell.scorm.client.scorm12;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.lom.Actom;
import kornell.core.lom.Contents;
import kornell.core.lom.ContentsOps;
import kornell.core.util.StringUtils;
import kornell.gui.client.event.NavigationAuthorizationEvent;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.sequence.NavigationRequest;
import kornell.gui.client.sequence.Sequencer;
import kornell.gui.client.sequence.SimpleSequencer;
import kornell.gui.client.uidget.Uidget;

public class SCORM12Sequencer extends SimpleSequencer implements Sequencer {

    private ClassroomPlace place;
    private FlowPanel contentPanel;
    private Uidget currentUidget;
    private boolean isActive;

    public SCORM12Sequencer(EventBus bus, KornellSession session) {
        super(bus, session);
    }

    @Override
    public void onContinue(NavigationRequest event) {
        if (!isActive) {
            return;
        }
        if (currentIndex < (actoms.size() - 1)) {
            currentIndex++;
            paintCurrent();
        } else {
            bus.fireEvent(NavigationAuthorizationEvent.next(false));
        }
    }

    @Override
    public void onPrevious(NavigationRequest event) {
        if (!isActive) {
            return;
        }
        if (currentIndex > 0) {
            currentIndex--;
            paintCurrent();
        } else {
            bus.fireEvent(NavigationAuthorizationEvent.prev(false));
        }
    }

    @Override
    public void onDirect(NavigationRequest event) {
        launch(event.getDestination());
    }

    private void launch(String key) {
        isActive = true;
        currentIndex = StringUtils.isSome(key) ? lookupCurrentIndex(key) : 0;
        if(currentIndex < actoms.size()){
            currentActom = actoms.get(currentIndex);
            paintCurrent();
        }
    }

    @Override
    public Sequencer withPanel(FlowPanel contentPanel) {
        this.contentPanel = contentPanel;
        return this;
    }

    @Override
    public Sequencer withPlace(ClassroomPlace place) {
        this.place = place;
        setEnrollmentUUID(place.getEnrollmentUUID());
        return this;
    }

    @Override
    public void go(Contents contents) {
        setContents(contents);
        String currentKey = session.getItem(getBreadcrumbKey());
        launch(currentKey);
    }

    private void paintCurrent() {
        if (!isActive) {
            return;
        }
        if (contentPanel != null) {
            contentPanel.clear();
        }
        currentActom = actoms.get(currentIndex);
        currentUidget = Uidget.forActom(currentActom);
        currentUidget.setVisible(false);
        contentPanel.add(currentUidget);
        dropBreadcrumb();
        makeCurrentVisible();
    }

    private void makeCurrentVisible() {
        currentUidget.setVisible(true);
    }

    private void setContents(Contents contents) {
        this.actoms = ContentsOps.collectActoms(contents);
    }

    @Override
    public void stop() {
        isActive = false;
        contentPanel.clear();
    }

    // TODO: Smell - Activity Bar shows only after this fires.
    @Override
    public void fireProgressEvent() {
        if (actoms == null) {
            return;
        }
        int pagesVisitedCount = 0;
        int totalPages = actoms.size();
        for (Actom actom : actoms) {
            if (actom.isVisited()) {
                pagesVisitedCount++;
                continue;
            }
            break;
        }
        ProgressEvent progressEvent = new ProgressEvent();
        progressEvent.setCurrentPage(currentIndex + 1);
        progressEvent.setTotalPages(totalPages);
        progressEvent.setPagesVisitedCount(pagesVisitedCount);
        progressEvent.setEnrollmentUUID(place.getEnrollmentUUID());
        bus.fireEvent(progressEvent);
    }

}
