package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.Track
import kornell.server.jdbc.repository.TracksRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("tracks")
class TracksResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = TrackResource(uuid)

  @POST
  @Consumes(Array(Track.TYPE))
  @Produces(Array(Track.TYPE))
  def create(track: Track): Track = {
    TracksRepo.create(track)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get
}

object TracksResource {
  def apply(uuid: String) = new TrackResource(uuid)
}
