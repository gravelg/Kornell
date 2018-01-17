package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.POST
import javax.ws.rs.Produces
import javax.ws.rs.Consumes
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.server.util.Conditional.toConditional
import kornell.server.util.AccessDeniedErr
import javax.ws.rs.PathParam
import javax.ws.rs.PUT
import kornell.core.entity.ContentRepository

@Path("contentRepositories")
class ContentRepositoriesResource {

  @Path("{repositoryUUID}")
  def getByUUID(@PathParam("repositoryUUID") repositoryUUID: String) = ContentRepositoryResource(repositoryUUID)

  @POST
  @Produces(Array(ContentRepository.TYPE))
  @Consumes(Array(ContentRepository.TYPE))
  def createRepo(contentRepository: ContentRepository) = {
    ContentRepositoriesRepo.createRepo(contentRepository)
  }.requiring(isControlPanelAdmin(), AccessDeniedErr()).get
}
