package kornell.server.api

import javax.ws.rs.GET
import kornell.core.entity.ContentRepository
import javax.ws.rs.Produces
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.ContentRepositoryRepo
import javax.ws.rs.PUT
import javax.ws.rs.Consumes

class ContentRepositoryResource(uuid: String) {

  @GET
  @Produces(Array(ContentRepository.TYPE))
  def get: ContentRepository = {
    ContentRepositoryRepo(uuid).get
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .get

  @PUT
  @Consumes(Array(ContentRepository.TYPE))
  @Produces(Array(ContentRepository.TYPE))
  def update(contentRepo: ContentRepository) = {
    ContentRepositoryRepo(uuid).update(contentRepo)
  }.requiring(isPlatformAdmin(), AccessDeniedErr())
    .or(isInstitutionAdmin(), AccessDeniedErr())
    .get
}

object ContentRepositoryResource {
  def apply(uuid: String) = new ContentRepositoryResource(uuid)
}
