package kornell.server.api

import javax.ws.rs._
import javax.ws.rs.core.Response
import kornell.core.entity.{CourseDetailsEntityType, CourseDetailsLibrary}
import kornell.core.to.CourseDetailsLibrariesTO
import kornell.server.jdbc.repository.CourseDetailsLibrariesRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("courseDetailsLibraries")
class CourseDetailsLibrariesResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = CourseDetailsLibraryResource(uuid)

  @POST
  @Consumes(Array(CourseDetailsLibrary.TYPE))
  @Produces(Array(CourseDetailsLibrary.TYPE))
  def create(courseDetailsLibrary: CourseDetailsLibrary): CourseDetailsLibrary = {
    CourseDetailsLibrariesRepo.create(courseDetailsLibrary)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @GET
  @Path("/{entityType}/{entityUUID}")
  @Produces(Array(CourseDetailsLibrariesTO.TYPE))
  def getByEntityTypeAndUUID(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String): CourseDetailsLibrariesTO = {
    CourseDetailsLibrariesRepo.getForEntity(entityUUID, CourseDetailsEntityType.valueOf(entityType))
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @POST
  @Path("/{entityType}/{entityUUID}/moveUp/{index}")
  def moveUp(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String,
    @PathParam("index") index: String): Response = {
    CourseDetailsLibrariesRepo.moveUp(entityUUID, CourseDetailsEntityType.valueOf(entityType), index.toInt)
    Response.noContent.build
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @POST
  @Path("/{entityType}/{entityUUID}/moveDown/{index}")
  def moveDown(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String,
    @PathParam("index") index: String): Response = {
    CourseDetailsLibrariesRepo.moveDown(entityUUID, CourseDetailsEntityType.valueOf(entityType), index.toInt)
    Response.noContent.build
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

}

object CourseDetailsLibrariesResource {
  def apply(uuid: String) = new CourseDetailsLibraryResource(uuid)
}
