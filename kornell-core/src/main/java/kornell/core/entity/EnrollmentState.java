package kornell.core.entity;

//TODO: Persist transition-triggering events
//TODO: Document states and transitions
public enum EnrollmentState {
    notEnrolled, //???
    enrolled,    //Enrolled on class, directly by an Institution or by being approved after requesting
    requested,   //Requested participation in a private course
    denied,      //Participation request denied
    cancelled,    //Participation canceled by institution (payment, timeout, ?)
    deleted      //Soft-delete
    //finished     All content seen and all required evaluations either passed or failed
    //TODO: Abandoned?
}
