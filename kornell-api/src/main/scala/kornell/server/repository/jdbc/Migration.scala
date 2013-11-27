package kornell.server.repository.jdbc

import scala.util.Try
import com.googlecode.flyway.core.Flyway
import scala.util._

object Migration {
  def apply() = migration

  lazy val migration = migrate match {
    case Success(m) => m
    case Failure(t) => throw t
  }

  def migrate = Try {
    val flyway = new Flyway();
    setDataSource(flyway)
    flyway.setLocations("db/");
    flyway.migrate();
  }

  //TODO: Clean this ugly hack moving it to DataSources
  private def setDataSource(flyway: com.googlecode.flyway.core.Flyway): Unit = {
    DataSources.JNDI match {
      case Success(_) => flyway.setDataSource(DataSources.KornellDS)
      case Failure(_) => DataSources.SYSPROPS match {
        case Success(_) => flyway.setDataSource(prop("JDBC_CONNECTION_STRING"),
          prop("JDBC_USERNAME"),
          prop("JDBC_PASSWORD"))
        case Failure(_) => flyway.setDataSource(DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD)
      }
    }
  }

}