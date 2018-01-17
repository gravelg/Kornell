package kornell.server.api

import javax.ws.rs.GET
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.PersonRepo
import javax.ws.rs.Consumes
import javax.ws.rs.PUT
import javax.ws.rs.DELETE
import kornell.server.util.AccessDeniedErr
import kornell.core.entity.CourseDetailsHint
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import kornell.core.entity.CourseDetailsLibrary
import kornell.server.jdbc.repository.CourseDetailsLibrariesRepo
import kornell.core.entity.CourseDetailsEntityType
import kornell.core.to.CourseDetailsLibrariesTO

@Path("courseDetailsLibraries")
class CourseDetailsLibrariesResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = CourseDetailsLibraryResource(uuid)

  @POST
  @Consumes(Array(CourseDetailsLibrary.TYPE))
  @Produces(Array(CourseDetailsLibrary.TYPE))
  def create(courseDetailsLibrary: CourseDetailsLibrary) = {
    CourseDetailsLibrariesRepo.create(courseDetailsLibrary)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @GET
  @Path("/{entityType}/{entityUUID}")
  @Produces(Array(CourseDetailsLibrariesTO.TYPE))
  def getByEntityTypeAndUUID(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String) = {
    CourseDetailsLibrariesRepo.getForEntity(entityUUID, CourseDetailsEntityType.valueOf(entityType))
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @POST
  @Path("/{entityType}/{entityUUID}/moveUp/{index}")
  def moveUp(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String,
    @PathParam("index") index: String) = {
    CourseDetailsLibrariesRepo.moveUp(entityUUID, CourseDetailsEntityType.valueOf(entityType), index.toInt)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

  @POST
  @Path("/{entityType}/{entityUUID}/moveDown/{index}")
  def moveDown(@PathParam("entityType") entityType: String,
    @PathParam("entityUUID") entityUUID: String,
    @PathParam("index") index: String) = {
    CourseDetailsLibrariesRepo.moveDown(entityUUID, CourseDetailsEntityType.valueOf(entityType), index.toInt)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
    .get

}

object CourseDetailsLibrariesResource {
  def apply(uuid: String) = new CourseDetailsLibraryResource(uuid)
}
