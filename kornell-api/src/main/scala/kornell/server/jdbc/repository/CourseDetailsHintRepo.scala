package kornell.server.jdbc.repository

import java.sql.ResultSet

import kornell.server.jdbc.SQL._
import scala.collection.JavaConverters._
import kornell.core.entity.CourseDetailsHint

class CourseDetailsHintRepo(uuid: String) {

  val finder = sql"select * from CourseDetailsHint where uuid=$uuid"

  def get = finder.get[CourseDetailsHint]
  def first = finder.first[CourseDetailsHint]

  def update(courseDetailsHint: CourseDetailsHint): CourseDetailsHint = {
    sql"""
    | update CourseDetailsHint c
    | set c.title = ${courseDetailsHint.getTitle},
    | c.text = ${courseDetailsHint.getText},
    | c.index = ${courseDetailsHint.getIndex},
    | c.fontAwesomeClassName = ${courseDetailsHint.getFontAwesomeClassName}
    | where c.uuid = ${courseDetailsHint.getUUID}""".executeUpdate

    courseDetailsHint
  }

  def delete = {
    val courseDetailsHint = get
    sql"""
      delete from CourseDetailsHint
      where uuid = ${uuid}""".executeUpdate

    val courseDetailsHints = CourseDetailsHintsRepo.getForEntity(courseDetailsHint.getEntityUUID, courseDetailsHint.getEntityType).getCourseDetailsHints
    val indexed = courseDetailsHints.asScala.zipWithIndex
    for (i <- indexed) {
      val courseDetailsHint = i._1
      val index = i._2
      courseDetailsHint.setIndex(index)
      CourseDetailsHintRepo(courseDetailsHint.getUUID).update(courseDetailsHint)
    }

    courseDetailsHint
  }

}

object CourseDetailsHintRepo {
  def apply(uuid: String) = new CourseDetailsHintRepo(uuid)
}
