package kornell.server.api

import java.util.Collections
import scala.collection.JavaConverters.setAsJavaSetConverter
import javax.ws.rs.core.Application
import kornell.server.ws.rs.AutoBeanWriter
import kornell.server.ws.rs.TOReader
import kornell.server.ws.rs.exception.EntityConflictMapper
import kornell.server.ws.rs.exception.EntityNotFoundMapper
import kornell.server.ws.rs.exception.KornellExceptionMapper
import kornell.server.ws.rs.exception.ServerErrorMapper
import kornell.server.ws.rs.exception.UnauthorizedAccessMapper
import kornell.server.ws.rs.reader.EntityReader
import kornell.server.ws.rs.reader.EventsReader
import kornell.server.ws.rs.reader.LOMReader
import kornell.server.ws.rs.writer.BooleanWriter
import kornell.server.ws.rs.exception.AuthenticationExceptionMapper

class KornellAPI extends Application {
  type ClassSet = Set[Class[_]]
  val readers:ClassSet = Set(classOf[EventsReader],
    classOf[TOReader],
    classOf[EntityReader],
    classOf[LOMReader])

  val writers:ClassSet = Set(classOf[AutoBeanWriter],
    classOf[BooleanWriter])
    
  val mappers:ClassSet = Set(classOf[EntityNotFoundMapper],
    classOf[EntityConflictMapper], 
    classOf[UnauthorizedAccessMapper],
    classOf[ServerErrorMapper],
    classOf[KornellExceptionMapper],
    classOf[AuthenticationExceptionMapper])
    
  val resources:ClassSet = Set(classOf[RootResource],
    classOf[UserResource],
    classOf[PeopleResource],
    classOf[UserResource],
    classOf[CoursesResource],
    classOf[CourseVersionsResource],
    classOf[CourseClassesResource],
    classOf[InstitutionsResource],
    classOf[ReportResource],
    classOf[EnrollmentsResource],
    classOf[EventsResource],
    classOf[ActomResource],
    classOf[ChatThreadsResource],
    classOf[HealthCheckResource],
    classOf[TokenResource],
    classOf[ContentRepositoriesResource],
    classOf[CourseDetailsHintsResource],
    classOf[CourseDetailsSectionsResource],
    classOf[CourseDetailsLibrariesResource],
    classOf[CertificateDetailsResource],
    classOf[PostbackResource]
  )
    
  override def getClasses() = 
    readers ++ 
    writers ++
    mappers ++ 
    resources asJava

  override def getSingletons() = Collections.emptySet()
}