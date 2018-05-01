package kornell.server.ws.rs.reader

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import javax.ws.rs.ext.Provider
import kornell.core.entity.EntityFactory

@Provider
class EntityReader extends AutoBeanReader {
  val factory: EntityFactory = AutoBeanFactorySource.create(classOf[EntityFactory])

  override def getTypePrefix: String = EntityFactory.PREFIX
  override def getAutoBeanFactory: EntityFactory = factory
}