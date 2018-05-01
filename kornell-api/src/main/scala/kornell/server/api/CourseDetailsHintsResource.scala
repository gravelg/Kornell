package kornell.server.api

import javax.ws.rs._
import javax.ws.rs.core.Response
import kornell.core.entity.{CourseDetailsEntityType, CourseDetailsHint}
import kornell.core.to.CourseDetailsHintsTO
import kornell.server.jdbc.repository.CourseDetailsHintsRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("courseDetailsHints")
class CourseDetailsHintsResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = CourseDetailsHintResource(uuid)

  @POST
  @Consumes(Array(CourseDetailsHint.TYPE))
  @Produces(Array(CourseDetailsHint.TYPE))
  def create(courseDetailsHint: CourseDetailsHint): CourseDetailsHint = {
    CourseDetailsHintsRepo.create(courseDetailsHint)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @GET
  @Path("/{entityType}/{entityUUID}")
  @Produces(Array(CourseDetailsHintsTO.TYPE))
  def getByEntityTypeAndUUID(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String): CourseDetailsHintsTO = {
    CourseDetailsHintsRepo.getForEntity(entityUUID, CourseDetailsEntityType.valueOf(entityType))
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @POST
  @Path("/{entityType}/{entityUUID}/moveUp/{index}")
  def moveUp(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String,
    @PathParam("index") index: String): Response = {
    CourseDetailsHintsRepo.moveUp(entityUUID, CourseDetailsEntityType.valueOf(entityType), index.toInt)
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
    CourseDetailsHintsRepo.moveDown(entityUUID, CourseDetailsEntityType.valueOf(entityType), index.toInt)
    Response.noContent.build
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

}

object CourseDetailsHintsResource {
  def apply(uuid: String) = new CourseDetailsHintResource(uuid)
}
