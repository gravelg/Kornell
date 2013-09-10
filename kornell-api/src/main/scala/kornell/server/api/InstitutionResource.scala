package kornell.server.api
import kornell.core.shared.data.Institution
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.server.repository.jdbc.Institutions
import kornell.server.repository.jdbc.Auth
import kornell.server.repository.jdbc.SQLInterpolation._ 


@Produces(Array(Institution.TYPE))
class InstitutionResource(institutionUUID: String) extends Resource {
  @GET
  def get = Institutions.byUUID(institutionUUID)

  @PUT
  @Produces(Array("text/plain"))
  def acceptTerms(implicit @Context sc: SecurityContext) =
    Auth.withPerson { person =>
      sql"""update Registration
      	 set termsAcceptedOn=now()
      	 where person_uuid=${person.getUUID}
      	   and institution_uuid=$institutionUUID
      	   """.executeUpdate
    }

}