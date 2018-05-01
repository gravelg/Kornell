package kornell.server.api

import javax.ws.rs.{GET, Path, Produces, QueryParam}
import kornell.core.entity.Person
import kornell.server.jdbc.repository.{AuthRepo, PersonRepo}
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Produces(Array(Person.TYPE))
class PersonResource(uuid: String) {

  @Path("isRegistered")
  @Produces(Array("application/boolean"))
  @GET
  def isRegistered(@QueryParam("cpf") cpf: String,
    @QueryParam("email") email: String): Boolean =
    AuthRepo().withPerson { person =>
      val result = PersonRepo(uuid).isRegistered(person.getInstitutionUUID, cpf, email)
      result
    }.requiring(PersonRepo(getAuthenticatedPersonUUID).hasPowerOver(uuid), AccessDeniedErr())
      .get
}
