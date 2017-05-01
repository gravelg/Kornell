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
  
  def getWithCourse: CourseVersionTO = {
    sql"""
    | select 
    | cv.uuid as courseVersionUUID,
    | cv.name as courseVersionName,
    | cv.course_uuid as courseUUID,
    | cv.versionCreatedAt as versionCreatedAt,
    | cv.distributionPrefix as distributionPrefix,
    | cv.contentSpec as contentSpec,
    | cv.disabled as courseVersionDisabled,
    | cv.parentVersionUUID as parentVersionUUID,
    | cv.instanceCount as instanceCount,
    | cv.label as label,
    | cv.thumbUrl as courseVersionThumbUrl,
    | c.uuid as courseUUID,
    | c.code as courseCode,
    | c.title as courseTitle,
    | c.description as courseDescription,
    | c.infoJson as infoJson,
    | c.institutionUUID as institutionUUID,
    | c.childCourse as childCourse,
    | c.thumbUrl as courseThumbUrl
    | from CourseVersion cv 
    | join Course c on cv.course_uuid = c.uuid
    | where cv.uuid = ${uuid}""".map[CourseVersionTO](toCourseVersionTO).head
  }
  
  def update(courseVersion: CourseVersion, institutionUUID: String): CourseVersion = {
    //get previous version
    val oldCourseVersion = CourseVersionRepo(courseVersion.getUUID).first.get
    
    sql"""
    | update CourseVersion c
    | set c.name = ${courseVersion.getName},
    | c.course_uuid = ${courseVersion.getCourseUUID}, 
    | c.versionCreatedAt = ${courseVersion.getVersionCreatedAt},
    | c.distributionPrefix = ${courseVersion.getDistributionPrefix},
    | c.contentSpec = ${courseVersion.getContentSpec.toString},
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
  
  def getChildren(): List[CourseVersion] = {
    sql"""
    	select * from CourseVersion where parentVersionUUID = ${uuid}"""
    .map[CourseVersion](toCourseVersion)
  }
  
}

object CourseVersionRepo {
  def apply(uuid: String) = new CourseVersionRepo(uuid: String)
}