package kornell.server

import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.entity.role.RoleCategory
import scala.collection.JavaConverters._
import java.util.logging.Logger
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.jdbc.repository.RolesRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.PersonRepo

package object api {
  val logger = Logger.getLogger("kornell.server.api")

  def isControlPanelAdmin(): Boolean = RoleCategory.isControlPanelAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs)

  def isPlatformAdmin(institutionUUID: String): Boolean = RoleCategory.isPlatformAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, institutionUUID)

  def isPlatformAdmin(): Boolean = RoleCategory.isPlatformAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID)

  def isInstitutionAdmin(institutionUUID: String): Boolean =
    RoleCategory.isInstitutionAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, institutionUUID)

  def isInstitutionAdmin(): Boolean =
    RoleCategory.isInstitutionAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID)

  def isCourseClassAdmin(courseClassUUID: String): Boolean =
    RoleCategory.isCourseClassAdmin(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, courseClassUUID)

  def isCourseClassTutor(courseClassUUID: String): Boolean =
    RoleCategory.isCourseClassTutor(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, courseClassUUID)

  def isCourseClassObserver(courseClassUUID: String): Boolean =
    RoleCategory.isCourseClassObserver(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, courseClassUUID)

  def isPublisher(institutionUUID: String): Boolean =
    RoleCategory.isPublisher(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, institutionUUID)

  def isPublisher(): Boolean =
    RoleCategory.isPublisher(RolesRepo.getUserRoles(getAuthenticatedPersonUUID, RoleCategory.BIND_DEFAULT).getRoleTOs, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID)

  def getAuthenticatedPersonUUID = ThreadLocalAuthenticator.getAuthenticatedPersonUUID.getOrElse(null)

  def getInstitutionUUID(courseClassUUID: String, courseUUID: String = null) = if (courseUUID != null) CourseRepo(courseUUID).get.getInstitutionUUID else CourseClassRepo(courseClassUUID).get.getInstitutionUUID

}
