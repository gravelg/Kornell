package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.core.entity.Course
import kornell.core.entity.Person
import kornell.server.repository.Entities.newCourse
import kornell.server.jdbc.SQL._ 
import kornell.core.entity.AuditedEntityType


class CourseRepo(uuid: String) {

  val finder = sql"select * from Course where uuid=$uuid"

  def get = finder.get[Course]
  def first = finder.first[Course]
  
  def update(course: Course): Course = {    
    //get previous version
    val oldCourse = CourseRepo(course.getUUID).first.get

    sql"""
    | update Course c
    | set c.code = ${course.getCode},
    | c.title = ${course.getTitle}, 
    | c.description = ${course.getDescription},
    | c.infoJson = ${course.getInfoJson},
    | c.institutionUUID = ${course.getInstitutionUUID},
    | c.childCourse = ${course.isChildCourse},
    | c.thumbUrl = ${course.getThumbUrl},
    | c.contentSpec = ${course.getContentSpec}
    | where c.uuid = ${course.getUUID}""".executeUpdate
	    
    //log entity change
    EventsRepo.logEntityChange(course.getInstitutionUUID, AuditedEntityType.course, course.getUUID, oldCourse, course)
	        
    course
  }

}

object CourseRepo {
  def apply(uuid: String) = new CourseRepo(uuid)
}