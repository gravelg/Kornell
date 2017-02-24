package kornell.server.api

import java.io.ByteArrayInputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import kornell.core.entity.RoleCategory
import kornell.core.error.exception.ServerErrorException
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.to.SimplePeopleTO
import kornell.server.content.ContentManagers
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.RolesRepo
import kornell.server.report.ReportCertificateGenerator
import kornell.server.report.ReportCourseClassAuditGenerator
import kornell.server.report.ReportCourseClassGenerator
import kornell.server.report.ReportGenerator
import kornell.server.report.ReportInstitutionBillingGenerator
import javax.servlet.http.HttpServletRequest
import kornell.core.util.StringUtils.mkurl
import kornell.server.util.Conditional.toConditional
import kornell.server.util.AccessDeniedErr


@Path("/report")
class ReportResource {

  @GET
  @Path("/certificate/{userUUID}/{courseClassUUID}")
  @Produces(Array("application/pdf"))
  def get(@Context resp: HttpServletResponse,
    @Context req: HttpServletRequest,
    @PathParam("userUUID") userUUID: String,
    @PathParam("courseClassUUID") courseClassUUID: String) = AuthRepo().withPerson{ person => {
	    val roles = RolesRepo.getUserRoles(person.getUUID, RoleCategory.BIND_DEFAULT).getRoleTOs
	    if (!(RoleCategory.isPlatformAdmin(roles, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID) ||
	      RoleCategory.isInstitutionAdmin(roles, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID) ||
	      RoleCategory.isCourseClassAdmin(roles, courseClassUUID) ||
	      RoleCategory.isCourseClassObserver(roles, courseClassUUID) ||
	      RoleCategory.isCourseClassTutor(roles, courseClassUUID) ||
	      person.getUUID == userUUID))
	      throw new UnauthorizedAccessException("accessDenied")
	    resp.addHeader("Content-disposition", "attachment; filename=Certificado.pdf")
	    ReportCertificateGenerator.generateCertificate(userUUID, courseClassUUID)
	  }
  }

