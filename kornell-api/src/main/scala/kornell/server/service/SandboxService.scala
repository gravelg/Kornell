package kornell.server.service

import java.math.BigDecimal
import java.util.Date

import kornell.core.entity._
import kornell.core.entity.role.RoleCategory
import kornell.core.error.exception.EntityNotFoundException
import kornell.core.to.RolesTO
import kornell.core.util.UUID
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.{CourseClassesRepo, CourseRepo, CourseVersionRepo, CourseVersionsRepo, EnrollmentRepo, EnrollmentsRepo, EventsRepo, InstitutionRepo, RolesRepo}
import kornell.server.repository.Entities

import scala.collection.JavaConverters._

object SandboxService {

  def processInstitution(institutionUUID: String): Unit = {
    val versions = CourseVersionsRepo.allByInstitution(institutionUUID)
    versions.foreach(courseVersion => processVersion(courseVersion.getUUID, institutionUUID))
  }

  def processVersion(courseVersionUUID: String, institutionUUID: String): Unit = {
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
          maxEnrollments = 1000,
          createdAt = new Date(),
          createdBy = creator,
          state = EntityState.active,
          registrationType = RegistrationType.email,
          institutionRegistrationPrefixUUID = null,
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

  def manageExistingEnrollment(sandboxClassUUID: String, personUUID: String, state: EnrollmentState): Unit = {
    val enrollment = EnrollmentsRepo.byCourseClassAndPerson(sandboxClassUUID, personUUID, getDeleted = false).get
    EventsRepo.logEnrollmentStateChanged(UUID.random, ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get, enrollment.getUUID, EnrollmentState.enrolled, state, sendEmail = false, "Sandbox class enrollment: " + state.toString)
    EventsRepo.deleteActoms(enrollment.getUUID)
  }

  def enrollAdmins(sandboxClassUUID: String, institutionUUID: String): Unit = {
    val rolesTO = new RolesRepo().getAllUsersWithRoleForInstitution(institutionUUID, RoleCategory.BIND_DEFAULT)
    for (roleTO <- rolesTO.getRoleTOs.asScala) {
      val enrollment = EnrollmentsRepo.byCourseClassAndPerson(sandboxClassUUID, roleTO.getRole.getPersonUUID, getDeleted = false)
      if (enrollment.isEmpty) {
        enrollAdmin(sandboxClassUUID, roleTO.getRole.getPersonUUID)
      } else if (!EnrollmentState.enrolled.equals(enrollment.get.getState)) {
        val e = enrollment.get
        e.setState(EnrollmentState.enrolled)
        EnrollmentRepo(e.getUUID).update(e)
      }
    }
  }

  def enrollAdmin(sandboxClassUUID: String, personUUID: String): Enrollment = {
    val enrollment = EnrollmentsRepo.create(
      courseClassUUID = sandboxClassUUID,
      personUUID = personUUID,
      enrollmentState = EnrollmentState.enrolled,
      courseVersionUUID = null,
      parentEnrollmentUUID = null,
      enrollmentSource = EnrollmentSource.WEBSITE)
    enrollment
  }

  /**
   * When adding/removing admin(s), call this method
   */
  def fixEnrollments(institutionUUID: String, oldRoles: RolesTO, newRoles: RolesTO): Unit = {
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
  def resetEnrollments(courseVersionUUID: String, institutionUUID: String): Unit = {
    // make sure the version is processed
    processVersion(courseVersionUUID: String, institutionUUID: String)

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

  def getEnrollment(courseVersionUUID: String): String = {
    if (CourseVersionRepo(courseVersionUUID).get.getParentVersionUUID != null) {
      throw new EntityNotFoundException("notFound")
    }

    val sandboxClass = CourseClassesRepo.sandboxForVersion(courseVersionUUID).get
    val enrollment = EnrollmentsRepo.byCourseClassAndPerson(sandboxClass.getUUID, ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get, false)

    if (enrollment.isDefined) {
      enrollment.get.getUUID
    } else {
      val enrollment = enrollAdmin(sandboxClass.getUUID, ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get)
      enrollment.getUUID
    }
  }
}
