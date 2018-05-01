package kornell.server.util

object UserLocale {
  val threadLocal = new ThreadLocal[String]

  def setLocale(locale: String): String = {
    threadLocal.set(locale)
    locale
  }

  def getLocale = Option(threadLocal.get)

  def clearLocale(): Unit = threadLocal.remove()
}