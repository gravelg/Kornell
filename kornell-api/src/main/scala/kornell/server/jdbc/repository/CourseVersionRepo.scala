package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.core.entity.CourseVersion
import kornell.server.jdbc.SQL._
import kornell.core.to.CourseVersionTO
import kornell.core.to.CourseClassesTO
import kornell.server.repository.TOs
import kornell.core.entity.AuditedEntityType

class CourseVersionRepo(uuid: String) {

  val finder = sql"select * from CourseVersion where uuid=$uuid"

  def get = finder.get[CourseVersion]
  def first = finder.first[CourseVersion]
  
  def update(courseVersion: CourseVersion, institutionUUID: String): CourseVersion = {
    //get previous version
    val oldCourseVersion = CourseVersionRepo(courseVersion.getUUID).first.get
    
    sql"""
    | update CourseVersion c
    | set c.name = ${courseVersion.getName},
    | c.course_uuid = ${courseVersion.getCourseUUID}, 
    | c.versionCreatedAt = ${courseVersion.getVersionCreatedAt},
    | c.distributionPrefix = ${courseVersion.getDistributionPrefix},
    | c.disabled = ${courseVersion.isDisabled},
    | c.parentVersionUUID = ${courseVersion.getParentVersionUUID},
    | c.instanceCount = ${courseVersion.getInstanceCount},
    | c.label = ${courseVersion.getLabel},
    | c.thumbUrl = ${courseVersion.getThumbUrl}
    | where c.uuid = ${courseVersion.getUUID}""".executeUpdate
	    
    //log entity change
    EventsRepo.logEntityChange(institutionUUID, AuditedEntityType.courseVersion, courseVersion.getUUID, oldCourseVersion, courseVersion)
	    
    courseVersion
  }
  
  def delete = {    
    val courseVersion = get
    if(CourseClassesRepo.countByCourseVersion(uuid) == 0){
      sql"""
        delete from CourseVersion
        where uuid = ${uuid}""".executeUpdate
      courseVersion
    }
  }
  
  def getChildren(): List[CourseVersion] = {
    sql"""
    	select * from CourseVersion where parentVersionUUID = ${uuid}"""
    .map[CourseVersion](toCourseVersion)
  }
  
}

object CourseVersionRepo {
  def apply(uuid: String) = new CourseVersionRepo(uuid: String)
}