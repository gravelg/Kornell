package kornell.server.authentication

object ThreadLocalAuthenticator {
  val threadLocal = new ThreadLocal[String]

  def setAuthenticatedPersonUUID(personUUID: String) = {
    threadLocal.set(personUUID)
    personUUID
  }

  def getAuthenticatedPersonUUID() = Option(threadLocal.get)

  def clearAuthenticatedPersonUUID = threadLocal.set(null)
}
