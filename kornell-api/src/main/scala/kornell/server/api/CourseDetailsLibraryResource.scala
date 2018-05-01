package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.CourseDetailsLibrary
import kornell.server.jdbc.repository.CourseDetailsLibraryRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

class CourseDetailsLibraryResource(uuid: String) {

  @GET
  @Produces(Array(CourseDetailsLibrary.TYPE))
  def get: CourseDetailsLibrary = {
    CourseDetailsLibraryRepo(uuid).get
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(CourseDetailsLibrary.TYPE))
  @Produces(Array(CourseDetailsLibrary.TYPE))
  def update(courseDetailsLibrary: CourseDetailsLibrary): CourseDetailsLibrary = {
    CourseDetailsLibraryRepo(uuid).update(courseDetailsLibrary)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get

  @DELETE
  @Produces(Array(CourseDetailsLibrary.TYPE))
  def delete(): CourseDetailsLibrary = {
    CourseDetailsLibraryRepo(uuid).delete
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .or(isPublisher, AccessDeniedErr())
    .get
}

object CourseDetailsLibraryResource {
  def apply(uuid: String) = new CourseDetailsLibraryResource(uuid)
}
