package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.CourseVersion
import kornell.core.to.CourseVersionsTO
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("courseVersions")
class CourseVersionsResource {

  @Path("{uuid}")
  def getCourseVersion(@PathParam("uuid") uuid: String) = CourseVersionResource(uuid)

  @GET
  @Produces(Array(CourseVersionsTO.TYPE))
  def getCourseVersions(@QueryParam("courseUUID") courseUUID: String, @QueryParam("searchTerm") searchTerm: String,
    @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int, @QueryParam("orderBy") orderBy: String, @QueryParam("asc") asc: String): CourseVersionsTO = {
    CourseVersionsRepo.byInstitution(getAutenticatedPersonInstitutionUUID, searchTerm, pageSize, pageNumber, orderBy, asc == "true", courseUUID)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @POST
  @Produces(Array(CourseVersion.TYPE))
  @Consumes(Array(CourseVersion.TYPE))
  def create(courseVersion: CourseVersion): CourseVersion = {
    CourseVersionsRepo.create(courseVersion, getAutenticatedPersonInstitutionUUID)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get
}

object CourseVersionsResource {
  def apply() = new CourseVersionsResource()
}
