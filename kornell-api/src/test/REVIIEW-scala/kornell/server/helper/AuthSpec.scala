package kornell.server.helper

import kornell.server.authentication.ThreadLocalAuthenticator
import org.scalatest.BeforeAndAfter

trait AuthSpec {
  def assumeIdentity(personUUID: String) =
    ThreadLocalAuthenticator.setAuthenticatedPersonUUID(personUUID)

  def yeldIdentity() =
    ThreadLocalAuthenticator.clearAuthenticatedPersonUUID()

  def asIdentity[T](personUUID: String)(fun: => T):T =
    try {
      assumeIdentity(personUUID)
      fun
    } finally {
      yeldIdentity
    }
}