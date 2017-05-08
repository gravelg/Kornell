package db.jdbcmigration

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import java.sql.Connection
import kornell.server.util.EnrollmentUtil._
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.jdbc.SQL._
import java.io.File
import org.apache.commons.io.FileUtils
import java.net.URL
import kornell.core.util.StringUtils
import net.lingala.zip4j.core.ZipFile
import java.sql.ResultSet
import kornell.server.jdbc.ConnectionHandler


class V2017_04_25_23_38__UpdateCourseCodes extends JdbcMigration  {
  override def migrate(conn: Connection) {
    migrateCourseCodes
  }

  def migrateCourseCodes() = {
    val versions = sql"""
  		  select course_uuid, distributionPrefix, uuid from CourseVersion
  		""".map[(String, String, String)](toTuple)

    for ((courseUUID, prefix, versionUUID) <- versions) {
      val code = prefix.replaceAll("/[0-9a-zA-Z\\.\\-]+/?$", "")
      val newPrefix = prefix.replaceAll("^[0-9a-zA-Z\\-]+/", "")
      println(code + " " + newPrefix + " " + courseUUID + " " + versionUUID)
      sql"""
  		  update Course set code = ${code} where uuid = ${courseUUID}
  		""".executeUpdate
  		sql"""
  		  update CourseVersion set distributionPrefix = ${newPrefix} where uuid = ${versionUUID}
  		""".executeUpdate
    }
    ConnectionHandler.commit()
  }

  implicit def toTuple(rs: ResultSet): (String, String, String) = (rs.getString(1), rs.getString(2), rs.getString(3))
}