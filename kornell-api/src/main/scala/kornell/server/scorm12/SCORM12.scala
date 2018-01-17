package kornell.server.scorm12

import kornell.core.scorm12.rte.DMElement
import kornell.core.scorm12.rte.RTE
import java.util.{ Map => JMap }
import scala.collection.mutable.{ Map => MMap }
import scala.collection.JavaConverters._
import java.util.logging.Logger
import java.util.HashMap
import kornell.core.scorm12._
import kornell.core.entity.Person
import kornell.core.entity.Enrollment
import kornell.core.entity.CourseClass

object SCORM12 {
  val logger = Logger.getLogger("kornell.server.scorm12")

  def initialize(entries: JMap[String, String], person: Person,
    enrollment: Enrollment,
    courseClass: CourseClass) = dataModel.initialize(entries, person, enrollment, courseClass)

  def merged(mms: Seq[JMap[String, String]]): MMap[String, String] = {
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

    def initialize(entries: JMap[String, String], person: Person,
      enrollment: Enrollment,
      courseClass: CourseClass): JMap[String, String] =
      _initialize(entries, person, enrollment, courseClass).asJava

    def _initialize(entries: JMap[String, String],
      person: Person,
      enrollment: Enrollment,
      courseClass: CourseClass): MMap[String, String] = {
      type Maps = List[JMap[String, String]]
      val childDataModels = el.getChildren().asScala
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