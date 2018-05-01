package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.{CertificateDetails, CourseDetailsEntityType}
import kornell.core.error.exception.EntityNotFoundException
import kornell.server.jdbc.repository.CertificatesDetailsRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("certificatesDetails")
class CertificatesDetailsResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = CertificateDetailsResource(uuid)

  @POST
  @Consumes(Array(CertificateDetails.TYPE))
  @Produces(Array(CertificateDetails.TYPE))
  def create(certificateDetails: CertificateDetails): CertificateDetails = {
    CertificatesDetailsRepo.create(certificateDetails)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @GET
  @Path("/{entityType}/{entityUUID}")
  @Produces(Array(CertificateDetails.TYPE))
  def getByEntityTypeAndUUID(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String): CertificateDetails = {
    val certificatesDetailsRepo = CertificatesDetailsRepo.getForEntity(entityUUID, CourseDetailsEntityType.valueOf(entityType))
    certificatesDetailsRepo match {
      case Some(x) => x
      case _ => throw new EntityNotFoundException("notFound")
    }
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

}

object CertificatesDetailsResource {
  def apply(uuid: String) = new CertificateDetailsResource(uuid)
}
