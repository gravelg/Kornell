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
import kornell.core.entity.CourseDetailsLibrary
import kornell.server.jdbc.repository.CourseDetailsLibraryRepo

class CourseDetailsLibraryResource(uuid: String) {

  @GET
  @Produces(Array(CourseDetailsLibrary.TYPE))
  def get = {
    CourseDetailsLibraryRepo(uuid).get
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(CourseDetailsLibrary.TYPE))
  @Produces(Array(CourseDetailsLibrary.TYPE))
  def update(courseDetailsLibrary: CourseDetailsLibrary) = {
    CourseDetailsLibraryRepo(uuid).update(courseDetailsLibrary)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(CourseDetailsLibrary.TYPE))
  def delete() = {
    CourseDetailsLibraryRepo(uuid).delete
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get
}

object CourseDetailsLibraryResource {
  def apply(uuid: String) = new CourseDetailsLibraryResource(uuid)
}
