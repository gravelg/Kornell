package kornell.server.ws.rs.reader
import kornell.core.event.EventFactory
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.event.EventFactory
import javax.ws.rs.ext.Provider

@Provider
class EventsReader extends AutoBeanReader {
  val factory = AutoBeanFactorySource.create(classOf[EventFactory])

  override def getTypePrefix = EventFactory.PREFIX
  override def getAutoBeanFactory = factory
}