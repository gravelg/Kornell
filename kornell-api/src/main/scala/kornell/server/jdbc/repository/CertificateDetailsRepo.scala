package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.jdbc.SQL._ 
import kornell.core.entity.CertificateDetails
import kornell.core.entity.CourseDetailsEntityType
import kornell.core.util.StringUtils
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.content.ContentManagers
import java.util.logging.Logger

class CertificateDetailsRepo(uuid: String) {
  val logger = Logger.getLogger("kornell.server.jdbc.repository.CertificateDetailsRepo")
  
  
  val finder = sql"select * from CertificateDetails where uuid=$uuid"

  def get = finder.get[CertificateDetails]
  def first = finder.first[CertificateDetails]
  
  def update(certificateDetails: CertificateDetails): CertificateDetails = {    
    sql"""
    | update CertificateDetails c
    | set c.bgImage = ${certificateDetails.getBgImage},
    | c.certificateType = ${certificateDetails.getCertificateType.toString}
    | where c.uuid = ${uuid}""".executeUpdate
    
    certificateDetails
  }
  
  def delete = {    
    val certificateDetails = get
    sql"""
      delete from CertificateDetails 
      where uuid = ${uuid}""".executeUpdate
      
    val person = PersonRepo(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get).get
    val repo = ContentRepositoriesRepo.firstRepositoryByInstitution(person.getInstitutionUUID).get
    val cm = ContentManagers.forRepository(repo.getUUID)
      
    val url = StringUtils.mkurl("repository", 
        repo.getUUID,
        "knl-institution",
        "course",
        certificateDetails.getEntityUUID,
        "certificate-bg.jpg")
    cm.delete(url)
        
    certificateDetails
  }
}

object CertificateDetailsRepo {
  def apply(uuid: String) = new CertificateDetailsRepo(uuid)
}