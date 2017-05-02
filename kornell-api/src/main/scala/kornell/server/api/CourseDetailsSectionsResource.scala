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
import kornell.core.entity.CourseDetailsSection
import kornell.server.jdbc.repository.CourseDetailsSectionsRepo
import kornell.core.entity.CourseDetailsEntityType

@Path("courseDetailsSections")
class CourseDetailsSectionsResource {
  
  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = CourseDetailsSectionResource(uuid)
   
  @POST
  @Consumes(Array(CourseDetailsSection.TYPE))
  @Produces(Array(CourseDetailsSection.TYPE))
  def create(courseDetailsSection: CourseDetailsSection) = {
    CourseDetailsSectionsRepo.create(courseDetailsSection)
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
   
   @GET
   @Path("/{entityType}/{entityUUID}")
   @Produces(Array(CourseDetailsSection.TYPE))
   def getByEntityTypeAndUUID(@PathParam("entityType") entityType: String,
       @PathParam("entityUUID") entityUUID: String) = {
    CourseDetailsSectionsRepo.getForEntity(entityUUID, CourseDetailsEntityType.valueOf(entityType))
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .or(isInstitutionAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr())
   .get
   
}

object CourseDetailsSectionsResource {
  def apply(uuid: String) = new CourseDetailsSectionResource(uuid)
}