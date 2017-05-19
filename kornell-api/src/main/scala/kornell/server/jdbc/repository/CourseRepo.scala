package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.core.entity.Course
import kornell.core.entity.Person
import kornell.server.repository.Entities.newCourse
import kornell.server.jdbc.SQL._ 
import kornell.core.entity.AuditedEntityType
import kornell.core.util.UUID
import kornell.core.entity.CourseDetailsEntityType
import scala.collection.JavaConverters._
import kornell.server.service.S3Service
import kornell.core.util.StringUtils._
import kornell.server.service.AssetService
import kornell.core.util.StringUtils
import kornell.core.entity.EntityState
import kornell.core.error.exception.EntityConflictException


class CourseRepo(uuid: String) {

  val finder = sql"select * from Course where uuid = $uuid and state <> ${EntityState.deleted.toString}"

  def get = finder.get[Course]
  def first = finder.first[Course]
  
  def update(course: Course): Course = {    
    //get previous course
    val oldCourse = CourseRepo(course.getUUID).first.get
    
    val courseVersionExists = sql"""
      select count(*) from Course
      where institutionUUID = ${course.getInstitutionUUID} 
      and name = ${course.getName} 
      and uuid <> ${course.getUUID}
      and state <> ${EntityState.deleted.toString}
    """.first[String].get
    if (courseVersionExists == "0") {
      sql"""
      | update Course c
      | set c.code = ${course.getCode},
      | c.name = ${course.getName}, 
      | c.description = ${course.getDescription},
      | c.infoJson = ${course.getInfoJson},
      | c.state = ${course.getState.toString},
      | c.institutionUUID = ${course.getInstitutionUUID},
      | c.childCourse = ${course.isChildCourse},
      | c.thumbUrl = ${course.getThumbUrl},
      | c.contentSpec = ${course.getContentSpec.toString}
      | where c.uuid = ${course.getUUID}""".executeUpdate
  	    
      //log entity change
      EventsRepo.logEntityChange(course.getInstitutionUUID, AuditedEntityType.course, course.getUUID, oldCourse, course)
  	        
      course
    } else {
      throw new EntityConflictException("courseAlreadyExists")
    }   
  }
  
  def delete = {    
    val course = get
    if(CourseVersionsRepo.countByCourse(uuid) == 0){
      sql"""
        update Course
        set state = ${EntityState.deleted.toString} 
        where uuid = ${uuid}
  		""".executeUpdate
      course
    }
  }
  
  def copy = {    
    val course = CourseRepo(uuid).first.get
    val sourceCourseUUID = course.getUUID
    val targetCourseUUID = UUID.random
    
    //copy course
    course.setUUID(targetCourseUUID)
    course.setCode(targetCourseUUID)
    course.setName(course.getName + " (2)")
    if(StringUtils.isSome(course.getThumbUrl)){
      course.setThumbUrl(course.getThumbUrl.replace(sourceCourseUUID, targetCourseUUID))
    }
    CoursesRepo.create(course)    
    
    AssetService.copyAssets(course.getInstitutionUUID, CourseDetailsEntityType.COURSE, sourceCourseUUID, targetCourseUUID, course.getThumbUrl)
	        
    course
  }

}

object CourseRepo {
  def apply(uuid: String) = new CourseRepo(uuid)
}