package db.jdbcmigration

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import kornell.server.jdbc.ConnectionHandler
import java.sql.Connection
import kornell.server.service.SandboxService
import kornell.server.jdbc.SQL._

class V2018_04_17_00_09__CreateSandboxClasses extends JdbcMigration {
  override def migrate(conn: Connection) {
    createSandboxClasses
  }

  def createSandboxClasses() = {
    val institutions = sql"""
        select uuid from Institution
      """.map[String]

    for (institutionUUID <- institutions) {
      SandboxService.processInstitution(institutionUUID)
    }
    ConnectionHandler.commit()
  }
}
