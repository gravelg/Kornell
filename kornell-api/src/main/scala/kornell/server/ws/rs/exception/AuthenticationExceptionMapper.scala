package kornell.server.ws.rs.exception

import kornell.core.error.exception.AuthenticationException
import javax.ws.rs.ext.ExceptionMapper
import javax.ws.rs.ext.Provider
import javax.ws.rs.core.Response

@Provider
class AuthenticationExceptionMapper extends ExceptionMapper[AuthenticationException] {
  override def toResponse(authException: AuthenticationException): Response =
    ExceptionMapperHelper.handleError(403, authException.getMessageKey)
}