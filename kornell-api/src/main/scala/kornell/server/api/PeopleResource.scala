package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.People
import kornell.core.to.PeopleTO
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("people")
@Produces(Array(People.TYPE))
class PeopleResource() {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String): PersonResource = new PersonResource(uuid)

  @GET
  @Produces(Array(PeopleTO.TYPE))
  def findBySearchTerm(@QueryParam("institutionUUID") institutionUUID: String, @QueryParam("search") search: String): PeopleTO = {
    PeopleRepo.findBySearchTerm(institutionUUID, search)
  }.requiring(isPlatformAdmin(institutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(institutionUUID), AccessDeniedErr()).get
}

object PeopleResource {
  def apply() = new PeopleResource()
}
