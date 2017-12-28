package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import kornell.core.entity.TrackItem
import kornell.server.jdbc.repository.TrackItemRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE

class TrackItemResource(uuid: String) {

  @GET
  @Produces(Array(TrackItem.TYPE))
  def get: TrackItem = {
    TrackItemRepo(uuid).get
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(TrackItem.TYPE))
  @Produces(Array(TrackItem.TYPE))
  def update(trackItem: TrackItem): TrackItem = {
    TrackItemRepo(uuid).update(trackItem)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(TrackItem.TYPE))
  def delete: TrackItem = {
    TrackItemRepo(uuid).delete
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get
}

object TrackItemResource {
  def apply(uuid: String) = new TrackItemResource(uuid)
}
