package kornell.server.api

import javax.ws.rs._
import javax.ws.rs.core.Response
import kornell.core.entity.{CourseDetailsEntityType, CourseDetailsSection}
import kornell.core.to.CourseDetailsSectionsTO
import kornell.server.jdbc.repository.CourseDetailsSectionsRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("courseDetailsSections")
class CourseDetailsSectionsResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = CourseDetailsSectionResource(uuid)

  @POST
  @Consumes(Array(CourseDetailsSection.TYPE))
  @Produces(Array(CourseDetailsSection.TYPE))
  def create(courseDetailsSection: CourseDetailsSection): CourseDetailsSection = {
    CourseDetailsSectionsRepo.create(courseDetailsSection)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @GET
  @Path("/{entityType}/{entityUUID}")
  @Produces(Array(CourseDetailsSectionsTO.TYPE))
  def getByEntityTypeAndUUID(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String): CourseDetailsSectionsTO = {
    CourseDetailsSectionsRepo.getForEntity(entityUUID, CourseDetailsEntityType.valueOf(entityType))
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @POST
  @Path("/{entityType}/{entityUUID}/moveUp/{index}")
  def moveUp(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String,
    @PathParam("index") index: String): Response = {
    CourseDetailsSectionsRepo.moveUp(entityUUID, CourseDetailsEntityType.valueOf(entityType), index.toInt)
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
    CourseDetailsSectionsRepo.moveDown(entityUUID, CourseDetailsEntityType.valueOf(entityType), index.toInt)
    Response.noContent.build
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

}

object CourseDetailsSectionsResource {
  def apply(uuid: String) = new CourseDetailsSectionResource(uuid)
}
