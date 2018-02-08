package kornell.gui.client.sequence;

import java.util.logging.Logger;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

import kornell.api.client.KornellSession;
import kornell.gui.client.presentation.classroom.ClassroomPlace;
import kornell.scorm.client.scorm12.SCORM12Sequencer;

public class SequencerFactoryImpl implements SequencerFactory {

    Logger logger = Logger.getLogger(SequencerFactoryImpl.class.getName());
    private KornellSession session;
    private EventBus bus;

    public SequencerFactoryImpl(EventBus bus, PlaceController ctrl, KornellSession session) {
        this.session = session;
        this.bus = bus;
    }

    @Override
    public Sequencer withPlace(ClassroomPlace place) {
        logger.info("Creating course sequencer");
        Sequencer sequencer = null;
        switch (place.getContentSpec()) {
        case SCORM12:
            sequencer = new SCORM12Sequencer(bus, session);
            break;
        case KNL:
        case WIZARD:
            sequencer = new PrefetchSequencer(bus, session);
            break;
        }
        return sequencer.withPlace(place);
    }

}