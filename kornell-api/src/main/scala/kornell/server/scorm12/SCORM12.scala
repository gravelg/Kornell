package kornell.server.scorm12

import java.util
import java.util.logging.Logger

import kornell.core.entity.{CourseClass, Enrollment, Person}
import kornell.core.scorm12.rte.{DMElement, RTE}

import scala.collection.JavaConverters._
import scala.collection.mutable.{Map => MMap}

object SCORM12 {
  val logger: Logger = Logger.getLogger("kornell.server.scorm12")

  def initialize(entries: util.Map[String, String], person: Person,
    enrollment: Enrollment,
    courseClass: CourseClass): util.Map[String, String] = dataModel.initialize(entries, person, enrollment, courseClass)

  def merged(mms: Seq[util.Map[String, String]]): MMap[String, String] = {
    val merged = MMap[String, String]()
    for {
      mm <- mms
      (k, v) <- mm.asScala
    } if (merged.contains(k))
      logger.finest(s"Map already contains key [${k}], ignoring value [${v}]")
    else merged.put(k, v)
    merged
  }

  implicit class Element(el: DMElement) {

    def initialize(entries: util.Map[String, String], person: Person,
      enrollment: Enrollment,
      courseClass: CourseClass): util.Map[String, String] =
      _initialize(entries, person, enrollment, courseClass).asJava

    def _initialize(entries: util.Map[String, String],
      person: Person,
      enrollment: Enrollment,
      courseClass: CourseClass): MMap[String, String] = {
      type Maps = List[util.Map[String, String]]
      val childDataModels = el.getChildren.asScala
      val kids: Maps = childDataModels
        .map { _.initialize(entries, person, enrollment, courseClass) }
        .toList
      val selfie = el.initializeMap(entries, person, enrollment, courseClass)
      val maps = kids ++ List(selfie)
      val result = merged(maps)
      result
    }
  }

  val dataModel: Element = RTE.root
}