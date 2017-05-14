package kornell.server.api

import scala.collection.JavaConverters._
import javax.ws.rs._
import javax.ws.rs.core._
import kornell.core.lom._
import kornell.core.to._
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.core.entity.Course
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.service.CourseCreationService

@Path("courses")
class CoursesResource {
  
  @Path("{uuid}")
  def getCourse(@PathParam("uuid") uuid:String) = CourseResource(uuid)
  
  @GET
  @Produces(Array(CoursesTO.TYPE))
  def getCourses(@QueryParam("fetchChildCourses") fetchChildCourses: String, @QueryParam("searchTerm") searchTerm: String,
      @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int) = {
    CoursesRepo.byInstitution(fetchChildCourses == "true", PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID, searchTerm, pageSize, pageNumber)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
  
  @POST
  @Produces(Array(CourseClassTO.TYPE))
  @Consumes(Array(Course.TYPE))
  def create(course: Course) = {
    CourseCreationService.simpleCreation(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID, course)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
}

object CoursesResource {
  def apply() = new CoursesResource();
}