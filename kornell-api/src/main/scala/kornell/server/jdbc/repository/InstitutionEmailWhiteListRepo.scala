package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import java.sql.ResultSet
import kornell.core.util.UUID
import kornell.server.repository.TOs
import scala.collection.JavaConverters._
import kornell.core.to.InstitutionEmailWhitelistTO
import kornell.core.entity.AuditedEntityType

class InstitutionEmailWhitelistRepo(institutionUUID: String) {

  def get = {
    TOs.newInstitutionEmailWhitelistTO(
      sql"""
        | select domain from InstitutionEmailWhitelist
        | where institutionUUID = ${institutionUUID}
        | order by domain"""
        .map[String])
  }

  def updateDomains(domains: InstitutionEmailWhitelistTO) = {
    val from = get

    removeDomains(institutionUUID)
    domains.getDomains.asScala.foreach(domain => addDomain(domain))

    //log entity change
    EventsRepo.logEntityChange(institutionUUID, AuditedEntityType.institutionEmailWhitelist, institutionUUID, from, domains)
    domains
  }

  def removeDomains(institutionUUID: String) = {
    sql"""delete from InstitutionEmailWhitelist where institutionUUID = ${institutionUUID}""".executeUpdate
    this
  }

  def addDomain(domain: String) = {
    sql"""insert into InstitutionEmailWhitelist (uuid, domain, institutionUUID) values
    (${UUID.random},
    ${domain},
    ${institutionUUID})""".executeUpdate
  }
}

object InstitutionEmailWhitelistRepo {
  def apply(institutionUUID: String) = new InstitutionEmailWhitelistRepo(institutionUUID)
}