  @PUT
  @Path("/certificate")
  @Consumes(Array(SimplePeopleTO.TYPE))
  def get(@Context req: HttpServletRequest,
   @QueryParam("courseClassUUID") courseClassUUID: String, 
    peopleTO: SimplePeopleTO) = AuthRepo().withPerson { p =>
    val courseClass = CourseClassesRepo(courseClassUUID).get
	  val roles = RolesRepo.getUserRoles(p.getUUID, RoleCategory.BIND_DEFAULT).getRoleTOs
    if (!(RoleCategory.isPlatformAdmin(roles, courseClass.getInstitutionUUID) ||
      RoleCategory.isInstitutionAdmin(roles, courseClass.getInstitutionUUID) ||
      RoleCategory.isCourseClassAdmin(roles, courseClass.getUUID) ||
      RoleCategory.isCourseClassObserver(roles, courseClass.getUUID) ||
      RoleCategory.isCourseClassTutor(roles, courseClass.getUUID)))
    	throw new UnauthorizedAccessException("unauthorizedAccessReport")
    else {
      try {
        var filename = p.getUUID + courseClassUUID + ".pdf"
        //S3.certificates.delete(filename)
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
          val repo = ContentManagers.forCertificates(p.getInstitutionUUID())
          repo.put(
            bs,
            "application/pdf",
            "Content-Disposition: attachment; filename=\"" + filename + ".pdf\"",
            Map("certificatedata" -> "09/01/1980", "requestedby" -> p.getFullName()),filename)
           mkurl(ContentManagers.USER_CONTENT_URL, repo.url(filename)) 
        }
      } catch {
        case e: Exception =>
          throw new ServerErrorException("errorGeneratingReport", e)
      }
    }
  }

  @GET
  @Path("courseClassCertificateExists")
  def fileExists(@QueryParam("courseClassUUID") courseClassUUID: String) = AuthRepo().withPerson { p =>
    try {
      var filename = p.getUUID + courseClassUUID + ".pdf"
      
      val url = mkurl(ContentManagers.USER_CONTENT_URL, ContentManagers.forCertificates(p.getInstitutionUUID).url(filename)) 

      HttpURLConnection.setFollowRedirects(false);
      val con = new URL(url).openConnection.asInstanceOf[HttpURLConnection]
      con.setRequestMethod("HEAD")
      if (con.getResponseCode() == HttpURLConnection.HTTP_OK)
        url
      else
        ""
    } catch {
      case e: Exception => throw new ServerErrorException("errorCheckingCerts", e)
    }
  }

  @GET
  @Path("/courseClassInfo")
  def getCourseClassInfo(@Context resp: HttpServletResponse,
    @Context req: HttpServletRequest,
    @QueryParam("courseUUID") courseUUID: String,
    @QueryParam("courseClassUUID") courseClassUUID: String,
    @QueryParam("fileType") fileType: String) =  {
	  if(courseUUID != null || courseClassUUID != null){
	    val fType = {
	      if(fileType != null && fileType == "xls")
	        "xls"
	      else
	        "pdf"
	    }
	    val fileName = { 
	    	if(courseUUID != null)
	    	  CourseRepo(courseUUID).get.getTitle
	    	else
	    	  CourseClassRepo(courseClassUUID).get.getName
	    } + " - " + new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())+ "."+fType
	    
	    resp.addHeader("Content-disposition", "attachment; filename=" + fileName)
	    if(fType != null && fType == "xls")
	    	resp.setContentType("application/vnd.ms-excel")
	    else
	    	resp.setContentType("application/pdf")
	    ReportCourseClassGenerator.generateCourseClassReport(courseUUID, courseClassUUID, fType)
	  }
  }.requiring(isPlatformAdmin(getInstitutionUUID(courseUUID, courseClassUUID)), AccessDeniedErr())
     .or(isInstitutionAdmin(getInstitutionUUID(courseUUID, courseClassUUID)), AccessDeniedErr())
     .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
     .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr()).get
     
  def getInstitutionUUID(courseUUID: String, courseClassUUID: String) = {
    if(courseUUID != null){
      CourseRepo(courseUUID).get.getInstitutionUUID
    } else {
      CourseClassRepo(courseClassUUID).get.getInstitutionUUID
    }
  }

  @GET
  @Path("/courseClassAudit")
  def getCourseClassAudit(@Context resp: HttpServletResponse,
    @QueryParam("courseClassUUID") courseClassUUID: String) = {
	  if(courseClassUUID != null){
	    val courseClass = CourseClassRepo(courseClassUUID).get
	    val fileName = courseClass.getName + " - Audit - " + new SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date())+ ".xls"
	    resp.addHeader("Content-disposition", "attachment; filename=" + fileName)
	    resp.setContentType("application/vnd.ms-excel")
	    ReportCourseClassAuditGenerator.generateCourseClassAuditReport(courseClass)
	  }
  }.requiring(isPlatformAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
     .or(isInstitutionAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
  .   or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr()).get

  @GET
  @Path("/institutionBilling")
  def getInstitutionBilling(@Context resp: HttpServletResponse,
    @QueryParam("institutionUUID") institutionUUID: String,
    @QueryParam("periodStart") periodStart: String,
    @QueryParam("periodEnd") periodEnd: String) = AuthRepo().withPerson{ person => {
	    val roles = RolesRepo.getUserRoles(person.getUUID, RoleCategory.BIND_DEFAULT).getRoleTOs
	    if (!(RoleCategory.isPlatformAdmin(roles, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID) ||
	      RoleCategory.isInstitutionAdmin(roles, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID)))
	      throw new UnauthorizedAccessException("accessDenied")
	    val institution = InstitutionRepo(institutionUUID).get
	    resp.addHeader("Content-disposition", "attachment; filename=" + institution.getName + " - " + periodStart + ".xls")
	    resp.setContentType("application/vnd.ms-excel")
	    ReportInstitutionBillingGenerator.generateInstitutionBillingReport(institution, periodStart, periodEnd)
    }
  }

  @GET
  @Path("/clear")
  @Produces(Array("application/pdf"))
  def clearJasperFiles = ReportGenerator.clearJasperFiles
  
}
