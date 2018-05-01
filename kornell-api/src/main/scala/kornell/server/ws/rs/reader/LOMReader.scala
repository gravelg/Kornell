package kornell.server.ws.rs.reader

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import javax.ws.rs.ext.Provider
import kornell.core.lom.LOMFactory

@Provider
class LOMReader extends AutoBeanReader {
  val factory: LOMFactory = AutoBeanFactorySource.create(classOf[LOMFactory])

  override def getTypePrefix: String = LOMFactory.PREFIX
  override def getAutoBeanFactory: LOMFactory = factory
}