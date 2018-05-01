package kornell.server.util

import org.jboss.shrinkwrap.api.ShrinkWrap
import org.jboss.shrinkwrap.api.spec.WebArchive
import org.jboss.shrinkwrap.resolver.api.maven.archive.importer.MavenImporter
import org.wildfly.swarm.Swarm

object KornellSwarm extends App {
  val container = new Swarm()
  val deployment = ShrinkWrap.create(classOf[MavenImporter])
    .loadPomFromFile("pom.xml")
    .importBuildOutput()
    .as(classOf[WebArchive])

  container.start()
  container.deploy(deployment)
}