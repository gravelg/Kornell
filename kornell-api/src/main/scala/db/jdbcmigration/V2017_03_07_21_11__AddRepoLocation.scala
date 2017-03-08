package db.jdbcmigration

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import java.sql.Connection
import kornell.server.util.EnrollmentUtil._
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.SQL._

class V2017_03_07_23_11__AddRepoLocation extends JdbcMigration  {
  override def migrate(conn: Connection) {
    migrateAddRepoLocation
  }

  def migrateAddRepoLocation() = {
    val repoPath = this.getClass
                       .getClassLoader().getResource("hello-world/").toString();
    println(repoPath)
    val uuid = "hwrbf852-91d1-4373-b9f7-979ce0e907c3"
    sql"""
		  update ContentRepository set path = ${repoPath} where uuid = ${uuid}
		""".executeUpdate
  }

}