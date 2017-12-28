package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import kornell.core.entity.TrackEnrollment
import javax.ws.rs.DELETE
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.TrackEnrollmentRepo
import kornell.server.util.Conditional.toConditional

class TrackEnrollmentResource(uuid: String) {

  @GET
  @Produces(Array(TrackEnrollment.TYPE))
  def get: TrackEnrollment = {
    TrackEnrollmentRepo(uuid).get
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(TrackEnrollment.TYPE))
  def delete: TrackEnrollment = {
    TrackEnrollmentRepo(uuid).delete
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get
}

object TrackEnrollmentResource {
  def apply(uuid: String) = new TrackEnrollmentResource(uuid)
}
