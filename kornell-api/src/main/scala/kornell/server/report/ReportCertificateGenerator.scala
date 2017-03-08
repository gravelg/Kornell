package kornell.server.report

import java.io.File
import java.net.URL
import java.sql.ResultSet
import java.util.HashMap
import org.apache.commons.io.FileUtils
import kornell.core.error.exception.EntityConflictException
import kornell.core.to.report.CertificateInformationTO
import kornell.core.util.StringUtils.composeURL
import kornell.server.jdbc.PreparedStmt
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.TOs
import kornell.server.util.Settings
import kornell.server.repository.Entities._
import kornell.server.util.DateConverter
import kornell.server.authentication.ThreadLocalAuthenticator
import java.util.Date
import kornell.core.entity.CertificateDetails
import kornell.server.jdbc.repository.CertificatesDetailsRepo
import kornell.core.entity.CourseDetailsEntityType
import kornell.core.error.exception.EntityNotFoundException
import kornell.core.entity.CertificateType
import kornell.core.util.StringUtils.mkurl
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.content.ContentManagers
import java.io.ByteArrayInputStream
import kornell.core.error.exception.ServerErrorException
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.core.to.SimplePeopleTO
import java.net.HttpURLConnection
import javax.servlet.http.HttpServletResponse
import kornell.server.jdbc.repository.ContentRepositoriesRepo

object ReportCertificateGenerator {

  def newCertificateInformationTO: CertificateInformationTO = new CertificateInformationTO
  def newCertificateInformationTO(personFullName: String, personCPF: String, courseTitle: String, courseClassName: String, institutionName: String, courseClassFinishedDate: Date, assetsURL: String, distributionPrefix: String, courseVersionUUID: String, courseClassUUID: String, courseUUID: String, baseURL: String): CertificateInformationTO = {
    val dateConverter = new DateConverter(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get)
    val to = newCertificateInformationTO
    to.setPersonFullName(personFullName)
    to.setPersonCPF(personCPF)
    to.setCourseTitle(courseTitle)
    to.setCourseClassName(courseClassName)
    to.setInstitutionName(institutionName)
    to.setAssetsURL(assetsURL)
    to.setDistributionPrefix(distributionPrefix)
    to.setCourseVersionUUID(courseVersionUUID)
    to.setCourseClassUUID(courseClassUUID)
    to.setCourseUUID(courseUUID)
    to.setBaseURL(baseURL)
    to.setCourseClassFinishedDate(dateConverter.dateToInstitutionTimezone(courseClassFinishedDate))
    to
  }
  
  implicit def toCertificateInformationTO(rs: ResultSet): CertificateInformationTO =
    newCertificateInformationTO(
      rs.getString("fullName"),
      rs.getString("cpf"),
      rs.getString("title"),
      rs.getString("name"),
      rs.getString("institutionName"),
      rs.getTimestamp("certifiedAt"),
      rs.getString("assetsRepositoryUUID"),
      rs.getString("distributionPrefix"),
      rs.getString("courseVersionUUID"),
      rs.getString("courseClassUUID"),
      rs.getString("courseUUID"),
      rs.getString("baseURL"))

  def findCertificateDetails(certificateInformationTO: CertificateInformationTO): CertificateDetails  = {
    var details = CertificatesDetailsRepo.getForEntity(certificateInformationTO.getCourseClassUUID, CourseDetailsEntityType.COURSE_CLASS)
    if (!details.isDefined) {
      details = CertificatesDetailsRepo.getForEntity(certificateInformationTO.getCourseVersionUUID, CourseDetailsEntityType.COURSE_VERSION)
      if (!details.isDefined) {
        details = CertificatesDetailsRepo.getForEntity(certificateInformationTO.getCourseUUID, CourseDetailsEntityType.COURSE)
        if (!details.isDefined) {
          //build default one
          details = Option(newCertificateDetails(null, "reports/", CertificateType.NO_BG, CourseDetailsEntityType.COURSE_CLASS,  null))
        }
      }
    }
    details.get
  }
      
  def generateCertificate(userUUID: String, courseClassUUID: String, resp: HttpServletResponse): Array[Byte] = {
    resp.addHeader("Content-disposition", "attachment; filename=Certificado.pdf")
    
    val certificateData = sql"""
				select p.fullName, c.title, cc.name, i.fullName as institutionName, i.assetsRepositoryUUID, cv.distributionPrefix, p.cpf, e.certifiedAt, cv.uuid as courseVersionUUID, cc.uuid as courseClassUUID, c.uuid as courseUUID, i.baseURL
	    		from Person p
					join Enrollment e on p.uuid = e.person_uuid
					join CourseClass cc on cc.uuid = e.class_uuid
		    	join CourseVersion cv on cv.uuid = cc.courseVersion_uuid
		    	join Course c on c.uuid = cv.course_uuid
				join Institution i on i.uuid = cc.institution_uuid
				join ContentRepository s on s.uuid = i.assetsRepositoryUUID
				where e.certifiedAt is not null and 
        		  p.uuid = $userUUID and
				  cc.uuid = $courseClassUUID
		    """.map[CertificateInformationTO](toCertificateInformationTO)
    val certificateDetails = findCertificateDetails(certificateData.head)
    generateCertificateReport(certificateData, certificateDetails)
  }
  
