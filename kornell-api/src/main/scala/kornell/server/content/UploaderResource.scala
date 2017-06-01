package kornell.server.content

import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.ws.rs.PathParam
import kornell.core.util.StringUtils
import java.nio.file.Files
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.OPTIONS
import javax.ws.rs.PUT
import java.nio.file.Paths
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import java.nio.file.StandardCopyOption

@Path("/")
class UploaderResource {

  @Path("{path: .+}")
  @OPTIONS
  def options(): Response = Response.status(200).build()
  
  @Path("{path: .+}")
  @PUT
  def put(@Context resp: HttpServletResponse, @Context req: HttpServletRequest, @PathParam("path") path: String):Response = {
    val repoData = StringUtils.parseRepositoryData(path.replace("repository/", ""))
    val repositoryUUID = repoData.getRepositoryUUID()
    val cm = ContentManagers.forRepository(repositoryUUID);
    val repo = ContentRepositoriesRepo.getByRepositoryUUID(repositoryUUID).get
    val destinationPath = Paths.get(StringUtils.mkurl(repo.getPath, path))
    //create dirs if they do not exist
    destinationPath.getParent.toFile().mkdirs()

    val fileContent = req.getInputStream
    try {
      Files.copy(fileContent, destinationPath, StandardCopyOption.REPLACE_EXISTING)
      Response.status(200).build()
    } catch {
      case e: Exception => Response.status(500).entity(s"Key [${path}] not loaded").build()
    } finally {
      fileContent.close()
    }
  }

}