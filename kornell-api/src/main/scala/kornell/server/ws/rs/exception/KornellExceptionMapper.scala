package kornell.server.ws.rs.exception

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import javax.ws.rs.core.Response
import javax.ws.rs.ext.{ExceptionMapper, Provider}
import kornell.core.error.KornellErrorTO
import kornell.core.to.TOFactory
import kornell.server.util.KornellErr

@Provider
class KornellExceptionMapper extends ExceptionMapper[KornellErr] {

  val errorFactory: TOFactory = AutoBeanFactorySource.create(classOf[TOFactory])

  override def toResponse(ke: KornellErr): Response = {
    val errorTO = errorFactory.newKornellErrorTO.as
    errorTO.setMessageKey(ke.getMessageKey)
    Response
      .status(ke.getCode)
      .entity(errorTO)
      .header("Content-Type", KornellErrorTO.TYPE)
      .build()
  }
}