package kornell.server.jdbc

import java.util.logging._

import com.googlecode.flyway.core.Flyway
import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import kornell.server.util.Settings._

object DataSources {
  val log: Logger = Logger.getLogger(getClass.getName)

  lazy val hikariDS: HikariDataSource = {
    val driverName = JDBC_DRIVER.get
    val jdbcURL = JDBC_CONNECTION_STRING.get
    val username = JDBC_USERNAME.get
    val password = JDBC_PASSWORD.get
    log.info(s"JDBC properties [$driverName, $jdbcURL, $username, *****]")
    val config = new HikariConfig()
    config.setDriverClassName(driverName)
    config.setJdbcUrl(jdbcURL)
    config.setUsername(username)
    config.setPassword(password)
    config.addDataSourceProperty("characterEncoding", "utf8")
    config.addDataSourceProperty("useUnicode", "true")
    config.setAutoCommit(false)
    val ds = new HikariDataSource(config)
    ds
  }

  lazy val POOL = { () => hikariDS.getConnection }

  val connectionFactory = POOL

  def configure(flyway: Flyway): Unit = flyway.setDataSource(
    JDBC_CONNECTION_STRING,
    JDBC_USERNAME,
    JDBC_PASSWORD)
}
