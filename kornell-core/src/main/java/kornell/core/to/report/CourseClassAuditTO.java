package kornell.core.to.report;

import java.util.Date;

public class CourseClassAuditTO {
    private Date eventFiredAt;
    private String eventType;
    private String adminFullName;
    private String adminUsername;
    private String participantFullName;
    private String participantUsername;
    private String fromCourseClassName;
    private String toCourseClassName;
    private String fromState;
    private String toState;
    private String adminUUID;
    private String participantUUID;
    private String enrollmentUUID;
    private String fromCourseClassUUID;
    private String toCourseClassUUID;
    public Date getEventFiredAt() {
        return eventFiredAt;
    }
    public void setEventFiredAt(Date eventFiredAt) {
        this.eventFiredAt = eventFiredAt;
    }
    public String getEventType() {
        return eventType;
    }
    public void setEventType(String eventType) {
        this.eventType = eventType;
    }
    public String getAdminFullName() {
        return adminFullName;
    }
    public void setAdminFullName(String adminFullName) {
        this.adminFullName = adminFullName;
    }
    public String getAdminUsername() {
        return adminUsername;
    }
    public void setAdminUsername(String adminUsername) {
        this.adminUsername = adminUsername;
    }
    public String getParticipantFullName() {
        return participantFullName;
    }
    public void setParticipantFullName(String participantFullName) {
        this.participantFullName = participantFullName;
    }
    public String getParticipantUsername() {
        return participantUsername;
    }
    public void setParticipantUsername(String participantUsername) {
        this.participantUsername = participantUsername;
    }
    public String getFromCourseClassName() {
        return fromCourseClassName;
    }
    public void setFromCourseClassName(String fromCourseClassName) {
        this.fromCourseClassName = fromCourseClassName;
    }
    public String getToCourseClassName() {
        return toCourseClassName;
    }
    public void setToCourseClassName(String toCourseClassName) {
        this.toCourseClassName = toCourseClassName;
    }
    public String getFromState() {
        return fromState;
    }
    public void setFromState(String fromState) {
        this.fromState = fromState;
    }
    public String getToState() {
        return toState;
    }
    public void setToState(String toState) {
        this.toState = toState;
    }
    public String getAdminUUID() {
        return adminUUID;
    }
    public void setAdminUUID(String adminUUID) {
        this.adminUUID = adminUUID;
    }
    public String getParticipantUUID() {
        return participantUUID;
    }
    public void setParticipantUUID(String participantUUID) {
        this.participantUUID = participantUUID;
    }
    public String getEnrollmentUUID() {
        return enrollmentUUID;
    }
    public void setEnrollmentUUID(String enrollmentUUID) {
        this.enrollmentUUID = enrollmentUUID;
    }
    public String getFromCourseClassUUID() {
        return fromCourseClassUUID;
    }
    public void setFromCourseClassUUID(String fromCourseClassUUID) {
        this.fromCourseClassUUID = fromCourseClassUUID;
    }
    public String getToCourseClassUUID() {
        return toCourseClassUUID;
    }
    public void setToCourseClassUUID(String toCourseClassUUID) {
        this.toCourseClassUUID = toCourseClassUUID;
    }


}
