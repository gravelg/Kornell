package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.CertificateDetails
import kornell.server.jdbc.repository.CertificateDetailsRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

class CertificateDetailsResource(uuid: String) {

  @GET
  @Produces(Array(CertificateDetails.TYPE))
  def get: CertificateDetails = {
    CertificateDetailsRepo(uuid).get
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(CertificateDetails.TYPE))
  @Produces(Array(CertificateDetails.TYPE))
  def update(certificateDetails: CertificateDetails): CertificateDetails = {
    CertificateDetailsRepo(uuid).update(certificateDetails)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(CertificateDetails.TYPE))
  def delete(): CertificateDetails = {
    CertificateDetailsRepo(uuid).delete
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get
}

object CertificateDetailsResource {
  def apply(uuid: String) = new CertificateDetailsResource(uuid)
}
