package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.Track
import kornell.server.jdbc.repository.TrackRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

class TrackResource(uuid: String) {

  @GET
  @Produces(Array(Track.TYPE))
  def get: Track = {
    TrackRepo(uuid).get
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(Track.TYPE))
  @Produces(Array(Track.TYPE))
  def update(track: Track): Track = {
    TrackRepo(uuid).update(track)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(Track.TYPE))
  def delete: Track = {
    TrackRepo(uuid).delete
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get
}

object TrackResource {
  def apply(uuid: String) = new TrackResource(uuid)
}
