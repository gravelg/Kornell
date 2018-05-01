package kornell.server.ws.rs.exception

import javax.ws.rs.core.Response
import javax.ws.rs.ext.{ExceptionMapper, Provider}
import kornell.core.error.exception.AuthenticationException

@Provider
class AuthenticationExceptionMapper extends ExceptionMapper[AuthenticationException] {
  override def toResponse(authException: AuthenticationException): Response =
    ExceptionMapperHelper.handleError(403, authException.getMessageKey)
}