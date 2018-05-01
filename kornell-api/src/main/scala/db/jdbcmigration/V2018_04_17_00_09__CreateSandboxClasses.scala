package db.jdbcmigration

import java.sql.Connection

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import kornell.server.jdbc.ConnectionHandler
import kornell.server.jdbc.SQL._
import kornell.server.service.SandboxService

class V2018_04_17_00_09__CreateSandboxClasses extends JdbcMigration {
  override def migrate(conn: Connection): Unit = {
    createSandboxClasses()
  }

  def createSandboxClasses(): Unit = {
    val institutions = sql"""
        select uuid from Institution
      """.map[String]

    for (institutionUUID <- institutions) {
      SandboxService.processInstitution(institutionUUID)
    }
    ConnectionHandler.commit()
  }
}
