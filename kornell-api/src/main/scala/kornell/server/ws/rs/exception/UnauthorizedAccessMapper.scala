package kornell.server.ws.rs.exception

import javax.ws.rs.core.Response
import javax.ws.rs.ext.{ExceptionMapper, Provider}
import kornell.core.error.exception.UnauthorizedAccessException

@Provider
class UnauthorizedAccessMapper extends ExceptionMapper[UnauthorizedAccessException] {
  override def toResponse(uae: UnauthorizedAccessException): Response =
    ExceptionMapperHelper.handleError(401, uae.getMessageKey)
}