package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.TrackEnrollment
import kornell.server.jdbc.repository.TrackEnrollmentsRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("trackEnrollments")
class TrackEnrollmentsResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = TrackEnrollmentResource(uuid)

  @POST
  @Consumes(Array(TrackEnrollment.TYPE))
  @Produces(Array(TrackEnrollment.TYPE))
  def create(trackEnrollment: TrackEnrollment): TrackEnrollment = {
    TrackEnrollmentsRepo.create(trackEnrollment)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get
}

object TrackEnrollmentsResource {
  def apply(uuid: String) = new TrackEnrollmentResource(uuid)
}
