package kornell.server.ws.rs.exception

import javax.ws.rs.core.Response
import javax.ws.rs.ext.{ExceptionMapper, Provider}
import kornell.core.error.exception.EntityConflictException

@Provider
class EntityConflictMapper extends ExceptionMapper[EntityConflictException] {
  override def toResponse(ece: EntityConflictException): Response =
    ExceptionMapperHelper.handleError(409, ece.getMessageKey)
}