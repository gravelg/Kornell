package kornell.server.ws.rs.reader

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import javax.ws.rs.ext.Provider
import kornell.core.to.TOFactory

@Provider
class TOReader extends AutoBeanReader {
  val factory: TOFactory = AutoBeanFactorySource.create(classOf[TOFactory])

  override def getTypePrefix: String = TOFactory.PREFIX
  override def getAutoBeanFactory: TOFactory = factory
}