package kornell.server.api

import javax.ws.rs.{GET, Path}
import javax.ws.rs.core.Response
import kornell.server.jdbc.SQL._

import scala.util.{Failure, Success, Try}

@Path("healthCheck")
class HealthCheckResource {

  @GET
  def isHealthy: Response = checkDatabase match {
    case Success(_) => Response.ok.entity("System seems healthy.").build
    case Failure(ex) => Response.serverError.entity(ex.getMessage).build
  }

  def checkDatabase = Try { sql"select 'Health Check'".executeQuery() }
}
