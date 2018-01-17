package kornell.server.ws.rs.reader

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import javax.ws.rs.ext.Provider
import kornell.core.entity.EntityFactory

@Provider
class EntityReader extends AutoBeanReader {
  val factory = AutoBeanFactorySource.create(classOf[EntityFactory])

  override def getTypePrefix = EntityFactory.PREFIX
  override def getAutoBeanFactory = factory
}