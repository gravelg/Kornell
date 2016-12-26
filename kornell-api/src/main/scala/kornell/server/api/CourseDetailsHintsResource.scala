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
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import kornell.server.jdbc.repository.CourseDetailsHintsRepo

@Path("courseDetailsHints")
class CourseDetailsHintsResource {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = CourseDetailsHintResource(uuid)
   
  @POST
  @Consumes(Array(CourseDetailsHint.TYPE))
  @Produces(Array(CourseDetailsHint.TYPE))
  def create(courseDetailsHint: CourseDetailsHint) = {
    CourseDetailsHintsRepo.create(courseDetailsHint)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
}

object CourseDetailsHintsResource {
  def apply(uuid: String) = new CourseDetailsHintResource(uuid)
}