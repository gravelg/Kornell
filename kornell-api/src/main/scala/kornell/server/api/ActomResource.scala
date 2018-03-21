package kornell.server.api

import java.sql.ResultSet
import java.util.HashMap
import scala.collection.JavaConversions._
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.Context
import kornell.core.entity.ActomEntries
import kornell.server.ep.EnrollmentSEP
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.ActomEntriesRepo
import kornell.server.repository.Entities
import kornell.core.scorm12.rte.RTE
import kornell.core.scorm12.rte.DMElement
import kornell.server.scorm12.SCORM12
import kornell.server.jdbc.repository.AuthRepo
import scala.collection.mutable.ListBuffer
import java.util.Map
import kornell.server.jdbc.PreparedStmt
import scala.util.Try
import java.math.BigDecimal
import kornell.server.util.EnrollmentUtil._
import scala.collection.JavaConverters._
import java.util.Date
import javax.ws.rs.core.Response
import kornell.core.util.UUID

class ActomResource(enrollmentUUID: String, actomURL: String) {
  implicit def toString(rs: ResultSet): String = rs.getString("entryValue")

  @GET
  def get() = actomKey

  val actomKey = if (actomURL.contains("?"))
    actomURL.substring(0, actomURL.indexOf("?"))
  else
    actomURL

  @Path("entries/{entryKey}")
  @GET
  def getValue(@PathParam("entryKey") entryKey: String) = {
    ActomEntriesRepo.getValue(enrollmentUUID, actomKey, entryKey)
    Response.noContent.build
  }

  @Path("entries/{entryKey}")
  @Consumes(Array("text/plain"))
  @PUT
  def putValue(@PathParam("entryKey") entryKey: String, entryValue: String) = {
    updateQueryModel(entryKey, entryValue)
    Response.noContent.build
  }

  def updateQueryModel(entryKey: String, entryValue: String) = sql"""
    insert into ActomEntries (uuid, enrollmentUUID, actomKey, entryKey, entryValue)
    values (${UUID.random}, ${enrollmentUUID} , ${actomKey}, ${entryKey}, ${entryValue})
    on duplicate key update entryValue = ${entryValue}
  """.executeUpdate

  //Batch version of put value using a map
  def putValues(actomEntries: Map[String, String]) = {
    var queryModelQuery = "insert into ActomEntries (uuid, enrollmentUUID, actomKey, entryKey, entryValue) values "
    val queryModelStrings = new ListBuffer[String]
    for ((key, value) <- actomEntries) {
      queryModelStrings += ("('" + UUID.random + "','" + enrollmentUUID + "','" + actomKey + "','" + key + "','" + value + "')")
    }
    queryModelQuery += queryModelStrings.mkString(",")
    queryModelQuery += " on duplicate key update entryValue = VALUES(entryValue)"

    new PreparedStmt(queryModelQuery, List[String]()).executeUpdate
  }

  @Path("entries")
  @Consumes(Array(ActomEntries.TYPE))
  @Produces(Array(ActomEntries.TYPE))
  @PUT
  def putEntries(entries: ActomEntries) = {
    val modifiedAt = entries.getLastModifiedAt()
    val actomEntries = entries.getEntries

    val enrollmentMap = collection.mutable.Map[String, String]()
    for ((key, value) <- actomEntries)
      enrollmentMap(key) = value

    val enrollmentsJMap = enrollmentMap.asJava
    if (enrollmentsJMap.size() > 0) {
      putValues(enrollmentsJMap)
    }

    val hasProgress = containsProgress(actomEntries)
    if (hasProgress) {
      EnrollmentSEP.onProgress(enrollmentUUID)
    }
    val hasAssessment = containsAssessment(actomEntries)
    if (hasAssessment) {
      EnrollmentSEP.onAssessment(enrollmentUUID)
    }

    parsePreAssessmentScore(actomEntries)
      .foreach {
        EnrollmentSEP.onPreAssessmentScore(enrollmentUUID, _);
      }

    parsePostAssessmentScore(actomEntries)
      .foreach {
        EnrollmentSEP.onPostAssessmentScore(enrollmentUUID, _);
      }

    entries
  }

  @Path("entries")
  @Produces(Array(ActomEntries.TYPE))
  @GET
  def getEntries(): ActomEntries = {
    val entries = Entities.newActomEntries(enrollmentUUID, actomKey, new HashMap[String, String])
    sql"""
    select * from ActomEntries
    where enrollmentUUID=${enrollmentUUID}
      and actomKey=${actomKey}""".foreach { rs =>
      entries.getEntries().put(rs.getString("entryKey"), rs.getString("entryValue"))
    }
    entries
  }

}

object ActomResource {
  def apply(enrollmentUUID: String, actomKey: String) = new ActomResource(enrollmentUUID, actomKey);
}
