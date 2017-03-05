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
import kornell.server.service.UploadService

class CourseVersionResource(uuid: String) {

  //Should be TO cause the UI needs Course
  @GET
  @Produces(Array(CourseVersionTO.TYPE))
  def get : CourseVersionTO  = {
    CourseVersionRepo(uuid).getWithCourse
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
   
  @PUT
  @Consumes(Array(CourseVersion.TYPE))
  @Produces(Array(CourseVersion.TYPE))
  def update(courseVersion: CourseVersion) = {
    CourseVersionRepo(uuid).update(courseVersion, PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
   
   @GET
   @Path("uploadUrl")
   @Produces(Array("application/octet-stream"))
   def getUploadUrl : String = {
    UploadService.getCourseVersionUploadUrl(uuid)
  }
}

object CourseVersionResource {
  def apply(uuid: String) = new CourseVersionResource(uuid)
}