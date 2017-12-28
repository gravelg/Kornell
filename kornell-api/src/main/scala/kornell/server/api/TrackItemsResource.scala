package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import kornell.core.entity.TrackItem
import kornell.server.jdbc.repository.TrackItemsRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("trackItems")
class TrackItemsResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = TrackItemResource(uuid)

  @POST
  @Consumes(Array(TrackItem.TYPE))
  @Produces(Array(TrackItem.TYPE))
  def create(trackItem: TrackItem): TrackItem = {
    TrackItemsRepo.create(trackItem)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get
}

object TrackItemsResource {
  def apply(uuid: String) = new TrackItemResource(uuid)
}
