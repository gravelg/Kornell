package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.Course
import kornell.core.to._
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.service.CourseCreationService
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("courses")
class CoursesResource {

  @Path("{uuid}")
  def getCourse(@PathParam("uuid") uuid: String) = CourseResource(uuid)

  @GET
  @Produces(Array(CoursesTO.TYPE))
  def getCourses(@QueryParam("fetchChildCourses") fetchChildCourses: String, @QueryParam("searchTerm") searchTerm: String,
    @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int, @QueryParam("orderBy") orderBy: String, @QueryParam("asc") asc: String): CoursesTO = {
    CoursesRepo.byInstitution(fetchChildCourses == "true", getAutenticatedPersonInstitutionUUID, searchTerm, pageSize, pageNumber, orderBy, asc == "true")
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @POST
  @Produces(Array(CourseClassTO.TYPE))
  @Consumes(Array(Course.TYPE))
  def create(course: Course): CourseClassTO = {
    CourseCreationService.simpleCreation(getAutenticatedPersonInstitutionUUID, course)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get
}

object CoursesResource {
  def apply() = new CoursesResource()
}
