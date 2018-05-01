package kornell.server.ws.rs.reader
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import javax.ws.rs.ext.Provider
import kornell.core.event.EventFactory

@Provider
class EventsReader extends AutoBeanReader {
  val factory: EventFactory = AutoBeanFactorySource.create(classOf[EventFactory])

  override def getTypePrefix: String = EventFactory.PREFIX
  override def getAutoBeanFactory: EventFactory = factory
}