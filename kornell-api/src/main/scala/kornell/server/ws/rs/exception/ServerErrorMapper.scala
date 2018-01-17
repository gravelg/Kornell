package kornell.server.ws.rs.exception

import javax.ws.rs.core._
import javax.ws.rs.ext._
import kornell.core.error.exception.ServerErrorException

@Provider
class ServerErrorMapper extends ExceptionMapper[ServerErrorException] {
  override def toResponse(see: ServerErrorException): Response = {
    if (see.getCause != null)
      ExceptionMapperHelper.handleError(500, see.getMessageKey, see.getCause.getMessage)
    else
      ExceptionMapperHelper.handleError(500, see.getMessageKey)
  }

}