package kornell.server.api

import javax.ws.rs.{DELETE, GET, Produces}
import kornell.core.entity.TrackEnrollment
import kornell.server.jdbc.repository.TrackEnrollmentRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

class TrackEnrollmentResource(uuid: String) {

  @GET
  @Produces(Array(TrackEnrollment.TYPE))
  def get: TrackEnrollment = {
    TrackEnrollmentRepo(uuid).get
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(TrackEnrollment.TYPE))
  def delete: TrackEnrollment = {
    TrackEnrollmentRepo(uuid).delete
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get
}

object TrackEnrollmentResource {
  def apply(uuid: String) = new TrackEnrollmentResource(uuid)
}
