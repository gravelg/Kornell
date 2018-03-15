package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.PersonRepo
import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.DELETE
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.CourseDetailsSectionRepo
import kornell.core.entity.CourseDetailsSection

class CourseDetailsSectionResource(uuid: String) {

  @GET
  @Produces(Array(CourseDetailsSection.TYPE))
  def get = {
    CourseDetailsSectionRepo(uuid).get
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isPublisher(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(CourseDetailsSection.TYPE))
  @Produces(Array(CourseDetailsSection.TYPE))
  def update(courseDetailsSection: CourseDetailsSection) = {
    CourseDetailsSectionRepo(uuid).update(courseDetailsSection)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isPublisher(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(CourseDetailsSection.TYPE))
  def delete() = {
    CourseDetailsSectionRepo(uuid).delete
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isPublisher(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

}

object CourseDetailsSectionResource {
  def apply(uuid: String) = new CourseDetailsSectionResource(uuid)
}
