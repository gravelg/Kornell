package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.repository.Entities._
import kornell.server.repository.Entities
import kornell.core.entity.Person
import kornell.core.entity.Institution
import kornell.server.jdbc.SQL._
import kornell.server.repository.TOs
import kornell.core.entity.AuditedEntityType
import java.util.Date
import kornell.core.util.UUID
import kornell.core.entity.InstitutionType
import com.google.common.cache.CacheLoader
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit.MINUTES

object InstitutionsRepo {

  val cacheBuilder = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, MINUTES)
    .maximumSize(1000)

  /*uuid cache*/  
  val uuidLoader = new CacheLoader[String, Option[Institution]]() {
    override def load(uuid: String): Option[Institution] = lookupByUUID(uuid)
  }
  val uuidCache = cacheBuilder.build(uuidLoader)
  def getByUUID(uuid: String) = uuidCache.get(uuid)
  def lookupByUUID(UUID:String) = 
	sql"select * from Institution where uuid = ${UUID}".first[Institution]
  
	
  /*name cache*/  
  val nameLoader = new CacheLoader[String, Option[Institution]]() {
    override def load(name: String): Option[Institution] = lookupByName(name)
  }
  val nameCache = cacheBuilder.build(nameLoader)
  def getByName(name: String) = nameCache.get(name)
  def lookupByName(institutionName:String) = 
	sql"select * from Institution where name = ${institutionName}".first[Institution]
  
	
  /*hostName cache*/  
  val hostNameLoader = new CacheLoader[String, Option[Institution]]() {
    override def load(hostName: String): Option[Institution] = lookupByHostName(hostName)
  }
  val hostNameCache = cacheBuilder.build(hostNameLoader)
  def getByHostName(hostName: String) = hostNameCache.get(hostName)
  def lookupByHostName(hostName:String) =
      sql"""
      	| select i.* from Institution i 
      	| join InstitutionHostName ihn on i.uuid = ihn.institutionUUID
      	| where ihn.hostName = ${hostName}
	    """.first[Institution]

	def clearCache() = {
    hostNameCache.invalidateAll()
	  nameCache.invalidateAll()
	  uuidCache.invalidateAll()
  }
	    
  def byType(institutionType: InstitutionType) = 
    sql"""
        select * from Institution where institutionType = ${institutionType.toString}
    """.first[Institution]
  
  def create(institution: Institution): Institution = {
    if (institution.getUUID == null) {
      institution.setUUID(UUID.random)
    }
    if (institution.getActivatedAt == null) {
      institution.setActivatedAt(new Date)
    }
    sql"""
    | insert into Institution (uuid,name,terms,baseURL,demandsPersonContactDetails,validatePersonContactDetails,fullName,allowRegistration,allowRegistrationByUsername,activatedAt,skin,billingType,institutionType,dashboardVersionUUID,useEmailWhitelist,assetsRepositoryUUID,timeZone,institutionSupportEmail,advancedMode, notifyInstitutionAdmins, allowedLanguages)
    | values(
    | ${institution.getUUID},
    | ${institution.getName},
    | ${institution.getTerms},
    | ${institution.getBaseURL},
    | ${institution.isDemandsPersonContactDetails},
    | ${institution.isValidatePersonContactDetails},
    | ${institution.getFullName},
    | ${institution.isAllowRegistration},
    | ${institution.isAllowRegistrationByUsername},
    | ${institution.getActivatedAt},
    | ${institution.getSkin},
    | ${institution.getBillingType.toString},
    | ${institution.getInstitutionType.toString},
    | ${institution.getDashboardVersionUUID},
    | ${institution.isUseEmailWhitelist},
    | ${institution.getAssetsRepositoryUUID},
    | ${institution.getTimeZone},
    | ${institution.getInstitutionSupportEmail},
    | ${institution.isAdvancedMode},
    | ${institution.isNotifyInstitutionAdmins},
    | ${institution.getAllowedLanguages})""".executeUpdate
    
    //log creation event
    EventsRepo.logEntityChange(institution.getUUID, AuditedEntityType.institution, institution.getUUID, null, institution)
    
    institution
  }  

  def updateCaches(i: Institution) = {
    val oi = Some(i)
    uuidCache.put(i.getUUID, oi)
    nameCache.put(i.getName, oi)
  }

  def cleanUpHostNameCache = {
    hostNameCache.cleanUp
  }
  
  def updateHostNameCache(institutionUUID: String, hostName: String) = {
    val oi = getByUUID(institutionUUID)
    hostNameCache.put(hostName, oi)    
  }
}