  def generateCertificate(certificateInformationTOs: List[CertificateInformationTO]): Array[Byte] = {
    val certificateDetails = findCertificateDetails(certificateInformationTOs.head)
    generateCertificateReport(certificateInformationTOs, certificateDetails)
  }
  
  def getCertificateInformationTOsByCourseClass(courseClassUUID: String, enrollments: String) = {
    var sql = """select p.fullName, c.title, cc.name, i.fullName as institutionName, i.assetsRepositoryUUID, cv.distributionPrefix, p.cpf, e.certifiedAt, cv.uuid as courseVersionUUID, cc.uuid as courseClassUUID, c.uuid as courseUUID, i.baseURL
      from Person p 
      join Enrollment e on p.uuid = e.person_uuid 
      join CourseClass cc on cc.uuid = e.class_uuid 
      join CourseVersion cv on cv.uuid = cc.courseVersion_uuid  
      join Course c on c.uuid = cv.course_uuid  
      join Institution i on i.uuid = cc.institution_uuid 
      where e.certifiedAt is not null and  
      e.state <> 'cancelled' and """ +
		s"""cc.uuid = '$courseClassUUID' """
	  if(enrollments != null)
		  sql += s"""and e.uuid in ( $enrollments )"""
    if (sql.contains("--")) throw new EntityConflictException("invalidValue")
    val pstmt = new PreparedStmt(sql,List())    
    pstmt.map[CertificateInformationTO](toCertificateInformationTO)
  }

  private def generateCertificateReport(certificateData: List[CertificateInformationTO], certificateDetails: CertificateDetails): Array[Byte] = {
    if(certificateData.length == 0){
    	return null
    }
    val parameters: HashMap[String, Object] = new HashMap()
    
    parameters.put("institutionURL", composeURL(certificateData.head.getBaseURL, "repository", certificateData.head.getAssetsURL) + "/")
    parameters.put("assetsURL", certificateDetails.getBgImage)
	  
    val cl = Thread.currentThread.getContextClassLoader
    val stream = cl.getResourceAsStream(certificateDetails.getCertificateType.getPath)
    getReportBytesFromStream(certificateData, parameters, stream, "pdf")
  }
  
   def generateCourseClassCertificates(courseClassUUID: String, peopleTO: SimplePeopleTO) = {
      try {
        val people = peopleTO.getSimplePeopleTO
        val enrollmentUUIDs = {
          if(people != null && people.size > 0) {
            var enrollmentUUIDsVar = ""
    		    for (i <- 0 until people.size) {
    		      val person = people.get(i)
    		      val enrollmentUUID = EnrollmentsRepo.byCourseClassAndUsername(courseClassUUID, person.getUsername)
    		      if(enrollmentUUID.isDefined){
    		        if(enrollmentUUIDsVar.length != 0) enrollmentUUIDsVar += ","
    		    	  enrollmentUUIDsVar += "'" + enrollmentUUID.get + "'"
    		      }
    		    }
            enrollmentUUIDsVar
          }
          else null
        }
      
        val certificateInformationTOsByCourseClass = ReportCertificateGenerator.getCertificateInformationTOsByCourseClass(courseClassUUID, enrollmentUUIDs)
        if (certificateInformationTOsByCourseClass.length == 0) {
          throw new ServerErrorException("errorGeneratingReport")
        } else {
          val report = ReportCertificateGenerator.generateCertificate(certificateInformationTOsByCourseClass)
          val bs = new ByteArrayInputStream(report)
          val person = PersonRepo(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get).get
          val repo = ContentManagers.forRepository(ContentRepositoriesRepo.firstRepositoryByInstitution(person.getInstitutionUUID()).get.getUUID)
          val filename = ReportCertificateGenerator.getCourseClassCertificateReportFileName(courseClassUUID)

          repo.put(
            bs,
            "application/pdf",
            "Content-Disposition: attachment; filename=\"" + filename + "\"",
            Map("certificatedata" -> "09/01/1980", "requestedby" -> person.getFullName()), filename)
           
          ReportCertificateGenerator.getCourseClassCertificateReportURL(courseClassUUID)
        }
      } catch {
        case e: Exception =>
          throw new ServerErrorException("errorGeneratingReport", e)
      }
   }
   
   def courseClassCertificateExists(courseClassUUID: String) = {
    try {
      val url = getCourseClassCertificateReportURL(courseClassUUID)
      HttpURLConnection.setFollowRedirects(false);
      val con = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
      con.setRequestMethod("HEAD")
      if (con.getResponseCode() == HttpURLConnection.HTTP_OK) url else ""
    } catch {
      case e: Exception => throw new ServerErrorException("errorCheckingCerts", e)
    }
  }
  
  def getCourseClassCertificateReportFileName(courseClassUUID: String) = {
      "knl-institution/certificates/" + ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get + courseClassUUID + ".pdf"
  }
  
  def getCourseClassCertificateReportURL(courseClassUUID: String) = {
      val institutionUUID = getInstitutionUUID(courseClassUUID)
      val repo = ContentManagers.forRepository(ContentRepositoriesRepo.firstRepositoryByInstitution(institutionUUID).get.getUUID)
      val key = getCourseClassCertificateReportFileName(courseClassUUID)
      mkurl(InstitutionRepo(institutionUUID).get.getBaseURL, repo.url(key))
  }
  
}