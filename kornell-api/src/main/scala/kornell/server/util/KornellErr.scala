package kornell.server.util

class KornellErr(code: Int, messageKey: String) extends Err {

  def getCode: Int = code
  def getMessageKey: String = messageKey
}

object AccessDeniedErr {
  def apply() = new KornellErr(401, "accessDenied")
}