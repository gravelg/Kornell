package kornell.server.api

import kornell.core.entity.Track
import javax.ws.rs.GET
import javax.ws.rs.Produces
import kornell.server.jdbc.repository.TrackRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional
import javax.ws.rs.DELETE
import javax.ws.rs.PUT
import javax.ws.rs.Consumes

class TrackResource(uuid: String) {

  @GET
  @Produces(Array(Track.TYPE))
  def get: Track = {
    TrackRepo(uuid).get
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(Track.TYPE))
  @Produces(Array(Track.TYPE))
  def update(track: Track): Track = {
    TrackRepo(uuid).update(track)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(Track.TYPE))
  def delete: Track = {
    TrackRepo(uuid).delete
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get
}

object TrackResource {
  def apply(uuid: String) = new TrackResource(uuid)
}
