package kornell.server.api

import javax.servlet.http.HttpServletResponse
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import kornell.core.to.SimplePeopleTO
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.report.ReportCertificateGenerator
import kornell.server.report.ReportCourseClassAuditGenerator
import kornell.server.report.ReportCourseClassGenerator
import kornell.server.report.clearJasperFiles
import kornell.server.report.ReportInstitutionBillingGenerator
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("report")
class ReportResource {

  @GET
  @Path("certificate/{userUUID}/{courseClassUUID}")
  @Produces(Array("application/pdf"))
  def get(@Context resp: HttpServletResponse,
    @PathParam("userUUID") userUUID: String,
    @PathParam("courseClassUUID") courseClassUUID: String) = {
    ReportCertificateGenerator.generateCertificate(userUUID, courseClassUUID, resp)
  }.requiring(isPlatformAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isInstitutionAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassTutor(courseClassUUID), AccessDeniedErr())
    .or((getAuthenticatedPersonUUID == userUUID), AccessDeniedErr()).get

  @PUT
  @Path("certificate")
  @Consumes(Array(SimplePeopleTO.TYPE))
  @Produces(Array("text/plain"))
  def getCertificates(peopleTO: SimplePeopleTO,
    @QueryParam("courseClassUUID") courseClassUUID: String) = {
    ReportCertificateGenerator.generateCourseClassCertificates(courseClassUUID, peopleTO)
  }.requiring(isPlatformAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isInstitutionAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassTutor(courseClassUUID), AccessDeniedErr()).get

  @GET
  @Path("courseClassCertificateExists")
  @Produces(Array("text/plain"))
  def fileExists(@QueryParam("courseClassUUID") courseClassUUID: String) = {
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
    @QueryParam("fileType") fileType: String) = {
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
    @QueryParam("fileType") fileType: String) = {
    ReportCourseClassGenerator.courseClassInfoExists(courseUUID, courseClassUUID, fileType)
  }.requiring(isPlatformAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isInstitutionAdmin(getInstitutionUUID(courseClassUUID)), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassObserver(courseClassUUID), AccessDeniedErr())
    .or(isCourseClassTutor(courseClassUUID), AccessDeniedErr()).get

  @GET
  @Path("courseClassAudit")
  def getCourseClassAudit(@Context resp: HttpServletResponse,
    @QueryParam("courseClassUUID") courseClassUUID: String) = {
    ReportCourseClassAuditGenerator.generateCourseClassAuditReport(courseClassUUID, resp)
  }.requiring(isPlatformAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(CourseClassRepo(courseClassUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isCourseClassAdmin(courseClassUUID), AccessDeniedErr()).get

  @GET
  @Path("institutionBilling")
  def getInstitutionBilling(@Context resp: HttpServletResponse,
    @QueryParam("institutionUUID") institutionUUID: String,
    @QueryParam("periodStart") periodStart: String,
    @QueryParam("periodEnd") periodEnd: String) = {
    ReportInstitutionBillingGenerator.generateInstitutionBillingReport(institutionUUID, periodStart, periodEnd, resp)
  }.requiring(isPlatformAdmin(institutionUUID), AccessDeniedErr()).get

  @GET
  @Path("clear")
  def clear = clearJasperFiles

}
