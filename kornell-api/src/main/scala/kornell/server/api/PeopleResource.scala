package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.PeopleRepo
import kornell.core.entity.People
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.to.PeopleTO
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.util.AccessDeniedErr

@Path("people")
@Produces(Array(People.TYPE))
class PeopleResource() {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String): PersonResource = new PersonResource(uuid)

  @GET
  @Produces(Array(PeopleTO.TYPE))
  def findBySearchTerm(@QueryParam("institutionUUID") institutionUUID: String, @QueryParam("search") search: String) = {
    PeopleRepo.findBySearchTerm(institutionUUID, search)
  }.requiring(isPlatformAdmin(institutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(institutionUUID), AccessDeniedErr()).get
}

object PeopleResource {
  def apply() = new PeopleResource()
}
