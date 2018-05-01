package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.CourseDetailsSection
import kornell.server.jdbc.repository.CourseDetailsSectionRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

class CourseDetailsSectionResource(uuid: String) {

  @GET
  @Produces(Array(CourseDetailsSection.TYPE))
  def get: CourseDetailsSection = {
    CourseDetailsSectionRepo(uuid).get
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(CourseDetailsSection.TYPE))
  @Produces(Array(CourseDetailsSection.TYPE))
  def update(courseDetailsSection: CourseDetailsSection): CourseDetailsSection = {
    CourseDetailsSectionRepo(uuid).update(courseDetailsSection)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(CourseDetailsSection.TYPE))
  def delete(): CourseDetailsSection = {
    CourseDetailsSectionRepo(uuid).delete
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

}

object CourseDetailsSectionResource {
  def apply(uuid: String) = new CourseDetailsSectionResource(uuid)
}
