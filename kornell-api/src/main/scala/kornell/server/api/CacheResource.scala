package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.PathParam
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.TokenRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional
import kornell.server.jdbc.repository.PersonRepo
import javax.ws.rs.POST
import kornell.core.error.exception.EntityNotFoundException

@Path("cache")
class CacheResource {
  
  @Path("/clear/{entityType}")
  @POST
  def clearCache(@PathParam("entityType") entityType: String) = {
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
    ""
  }.requiring(isPlatformAdmin(PersonRepo(getAuthenticatedPersonUUID).get.getInstitutionUUID), AccessDeniedErr()).get
}