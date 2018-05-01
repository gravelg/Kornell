package kornell.server.jdbc.repository
import kornell.server.jdbc.SQL._

object ActomEntriesRepo {
  def getValue(enrollmentUUID: String, actomKey: String, entryKey: String): Option[String] = {
    val value = sql"""
    select * from ActomEntries
    where enrollmentUUID = $enrollmentUUID
    and actomKey like $actomKey
    and entryKey = $entryKey
  """.first[String] { _.getString("entryValue") }
    value
  }

  def getValues(enrollmentUUID: String, actomKey: String, entryKey: String): List[String] = {
    val value = sql"""
    select * from ActomEntries
    where enrollmentUUID = $enrollmentUUID
    and actomKey like $actomKey
    and entryKey = $entryKey
  """.map[String] { _.getString("entryValue") }
    value
  }
}
