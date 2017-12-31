package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import javax.ws.rs.Consumes
import javax.ws.rs.Produces
import kornell.core.entity.TrackEnrollment
import kornell.server.jdbc.repository.TrackEnrollmentsRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("trackEnrollments")
class TrackEnrollmentsResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = TrackEnrollmentResource(uuid)

  @POST
  @Consumes(Array(TrackEnrollment.TYPE))
  @Produces(Array(TrackEnrollment.TYPE))
  def create(trackEnrollment: TrackEnrollment) = {
    TrackEnrollmentsRepo.create(trackEnrollment)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get
}

object TrackEnrollmentsResource {
  def apply(uuid: String) = new TrackEnrollmentResource(uuid)
}
