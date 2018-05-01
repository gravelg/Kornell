package kornell.server.content

import javax.ws.rs.core.Application
import java.util.Collections

class RepositoryApp extends Application {
  override def getClasses = Collections.singleton(classOf[RepositoryResource])
  override def getSingletons = Collections.emptySet()
}
