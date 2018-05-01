package kornell.server.api

import javax.ws.rs._
import kornell.core.entity.ContentRepository
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("contentRepositories")
class ContentRepositoriesResource {

  @Path("{repositoryUUID}")
  def getByUUID(@PathParam("repositoryUUID") repositoryUUID: String) = ContentRepositoryResource(repositoryUUID)

  @POST
  @Produces(Array(ContentRepository.TYPE))
  @Consumes(Array(ContentRepository.TYPE))
  def createRepo(contentRepository: ContentRepository): ContentRepository = {
    ContentRepositoriesRepo.createRepo(contentRepository)
  }.requiring(isControlPanelAdmin, AccessDeniedErr()).get
}
