package db.jdbcmigration

import java.sql.{Connection, ResultSet}

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import kornell.server.jdbc.ConnectionHandler
import kornell.server.jdbc.SQL._

class V2017_04_25_23_38__UpdateCourseCodes extends JdbcMigration {
  override def migrate(conn: Connection): Unit = {
    migrateCourseCodes()
  }

  def migrateCourseCodes(): Unit = {
    val versions = sql"""
  		  select course_uuid, distributionPrefix, uuid from CourseVersion
  		""".map[(String, String, String)](toTuple)

    for ((courseUUID, prefix, versionUUID) <- versions) {

      try {
        val code = prefix.replaceAll("/[0-9a-zA-Z\\.\\-]+/?$", "")
        val newPrefix = prefix.replaceAll("^[0-9a-zA-Z\\-]+/", "")
        println(code + " " + newPrefix + " " + courseUUID + " " + versionUUID)
        if (code.length > 0 && newPrefix.length > 0) {
          sql"""
      		  update Course set code = ${code} where uuid = ${courseUUID}
      		""".executeUpdate
          sql"""
      		  update CourseVersion set distributionPrefix = ${newPrefix} where uuid = ${versionUUID}
      		""".executeUpdate
        }
      } catch {
        case _: Exception =>
      }
    }
    ConnectionHandler.commit()
  }

  implicit def toTuple(rs: ResultSet): (String, String, String) = (rs.getString(1), rs.getString(2), rs.getString(3))
}