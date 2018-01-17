package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.GET
import kornell.server.jdbc.SQL._
import scala.util.Try
import scala.util.Success
import scala.util.Success
import javax.ws.rs.core.Response
import scala.util.Failure
import javax.ws.rs.core.Response._
import kornell.server.util.EmailSender._
import java.sql.ResultSet

@Path("healthCheck")
class HealthCheckResource {

  @GET
  def isHealthy = checkDatabase match {
    case Success(_) => ok.entity("System seems healthy.").build
    case Failure(ex) => serverError.entity(ex.getMessage).build
  }

  def checkDatabase = Try { sql"select 'Health Check'".executeQuery }
}
