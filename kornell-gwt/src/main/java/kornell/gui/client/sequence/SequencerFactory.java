package kornell.gui.client.sequence;

import kornell.gui.client.presentation.classroom.ClassroomPlace;

public interface SequencerFactory {

    Sequencer withPlace(ClassroomPlace place);

}
