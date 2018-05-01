package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.CourseDetailsHint
import kornell.server.jdbc.repository.CourseDetailsHintRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

class CourseDetailsHintResource(uuid: String) {

  @GET
  @Produces(Array(CourseDetailsHint.TYPE))
  def get: CourseDetailsHint = {
    CourseDetailsHintRepo(uuid).get
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(CourseDetailsHint.TYPE))
  @Produces(Array(CourseDetailsHint.TYPE))
  def update(courseDetailsHint: CourseDetailsHint): CourseDetailsHint = {
    CourseDetailsHintRepo(uuid).update(courseDetailsHint)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(CourseDetailsHint.TYPE))
  def delete(): CourseDetailsHint = {
    CourseDetailsHintRepo(uuid).delete
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get
}

object CourseDetailsHintResource {
  def apply(uuid: String) = new CourseDetailsHintResource(uuid)
}
