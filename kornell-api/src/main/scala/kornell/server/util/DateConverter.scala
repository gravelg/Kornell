package kornell.server.util

import java.util.Date
import org.joda.time.DateTimeZone

object DateConverter {
  val threadLocal = new ThreadLocal[String]

  def setTimeZone(timeZone: String): String = {
    threadLocal.set(timeZone)
    timeZone
  }

  def getTimeZone = Option(threadLocal.get)

  def convertDate(date: Date): Date = {
    Option(date) match {
      case Some(s) => {
        val st = s.getTime
        new Date(st - DateTimeZone.forID(threadLocal.get).getStandardOffset(st))
      }
      case None => null
    }
  }

  def clearTimeZone(): Unit = threadLocal.set(null)
}