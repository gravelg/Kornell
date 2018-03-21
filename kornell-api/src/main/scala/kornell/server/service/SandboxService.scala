package kornell.server.service

import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.core.entity.InstitutionType
import kornell.server.repository.Entities
import kornell.core.util.UUID
import java.util.Date
import kornell.core.entity.EntityState
import kornell.core.entity.RegistrationType
import kornell.server.authentication.ThreadLocalAuthenticator
import java.math.BigDecimal
import kornell.server.jdbc.repository.RolesRepo
import kornell.core.entity.RoleCategory
import scala.collection.JavaConverters._
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.core.entity.EnrollmentState
import kornell.core.entity.EnrollmentSource
import kornell.server.jdbc.repository.EventsRepo
import kornell.core.to.RolesTO
import scala.collection.mutable.ListBuffer
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.SQL._
import kornell.core.entity.CourseClass

object SandboxService {

  def processInstitution(institutionUUID: String) = {
    val versions = CourseVersionsRepo.allByInstitution(institutionUUID)
    versions.foreach(courseVersion => processVersion(courseVersion.getUUID, institutionUUID))
  }

  def processVersion(courseVersionUUID: String, institutionUUID: String) = {
    val sandboxClass = CourseClassesRepo.sandboxForVersion(courseVersionUUID)
    if (sandboxClass.isEmpty) {
      //create the class
      val version = CourseVersionRepo(courseVersionUUID).get
      val course = CourseRepo(version.getCourseUUID).get
      val institution = InstitutionRepo(institutionUUID).get
      //only create the class for DEFAULT institutions
      if (version.getParentVersionUUID == null) {
        val creator = if (ThreadLocalAuthenticator.getAuthenticatedPersonUUID.isDefined) ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get else "system"
        val courseClass = CourseClassesRepo.create(Entities.newCourseClass(
          uuid = UUID.random,
          name = "[Sandbox] " + course.getName + " / " + version.getName,
          courseVersionUUID = courseVersionUUID,
          institutionUUID = institution.getUUID,
          requiredScore = new BigDecimal(0.00),
          publicClass = false,
          overrideEnrollments = false,
          invisible = false,
          maxEnrollments = 1000,
          createdAt = new Date(),
          createdBy = creator,
          state = EntityState.active,
          registrationType = RegistrationType.email,
          institutionRegistrationPrefixUUID = null,
          courseClassChatEnabled = false,
          chatDockEnabled = false,
          allowBatchCancellation = false,
          tutorChatEnabled = false,
          approveEnrollmentsAutomatically = false,
          startDate = null,
          ecommerceIdentifier = null,
          thumbUrl = null,
          sandbox = true))
        enrollAdmins(courseClass.getUUID, institution.getUUID)
      }
    } else {
      //fix enrollments
      enrollAdmins(sandboxClass.get.getUUID, sandboxClass.get.getInstitutionUUID)
    }
  }

  def manageExistingEnrollment(sandboxClassUUID: String, personUUID: String, state: EnrollmentState) = {
    val enrollment = EnrollmentsRepo.byCourseClassAndPerson(sandboxClassUUID, personUUID, false).get
    EventsRepo.logEnrollmentStateChanged(UUID.random, ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get, enrollment.getUUID, EnrollmentState.enrolled, state, false, "Sandbox class enrollment: " + state.toString)
    EventsRepo.deleteActoms(enrollment.getUUID)
  }

  def enrollAdmins(sandboxClassUUID: String, institutionUUID: String) = {
    val rolesTO = RolesRepo.getAllAdminsForInstitution(institutionUUID, RoleCategory.BIND_DEFAULT)
    for (roleTO <- rolesTO.getRoleTOs.asScala) {
      val enrollment = EnrollmentsRepo.byCourseClassAndPerson(sandboxClassUUID, roleTO.getRole.getPersonUUID, false)
      if (enrollment.isEmpty) {
        enrollAdmin(sandboxClassUUID, roleTO.getRole.getPersonUUID)
      }
    }
  }

  def enrollAdmin(sandboxClassUUID: String, personUUID: String) = {
    val enrollment = EnrollmentsRepo.create(
      courseClassUUID = sandboxClassUUID,
      personUUID = personUUID,
      enrollmentState = EnrollmentState.enrolled,
      courseVersionUUID = null,
      parentEnrollmentUUID = null,
      enrollmentSource = EnrollmentSource.WEBSITE)
  }

  /**
   * When adding/removing admin(s), call this method
   */
  def fixEnrollments(institutionUUID: String, oldRoles: RolesTO, newRoles: RolesTO) = {
    val oldPersonUUIDs = oldRoles.getRoleTOs.asScala.map(x => x.getRole.getPersonUUID)
    val newPersonUUIDs = newRoles.getRoleTOs.asScala.map(x => x.getUsername)

    val missing = oldPersonUUIDs.filter(x => !newPersonUUIDs.contains(x))
    val added = newPersonUUIDs.filter(x => !oldPersonUUIDs.contains(x))

    val sandboxClasses = CourseClassesRepo.sandboxClassesForInstitution(institutionUUID)
    for (sandboxClass <- sandboxClasses) {
      added.foreach(personUUID => enrollAdmin(sandboxClass.getUUID, personUUID))
      missing.foreach(personUUID => manageExistingEnrollment(sandboxClass.getUUID, personUUID, EnrollmentState.deleted))
    }
  }

  /**
   * This methods wipes progress for all enrollments on the sandbox class, used when wizard course changes a lot
   */
  def resetEnrollments(courseVersionUUID: String) = {
    val courseClass = CourseClassesRepo.sandboxForVersion(courseVersionUUID).get
    if (courseClass.getState == EntityState.deleted) {
      sql"""
        update CourseClass set state = ${EntityState.active.toString} where uuid = ${courseClass.getUUID}
      """.executeUpdate
    }
    val enrollmentTOs = EnrollmentsRepo.byCourseClass(courseClass.getUUID).getEnrollmentTOs.asScala
    for (enrollmentTO <- enrollmentTOs) {
      sql"""
        update Enrollment set progress = null,
        assessment = null,
        lastProgressUpdate = null,
        lastAssessmentUpdate = null,
        assessmentScore = null,
        certifiedAt = null,
        preAssessmentScore = null,
        postAssessmentScore = null
        where uuid = ${enrollmentTO.getEnrollment.getUUID}
      """.executeUpdate
      EventsRepo.deleteActoms(enrollmentTO.getEnrollment.getUUID)
    }
  }
}
