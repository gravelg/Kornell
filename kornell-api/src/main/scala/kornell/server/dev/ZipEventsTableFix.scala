package kornell.server.dev

import kornell.server.jdbc.SQL._
import java.text.DateFormat
import java.text.SimpleDateFormat
import kornell.server.jdbc.DataSources
import java.sql.Connection
import java.util.Date
import java.util.concurrent.atomic.AtomicInteger
import scala.util.Try

//HOWTO: mvn exec:java -Dexec.mainClass=kornell.server.dev.ZipEventsTableFix -DcleanupDaemonThreads = false
object ZipEventsTableFix extends App {

  val t0 = System.currentTimeMillis
  val df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS")

  def zipEntry(conn: Connection,
    enrollmentUUID: String,
    actomKey: String,
    entryKey: String) = {
    val stmt = conn.prepareStatement(
        "delete from ActomEntryChangedEvent where uuid=?")
    var reds = 0
    var last_value: String = null
    val entries = sql"""
	  select *
	  from ActomEntryChangedEvent
	  where enrollmentUUID=$enrollmentUUID
	   and actomKey=$actomKey
	   and entryKey=$entryKey
	  order by ingestedAt
    """.foreach { rs =>
      count.incrementAndGet
      val uuid = rs.getString("uuid");
      val entryKey = rs.getString("entryValue")
      val entryValue = rs.getString("entryValue")
      val entryTime = rs.getTimestamp("ingestedAt")
      
      val entryTimeS = Try {df.format(entryTime)}.getOrElse("unparseable_date@"+System.currentTimeMillis)
      val isRedundant = (entryValue == last_value)
      if (isRedundant) {
        reds += 1
        stmt.setString(1, uuid)
        stmt.addBatch()
      }
      //println(s"[${if(isRedundant)"R"else"XXX";}][$entryTimeS][$entryKey]=[$entryValue]")
      last_value = entryValue
    }
    if (reds > 0) {
      fixed.addAndGet(reds)
      println(s"*** Removing [$reds] redundant records from ActomEntryChangedEvent [$enrollmentUUID, $actomKey, $entryKey]")
      stmt.executeBatch
    }
    stmt.close

  }

  def zipActom(enrollmentUUID: String, actomKey: String) = {
    println(s"Zipping [$enrollmentUUID][$actomKey]")
    val entryKeys = sql"""
	 select distinct(entryKey)
	 from ActomEntryChangedEvent
	 where enrollmentUUID=$enrollmentUUID
	   and actomKey=$actomKey
	 """.map[String]
    entryKeys foreach { key =>
      val conn = DataSources.connectionFactory()
      try {
        conn.setAutoCommit(false)
        zipEntry(conn, enrollmentUUID, actomKey, key)
        conn.commit
      } finally {
        conn.rollback
        conn.close
      }
    }
  }

  def zipEnrollment(enrollmentUUID: String) = {
    println(s"** Zipping [${enrollmentUUID}]")
    val t1 = System.currentTimeMillis
    println(s"** Now [${df.format(new Date(t1))}]")
    val elapsed = (t1 - t0) / 1000
    println(s"** Elaspsed [${elapsed}]")
    println(s"** Count [${count}] rs")
    val pace = count.get / elapsed
    println(s"** Pace  [$pace] rs/s")
    val actoms = sql"""
    select distinct(actomKey)
    from ActomEntryChangedEvent
    where enrollmentUUID=${enrollmentUUID}
    """.map[String]
    actoms foreach { actomKey => zipActom(enrollmentUUID, actomKey) }
  }

  println(s"==== BEGIN ====")
  val mainThread = Thread.currentThread();
  var count = new AtomicInteger
  var fixed = new AtomicInteger
  val enrollments = sql"""select distinct(enrollmentUUID)
  	from ActomEntryChangedEvent""".map[String]
  println(s"* Zipping ${enrollments.size} enrollments")
  //God bless parallel colletions #scala
  enrollments.par foreach zipEnrollment
  scala.sys.addShutdownHook {
    println(s"==== END ====")
    println(s"** Then [${df.format(new Date(t0))}]")
    println(s"** Now  [${df.format(new Date)}]")
    println(s"** Count [${count.get}] rs")
    println(s"** Fixed [${fixed.get}]")
    val t1 = System.currentTimeMillis
    val pace = ((1000) * count.get) / (t1 - t0)
    println(s"** Pace  [$pace] rs/s")    
    mainThread.join
  }
  println(s"==== === ====")
}