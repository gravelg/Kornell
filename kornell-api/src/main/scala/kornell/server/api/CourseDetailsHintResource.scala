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
import kornell.server.jdbc.repository.CourseDetailsHintRepo
import kornell.core.entity.CourseDetailsHint
import javax.ws.rs.POST
import javax.ws.rs.PathParam

class CourseDetailsHintResource(uuid: String) {
  
  @GET
  @Produces(Array(CourseDetailsHint.TYPE))
  def get = {
    CourseDetailsHintRepo(uuid).get
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
   
  @PUT
  @Consumes(Array(CourseDetailsHint.TYPE))
  @Produces(Array(CourseDetailsHint.TYPE))
  def update(courseDetailsHint: CourseDetailsHint) = {
    CourseDetailsHintRepo(uuid).update(courseDetailsHint)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get

  @DELETE
  @Produces(Array(CourseDetailsHint.TYPE))
  def delete() = {
    CourseDetailsHintRepo(uuid).delete
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
}

object CourseDetailsHintResource {
  def apply(uuid: String) = new CourseDetailsHintResource(uuid)
}