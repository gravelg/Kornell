package kornell.gui.client.sequence;

import java.util.logging.Logger;

import com.google.gwt.user.client.ui.FlowPanel;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.core.lom.Actom;
import kornell.core.lom.Contents;
import kornell.core.lom.ContentsOps;
import kornell.gui.client.event.ProgressEvent;
import kornell.gui.client.event.ViewReadyEvent;
import kornell.gui.client.event.ViewReadyEventHandler;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.gui.client.uidget.Uidget;

public class PrefetchSequencer extends SimpleSequencer implements Sequencer {
    Logger logger = Logger.getLogger(PrefetchSequencer.class.getName());
    private FlowPanel contentPanel;
    private String enrollmentUUID;
    private Actom nextActom;
    private Uidget nextUidget;
    private Uidget currentUidget;
    private Actom prevActom;
    private Uidget prevUidget;
    private boolean isActive;

    public PrefetchSequencer(EventBus bus, KornellSession session) {
        super(bus, session);
    }

    @Override
    public void onContinue(NavigationRequest event) {
        if (!isActive || doesntHaveNext())
            return;
        removePrevious();
        makeCurrentPrevious();
        makeNextCurrent();
        currentIndex++;
        preloadNext();
        makeCurrentVisible();
        dropBreadcrumb();
        debug("CONTINUED");
    }

    @Override
    public void onPrevious(NavigationRequest event) {
        if (!isActive || doesntHavePrevious())
            return;
        removeNext();
        makeCurrentNext();
        makePrevCurrent();
        currentIndex--;
        preloadPrevious();
        makeCurrentVisible();
        dropBreadcrumb();
        debug("PREVED");
    }

    @Override
    public void onDirect(NavigationRequest event) {
        if (isActive) {
            stop();
            orientateAndSail(event.getDestination());
        }
    }

    private void debug(String event) {
        String prevString = prevKey() + prevVis();
        String currString = currentKey() + currVis();
        String nextString = nextKey() + nextVis();

        logger.finer(event + " " + currentIndex + " [" + prevString + " | " + currString + " | " + nextString + "]");
    }

    private String nextVis() {
        return " " + (nextUidget != null ? nextUidget.isVisible() : "-");
    }

    private String currVis() {
        return " " + (currentUidget != null ? currentUidget.isVisible() : "-");
    }

    private String prevVis() {
        return " " + (prevUidget != null ? prevUidget.isVisible() : "-");
    }

    private String prevKey() {
        return prevActom != null ? prevActom.getKey() : "";
    }

    private String nextKey() {
        return nextActom != null ? nextActom.getKey() : "";
    }

    private boolean doesntHaveNext() {
        return actoms != null && currentIndex >= actoms.size() - 1;
    }

    private boolean doesntHavePrevious() {
        return actoms != null && currentIndex <= 0;
    }

    private void makeCurrentPrevious() {
        prevActom = currentActom;
        prevUidget = currentUidget;
    }

    private void makeNextCurrent() {
        currentActom = nextActom;
        currentUidget = nextUidget;
    }

    private void makeCurrentVisible() {
        if (currentUidget != null)
            currentUidget.setVisible(true);
        else
            logger.warning("CURRENT UIDGET IS NULL. HOW COME?");
        if (nextUidget != null)
            nextUidget.setVisible(false);
        if (prevUidget != null)
            prevUidget.setVisible(false);
    }

    private void makeCurrentNext() {
        nextUidget = currentUidget;
        nextActom = currentActom;
    }

    private void removePrevious() {
        if (prevUidget != null)
            contentPanel.remove(prevUidget);
        prevUidget = null;
        prevActom = null;
    }

    private void removeNext() {
        if (nextUidget != null)
            contentPanel.remove(nextUidget);
        nextUidget = null;
        nextActom = null;
    }

    private void removeCurrent() {
        if (currentUidget != null)
            contentPanel.remove(currentUidget);
        currentUidget = null;
        currentActom = null;
    }

    private void makePrevCurrent() {
        currentActom = prevActom;
        currentUidget = prevUidget;
    }

    @Override
    public Sequencer withPanel(FlowPanel contentPanel) {
        this.contentPanel = contentPanel;
        return this;
    }

    @Override
    public Sequencer withPlace(ClassroomPlace place) {
        setEnrollmentUUID(place.getEnrollmentUUID());
        return this;
    }

    @Override
    public void go(Contents contents) {
        setContents(contents);
        orientateAndSail();
    }

    private void orientateAndSail() {
        orientateAndSail(session.getItem(getBreadcrumbKey()));
    }

    private void orientateAndSail(String key) {
        currentIndex = lookupCurrentIndex(key);
        currentActom = actoms.get(currentIndex);
        initialLoad();
    }

    class ShowWhenReady implements ViewReadyEventHandler {
        private Uidget uidget;

        public ShowWhenReady(Uidget uidget) {
            this.uidget = uidget;
        }

        @Override
        public void onViewReady(ViewReadyEvent evt) {
            uidget.setVisible(true);
        }
    }

    private void initialLoad() {
        isActive = true;
        showCurrentASAP();
        preloadNext();
        preloadPrevious();
        makeCurrentVisible();
        debug("INITIAL");
    }

    private void preloadNext() {
        if (!isActive || doesntHaveNext()) {
            nextActom = null;
            nextUidget = null;
        } else {
            int nextIndex = currentIndex + 1;
            nextActom = actoms.get(nextIndex);
            nextUidget = Uidget.forActom(nextActom);
            contentPanel.add(nextUidget);
        }
    }

    private void preloadPrevious() {
        if (!isActive || doesntHavePrevious()) {
            prevActom = null;
            prevUidget = null;
        } else {
            int previousIndex = currentIndex - 1;
            prevActom = actoms.get(previousIndex);
            prevUidget = Uidget.forActom(prevActom);
            contentPanel.add(prevUidget);
        }
    }

    private void showCurrentASAP() {
        if (!isActive) {
            currentActom = null;
            currentUidget = null;
        } else {
            currentUidget = Uidget.forActom(currentActom);
            currentUidget.setVisible(false);
            currentUidget.onViewReady(new ShowWhenReady(currentUidget));
            contentPanel.add(currentUidget);
            dropBreadcrumb();
            makeCurrentVisible();
        }
    }

    private void setContents(Contents contents) {
        this.actoms = ContentsOps.collectActoms(contents);
    }

    // TODO: Smells... progress acct may be different per ContentSpec
    @Override
    public void fireProgressEvent() {
        if (actoms == null)
            return;
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
        progressEvent.setEnrollmentUUID(enrollmentUUID);
        bus.fireEvent(progressEvent);
    }

    @Override
    public void stop() {
        isActive = false;
        removeCurrent();
        removeNext();
        removePrevious();
        contentPanel.clear();
    }
}