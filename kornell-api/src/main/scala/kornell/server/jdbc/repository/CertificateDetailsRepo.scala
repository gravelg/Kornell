package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.jdbc.SQL._ 
import kornell.core.entity.CertificateDetails

class CertificateDetailsRepo(uuid: String) {
  
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
}

object CertificateDetailsRepo {
  def apply(uuid: String) = new CertificateDetailsRepo(uuid)
}