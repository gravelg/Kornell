package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.TrackItem
import kornell.server.jdbc.repository.TrackItemsRepo
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
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get
}

object TrackItemsResource {
  def apply(uuid: String) = new TrackItemResource(uuid)
}
