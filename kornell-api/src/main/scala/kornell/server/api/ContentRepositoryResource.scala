package kornell.server.api

import javax.ws.rs.{Consumes, GET, PUT, Produces}
import kornell.core.entity.ContentRepository
import kornell.server.jdbc.repository.ContentRepositoryRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

class ContentRepositoryResource(uuid: String) {

  @GET
  @Produces(Array(ContentRepository.TYPE))
  def get: ContentRepository = {
    ContentRepositoryRepo(uuid).get
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(ContentRepository.TYPE))
  @Produces(Array(ContentRepository.TYPE))
  def update(contentRepo: ContentRepository): ContentRepository = {
    ContentRepositoryRepo(uuid).update(contentRepo)
  }.requiring(isPlatformAdmin, AccessDeniedErr())
    .or(isInstitutionAdmin, AccessDeniedErr())
    .get
}

object ContentRepositoryResource {
  def apply(uuid: String) = new ContentRepositoryResource(uuid)
}
