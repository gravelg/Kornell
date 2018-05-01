package kornell.server.api

import java.util.Collections

import javax.ws.rs.core.Application
import kornell.server.ws.rs.exception._
import kornell.server.ws.rs.reader.{EntityReader, EventsReader, LOMReader, TOReader}
import kornell.server.ws.rs.writer.{AutoBeanWriter, BooleanWriter}

import scala.collection.JavaConverters.setAsJavaSetConverter

class KornellAPI extends Application {
  type ClassSet = Set[Class[_]]
  val readers: ClassSet = Set(classOf[EventsReader],
    classOf[TOReader],
    classOf[EntityReader],
    classOf[LOMReader])

  val writers: ClassSet = Set(classOf[AutoBeanWriter],
    classOf[BooleanWriter])

  val mappers: ClassSet = Set(classOf[EntityNotFoundMapper],
    classOf[EntityConflictMapper],
    classOf[UnauthorizedAccessMapper],
    classOf[ServerErrorMapper],
    classOf[KornellExceptionMapper],
    classOf[AuthenticationExceptionMapper])

  val resources: ClassSet = Set(classOf[RootResource],
    classOf[UserResource],
    classOf[PeopleResource],
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
    classOf[CertificatesDetailsResource],
    classOf[CacheResource],
    classOf[PostbackResource],
    classOf[TracksResource],
    classOf[TrackEnrollmentsResource],
    classOf[TrackItemsResource])

  override def getClasses =
    readers ++
      writers ++
      mappers ++
      resources asJava

  override def getSingletons = Collections.emptySet()
}
