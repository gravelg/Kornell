package kornell.server.api

import javax.ws.rs.core.Response
import javax.ws.rs.{POST, Path, PathParam}
import kornell.core.error.exception.EntityNotFoundException
import kornell.server.jdbc.repository.{ContentRepositoriesRepo, EnrollmentsRepo, InstitutionsRepo, PeopleRepo, PersonRepo, TokenRepo}
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("cache")
class CacheResource {

  @Path("/clear/{entityType}")
  @POST
  def clearCache(@PathParam("entityType") entityType: String): Response = {
    entityType match {
      case "institutions" => InstitutionsRepo.clearCache()
      case "contentRepositories" => ContentRepositoriesRepo.clearCache()
      case "enrollments" => EnrollmentsRepo.clearCache()
      case "people" => PeopleRepo.clearCache()
      case "tokens" => TokenRepo.clearCache()
      case "all" => {
        InstitutionsRepo.clearCache()
        ContentRepositoriesRepo.clearCache()
        EnrollmentsRepo.clearCache()
        PeopleRepo.clearCache()
        TokenRepo.clearCache()
      }
      case _ => throw new EntityNotFoundException("invalidEntityType")
    }
    Response.noContent.build
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr()).get
}
