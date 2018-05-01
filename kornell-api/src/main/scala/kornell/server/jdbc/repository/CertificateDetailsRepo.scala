package kornell.server.jdbc.repository

import java.util.logging.Logger

import kornell.core.entity.CertificateDetails
import kornell.core.util.StringUtils.mkurl
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.content.ContentManagers
import kornell.server.jdbc.SQL._

class CertificateDetailsRepo(uuid: String) {
  val logger: Logger = Logger.getLogger("kornell.server.jdbc.repository.CertificateDetailsRepo")

  val finder = sql"select * from CertificateDetails where uuid=$uuid"

  def get: CertificateDetails = finder.get[CertificateDetails]
  def first: Option[CertificateDetails] = finder.first[CertificateDetails]

  def update(certificateDetails: CertificateDetails): CertificateDetails = {
    sql"""
    | update CertificateDetails c
    | set c.bgImage = ${certificateDetails.getBgImage},
    | c.certificateType = ${certificateDetails.getCertificateType.toString}
    | where c.uuid = ${uuid}""".executeUpdate

    certificateDetails
  }

  def delete: CertificateDetails = {
    val certificateDetails = get
    sql"""
      delete from CertificateDetails
      where uuid = ${uuid}""".executeUpdate

    val person = PersonRepo(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get).get
    val repo = ContentRepositoriesRepo.firstRepositoryByInstitution(person.getInstitutionUUID).get
    val cm = ContentManagers.forRepository(repo.getUUID)

    val url = mkurl(certificateDetails.getBgImage, "certificate-bg.jpg")
    cm.delete(url)

    certificateDetails
  }
}

object CertificateDetailsRepo {
  def apply(uuid: String) = new CertificateDetailsRepo(uuid)
}
