package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import kornell.core.entity.CourseVersion
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.PersonRepo
import javax.ws.rs.PUT
import javax.ws.rs.Consumes
import javax.ws.rs.DELETE
import kornell.core.to.CourseVersionTO
import kornell.server.util.AccessDeniedErr
import kornell.core.to.CourseVersionUploadTO
import javax.ws.rs.Path
import kornell.server.service.S3Service
import javax.ws.rs.PathParam
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.AuthRepo
import javax.ws.rs.POST
import kornell.server.service.SandboxService
import javax.ws.rs.core.Response

class CourseVersionResource(uuid: String) {

  @GET
  @Produces(Array(CourseVersionTO.TYPE))
  def get = {
    CourseVersionsRepo.getCourseVersionTO(getAutenticatedPersonInstitutionUUID, uuid)
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(CourseVersion.TYPE))
  @Produces(Array(CourseVersion.TYPE))
  def update(courseVersion: CourseVersion, @QueryParam("skipAudit") skipAudit: Boolean) = {
    CourseVersionRepo(uuid).update(courseVersion, skipAudit, getAutenticatedPersonInstitutionUUID)
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(CourseVersion.TYPE))
  def delete() = {
    CourseVersionRepo(uuid).delete
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get

  @POST
  @Path("copy")
  @Produces(Array(CourseVersion.TYPE))
  def copy = {
    CourseVersionRepo(uuid).copy
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get

  @GET
  @Path("uploadUrl")
  @Produces(Array("text/plain"))
  def getUploadUrl(@QueryParam("filename") filename: String, @QueryParam("path") path: String): String = {
    S3Service.getCourseVersionUploadUrl(uuid, filename, path)
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get

  @GET
  @Path("contentUploadUrl")
  @Produces(Array("text/plain"))
  def getUploadUrl: String = {
    S3Service.getCourseVersionContentUploadUrl(uuid)
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get

  @PUT
  @Path("resetSandbox")
  def resetSandbox = {
    SandboxService.resetEnrollments(uuid)
    Response.noContent.build
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get
}

object CourseVersionResource {
  def apply(uuid: String) = new CourseVersionResource(uuid)
}
