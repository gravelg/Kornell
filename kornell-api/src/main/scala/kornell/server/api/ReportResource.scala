package kornell.server.api

import javax.servlet.http.HttpServletResponse
import javax.ws.rs._
import javax.ws.rs.core.Context
import kornell.core.to.SimplePeopleTO
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.report._
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("report")
class ReportResource {

  @GET
  @Path("certificate/{userUUID}/{courseClassUUID}")
  @Produces(Array("application/pdf"))
  def get(@Context resp: HttpServletResponse,
    @PathParam("userUUID") userUUID: String,
    @PathParam("courseClassUUID") courseClassUUID: String): Array[Byte] = {
    ReportCertificateGenerator.generateCertificate(userUUID, courseClassUUID, resp)
  }.requiring(isPlatformAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isInstitutionAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassTutor(courseClassUUID), AccessDeniedErr())
    .or(getAuthenticatedPersonUUID == userUUID, AccessDeniedErr()).get

  @PUT
  @Path("certificate")
  @Consumes(Array(SimplePeopleTO.TYPE))
  @Produces(Array("text/plain"))
  def getCertificates(peopleTO: SimplePeopleTO,
    @QueryParam("courseClassUUID") courseClassUUID: String): String = {
    ReportCertificateGenerator.generateCourseClassCertificates(courseClassUUID, peopleTO)
  }.requiring(isPlatformAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isInstitutionAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassTutor(courseClassUUID), AccessDeniedErr()).get

  @GET
  @Path("courseClassCertificateExists")
  @Produces(Array("text/plain"))
  def fileExists(@QueryParam("courseClassUUID") courseClassUUID: String): String = {
    ReportCertificateGenerator.courseClassCertificateExists(courseClassUUID)
  }.requiring(isPlatformAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isInstitutionAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassTutor(courseClassUUID), AccessDeniedErr()).get

  @PUT
  @Path("courseClassInfo")
  @Consumes(Array(SimplePeopleTO.TYPE))
  @Produces(Array("text/plain"))
  def getCourseClassInfo(peopleTO: SimplePeopleTO,
    @QueryParam("courseUUID") courseUUID: String,
    @QueryParam("courseClassUUID") courseClassUUID: String,
    @QueryParam("fileType") fileType: String): String = {
    ReportCourseClassGenerator.generateCourseClassReport(courseUUID, courseClassUUID, fileType, peopleTO)
  }.requiring(isPlatformAdmin(getInstitutionUUID(courseClassUUID, courseUUID)), AccessDeniedErr())
    .or(isInstitutionAdmin(getInstitutionUUID(courseClassUUID, courseUUID)), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr()).get

  @GET
  @Path("courseClassInfoExists")
  @Produces(Array("text/plain"))
  def fileExists(@QueryParam("courseUUID") courseUUID: String,
    @QueryParam("courseClassUUID") courseClassUUID: String,
    @QueryParam("fileType") fileType: String): String = {
    ReportCourseClassGenerator.courseClassInfoExists(courseUUID, courseClassUUID, fileType)
  }.requiring(isPlatformAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isInstitutionAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassTutor(courseClassUUID), AccessDeniedErr()).get

  @GET
  @Path("courseClassAudit")
  def getCourseClassAudit(@Context resp: HttpServletResponse,
    @QueryParam("courseClassUUID") courseClassUUID: String): Array[Byte] = {
    ReportCourseClassAuditGenerator.generateCourseClassAuditReport(courseClassUUID, resp)
  }.requiring(isPlatformAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr()).get

  @GET
  @Path("institutionBilling")
  def getInstitutionBilling(@Context resp: HttpServletResponse,
    @QueryParam("institutionUUID") institutionUUID: String,
    @QueryParam("periodStart") periodStart: String,
    @QueryParam("periodEnd") periodEnd: String): Array[Byte] = {
    ReportInstitutionBillingGenerator.generateInstitutionBillingReport(institutionUUID, periodStart, periodEnd, resp)
  }.requiring(isPlatformAdmin(institutionUUID), AccessDeniedErr()).get

  @GET
  @Path("clear")
  def clear: String = clearJasperFiles

}
