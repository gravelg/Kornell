package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.jdbc.repository.AuthRepo
import kornell.core.to.CourseVersionsTO
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.repository.Entities
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import javax.ws.rs.Consumes
import kornell.core.entity.CourseVersion
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr

@Path("courseVersions")
class CourseVersionsResource {

  @Path("{uuid}")
  def getCourseVersion(@PathParam("uuid") uuid: String) = CourseVersionResource(uuid)

  @GET
  @Produces(Array(CourseVersionsTO.TYPE))
  def getCourseVersions(@QueryParam("courseUUID") courseUUID: String, @QueryParam("searchTerm") searchTerm: String,
    @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int, @QueryParam("orderBy") orderBy: String, @QueryParam("asc") asc: String) = {
    CourseVersionsRepo.byInstitution(getAutenticatedPersonInstitutionUUID, searchTerm, pageSize, pageNumber, orderBy, asc == "true", courseUUID)
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get

  @POST
  @Produces(Array(CourseVersion.TYPE))
  @Consumes(Array(CourseVersion.TYPE))
  def create(courseVersion: CourseVersion) = {
    CourseVersionsRepo.create(courseVersion, getAutenticatedPersonInstitutionUUID)
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .or(isPublisher(), AccessDeniedErr())
    .get
}

object CourseVersionsResource {
  def apply() = new CourseVersionsResource()
}
