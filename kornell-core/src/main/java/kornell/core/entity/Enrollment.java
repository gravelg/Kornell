package kornell.core.entity;

import java.math.BigDecimal;
import java.util.Date;

public interface Enrollment extends Entity{
    public static String TYPE = EntityFactory.PREFIX + "enrollment+json";

    Date getEnrolledOn();
    void setEnrolledOn(Date enrolledOn);

    String getCourseClassUUID();
    void setCourseClassUUID(String courseClassUUID);

    String getCourseVersionUUID();
    void setCourseVersionUUID(String courseVersionUUID);

    String getPersonUUID();
    void setPersonUUID(String personUUID);

    Integer getProgress();
    void setProgress(Integer progress);

    String getNotes();
    void setNotes(String notes);

    String getLastActomVisited();
    void setLastActomVisited(String lastActomVisited);

    EnrollmentState getState();
    void setState(EnrollmentState state);

    Date getLastProgressUpdate();
    void setLastProgressUpdate(Date lastProgressUpdate);

    /**
     * use lastProgressUpdate / lastAssessmentUpdate
     * @return
     */
    @Deprecated
    Date getCompletionDate();
    @Deprecated
    void setCompletionDate(Date completionDate);

    Assessment getAssessment();
    void setAssessment(Assessment assessment);

    Date getLastAssessmentUpdate();
    void setLastAssessmentUpdate(Date lastAssessmentUpdate);

    Date getCertifiedAt();
    void setCertifiedAt(Date certifiedAt);

    BigDecimal getAssessmentScore();
    void setAssessmentScore(BigDecimal assessmentScore);

    Date getLastBilledAt();
    void setLastBilledAt(Date lastBilled);

    String getParentEnrollmentUUID();
    void setParentEnrollmentUUID(String enrollmentUUID);

    Date getStartDate();
    void setStartDate(Date startDate);

    Date getEndDate();
    void setEndDate(Date endDate);

    BigDecimal getPreAssessmentScore();
    void setPreAssessmentScore(BigDecimal preAssessmentScore);

    BigDecimal getPostAssessmentScore();
    void setPostAssessmentScore(BigDecimal assessmentScore);

    EnrollmentSource getEnrollmentSource();
    void setEnrollmentSource(EnrollmentSource enrollmentSource);
}
