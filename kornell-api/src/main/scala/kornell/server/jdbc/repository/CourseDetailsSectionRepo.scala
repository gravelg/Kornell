package kornell.server.jdbc.repository

import kornell.core.entity.CourseDetailsSection
import kornell.server.jdbc.SQL._

import scala.collection.JavaConverters._

class CourseDetailsSectionRepo(uuid: String) {

  val finder = sql"select * from CourseDetailsSection where uuid=$uuid"

  def get: CourseDetailsSection = finder.get[CourseDetailsSection]
  def first: Option[CourseDetailsSection] = finder.first[CourseDetailsSection]

  def update(courseDetailsSection: CourseDetailsSection): CourseDetailsSection = {
    sql"""
    | update CourseDetailsSection s
    | set s.text = ${courseDetailsSection.getText},
    | s.index = ${courseDetailsSection.getIndex},
    | s.title = ${courseDetailsSection.getTitle}
    | where s.uuid = ${courseDetailsSection.getUUID}""".executeUpdate

    courseDetailsSection
  }

  def delete: CourseDetailsSection = {
    val courseDetailsSection = get
    sql"""
      delete from CourseDetailsSection
      where uuid = ${uuid}""".executeUpdate

    val courseDetailsSections = CourseDetailsSectionsRepo.getForEntity(courseDetailsSection.getEntityUUID, courseDetailsSection.getEntityType).getCourseDetailsSections
    val indexed = courseDetailsSections.asScala.zipWithIndex
    for (i <- indexed) {
      val courseDetailsSection = i._1
      val index = i._2
      courseDetailsSection.setIndex(index)
      CourseDetailsSectionRepo(courseDetailsSection.getUUID).update(courseDetailsSection)
    }

    courseDetailsSection
  }
}

object CourseDetailsSectionRepo {
  def apply(uuid: String) = new CourseDetailsSectionRepo(uuid)
}
