package db.jdbcmigration

import java.sql.Connection

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import kornell.server.jdbc.ConnectionHandler
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.EnrollmentRepo
import kornell.server.util.EnrollmentUtil._

class V2016_02_13_16_45__PostAssessmentScore extends JdbcMigration {
  override def migrate(conn: Connection): Unit = {
    migratePostAssessmentScores()
  }

  def migratePostAssessmentScores(): Unit = {
    sql"""
		  select ae.enrollment_uuid,ae.entryValue 
		  from ActomEntries ae
		  join Enrollment e on ae.enrollment_uuid = e.uuid
		  where entryKey = 'cmi.suspend_data' 
				and ae.entryValue like '%finalteste%'
				and e.postAssessmentScore is null
		""".foreach { rs =>
      val uuid = rs.getString("enrollment_uuid")
      val sdata = rs.getString("entryValue")
      for (score <- parsePostScore(sdata)) {
        println(s"Updating [${uuid}] postAssessment [${score}]")
        EnrollmentRepo(uuid).updatePostAssessmentScore(score)
      }
    }
    ConnectionHandler.commit()
  }

}