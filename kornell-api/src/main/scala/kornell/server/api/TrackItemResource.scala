package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.TrackItem
import kornell.server.jdbc.repository.TrackItemRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

class TrackItemResource(uuid: String) {

  @GET
  @Produces(Array(TrackItem.TYPE))
  def get: TrackItem = {
    TrackItemRepo(uuid).get
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(TrackItem.TYPE))
  @Produces(Array(TrackItem.TYPE))
  def update(trackItem: TrackItem): TrackItem = {
    TrackItemRepo(uuid).update(trackItem)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(TrackItem.TYPE))
  def delete: TrackItem = {
    TrackItemRepo(uuid).delete
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get
}

object TrackItemResource {
  def apply(uuid: String) = new TrackItemResource(uuid)
}
