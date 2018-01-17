package kornell.server.jdbc

import javax.servlet.annotation.WebListener
import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent

@WebListener
class MigrationListener extends ServletContextListener {
  override def contextInitialized(e: ServletContextEvent): Unit = Migration()

  override def contextDestroyed(e: ServletContextEvent): Unit = {}
}
