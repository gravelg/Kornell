package kornell.server.ws.rs.exception

import javax.ws.rs.core._
import javax.ws.rs.ext._
import kornell.core.error.exception.EntityNotFoundException

@Provider
class EntityNotFoundMapper extends ExceptionMapper[EntityNotFoundException] {
  override def toResponse(enf: EntityNotFoundException): Response =
    ExceptionMapperHelper.handleError(404, enf.getMessageKey)
}