package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import kornell.core.entity.CertificateDetails
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.CertificateDetailsRepo
import kornell.server.util.Conditional.toConditional
import javax.ws.rs.DELETE

class CertificateDetailsResource(uuid: String) {

  @GET
  @Produces(Array(CertificateDetails.TYPE))
  def get = {
    CertificateDetailsRepo(uuid).get
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(CertificateDetails.TYPE))
  @Produces(Array(CertificateDetails.TYPE))
  def update(certificateDetails: CertificateDetails) = {
    CertificateDetailsRepo(uuid).update(certificateDetails)
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(CertificateDetails.TYPE))
  def delete() = {
    CertificateDetailsRepo(uuid).delete
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get
}

object CertificateDetailsResource {
  def apply(uuid: String) = new CertificateDetailsResource(uuid)
}
