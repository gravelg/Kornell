package kornell.server.util

import scala.util.Try
import java.math.BigDecimal

import scala.util.matching.Regex

object EnrollmentUtil {
  type EntriesMap = java.util.Map[String, String]

  val preScore_r: Regex = """.*::preteste,(\d+).*""".r
  val postScore_r: Regex = """.*::finalteste,(\d+).*""".r

  def containsProgress(entries: EntriesMap): Boolean =
    entries.containsKey("cmi.core.lesson_status") ||
      entries.containsKey("cmi.core.lesson_location") ||
      entries.containsKey("cmi.suspend_data")

  def containsAssessment(entries: EntriesMap): Boolean =
    entries.containsKey("cmi.core.score.raw") ||
      entries.containsKey("cmi.suspend_data")

  def parsePreScore(value: String): Option[BigDecimal] = value match {
    case preScore_r(x) => parseBigDecimal(x)
    case _ => None
  }

  def parsePostScore(value: String): Option[BigDecimal] = value match {
    case postScore_r(x) => parseBigDecimal(x)
    case _ => None
  }

  def optSuspendData(entries: EntriesMap): Option[String] =
    Option(entries.get("cmi.suspend_data"))

  def parsePreAssessmentScore(entries: EntriesMap): Option[BigDecimal] =
    optSuspendData(entries) flatMap parsePreScore

  def parsePostAssessmentScore(entries: EntriesMap): Option[BigDecimal] =
    optSuspendData(entries) flatMap parsePostScore

  def parseBigDecimal(x: String): Option[BigDecimal] = Try {
    new BigDecimal(x)
  }.toOption

}