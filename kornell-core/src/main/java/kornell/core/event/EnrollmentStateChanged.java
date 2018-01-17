package kornell.core.event;

import kornell.core.entity.EnrollmentState;


public interface EnrollmentStateChanged extends Event {
    public static final String TYPE = EventFactory.PREFIX+"EnrollmentStateChanged+json";

    String getFromPersonUUID();
    void setFromPersonUUID(String fromPersonUUID);

    String getEnrollmentUUID();
    void setEnrollmentUUID(String enrollmentUUID);

    EnrollmentState getFromState();
    void setFromState(EnrollmentState fromState);

    EnrollmentState getToState();
    void setToState(EnrollmentState toState);
}
