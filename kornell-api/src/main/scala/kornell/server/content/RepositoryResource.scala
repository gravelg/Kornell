package kornell.server.content

import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.core.Response
import javax.ws.rs.PathParam
import kornell.core.util.StringUtils
import java.nio.file.Files
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletResponse
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import org.apache.commons.io.IOUtils
import javax.ws.rs.core.Response
import scala.util.Success
import javax.ws.rs.NotFoundException
import scala.util.Success
import scala.util.Failure

@Path("/")
class RepositoryResource {

  @Path("{path: .+}")
  @GET
  def get(@Context resp: HttpServletResponse, @PathParam("path") path: String): Response = {
    val repoData = StringUtils.parseRepositoryData(path)
    val repositoryUUID = repoData.getRepositoryUUID()
    val cm = ContentManagers.forRepository(repositoryUUID);
    val key = repoData.getKey
    val input = cm.inputStream(key)
    input match {
      case Success(in) => {
        val out: OutputStream = resp.getOutputStream();
        try {
          resp.setContentType(StringUtils.getMimeType(key))
          IOUtils.copy(in, out)
          in.close()
          Response.ok().build()
        } catch {
          case ioe: IOException => Response.status(500).entity(s"Key [${key}] not loaded").build()
        }
      }
      case Failure(e) => Response.status(404).entity(s"Key [${key}] not found").build()
    }
  }

}
