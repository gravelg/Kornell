package kornell.server.content

import java.io.{IOException, OutputStream}

import javax.servlet.http.HttpServletResponse
import javax.ws.rs.{GET, Path, PathParam}
import javax.ws.rs.core.{Context, Response}
import kornell.core.util.StringUtils
import org.apache.commons.io.IOUtils

import scala.util.{Failure, Success}

@Path("/")
class RepositoryResource {

  @Path("{path: .+}")
  @GET
  def get(@Context resp: HttpServletResponse, @PathParam("path") path: String): Response = {
    val repoData = StringUtils.parseRepositoryData(path)
    val repositoryUUID = repoData.getRepositoryUUID
    val cm = ContentManagers.forRepository(repositoryUUID)
    val key = repoData.getKey
    val input = cm.inputStream(key)
    input match {
      case Success(in) => {
        val out: OutputStream = resp.getOutputStream
        try {
          resp.setContentType(StringUtils.getMimeType(key))
          IOUtils.copy(in, out)
          in.close()
          Response.ok().build()
        } catch {
          case _: IOException => Response.status(500).entity(s"Key [${key}] not loaded").build()
        }
      }
      case Failure(_) => Response.status(404).entity(s"Key [${key}] not found").build()
    }
  }
}
