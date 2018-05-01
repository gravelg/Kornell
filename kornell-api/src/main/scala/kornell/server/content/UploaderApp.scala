package kornell.server.content

import javax.ws.rs.core.Application
import java.util.Collections

class UploaderApp extends Application {
  override def getClasses = Collections.singleton(classOf[UploaderResource])
  override def getSingletons = Collections.emptySet()
}
