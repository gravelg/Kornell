package kornell.server.ep

import kornell.server.jdbc.repository.EnrollmentRepo
import java.math.BigDecimal

/**
 * Simple Event Processing
 */
object EnrollmentSEP {

  def onProgress(enrollmentUUID: String): Unit = {
    EnrollmentRepo(enrollmentUUID).updateProgress()
  }

  def onAssessment(enrollmentUUID: String): Unit =
    EnrollmentRepo(enrollmentUUID).updateAssessment()

  def onPreAssessmentScore(enrollmentUUID: String, score: BigDecimal): Unit =
    EnrollmentRepo(enrollmentUUID).updatePreAssessmentScore(score)

  def onPostAssessmentScore(enrollmentUUID: String, score: BigDecimal): Unit =
    EnrollmentRepo(enrollmentUUID).updatePostAssessmentScore(score)

}
