package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.core.entity.Course
import kornell.core.entity.Course
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.core.entity.CourseVersion
import kornell.core.to.CourseVersionTO
import kornell.core.to.CourseVersionsTO
import kornell.core.util.UUID
import java.util.Date
import kornell.core.error.exception.EntityConflictException
import kornell.core.entity.AuditedEntityType

object CourseVersionsRepo {
  
  def create(courseVersion: CourseVersion, institutionUUID: String): CourseVersion = {  
    val courseVersionExists = sql"""
	    select count(*) from CourseVersion where course_uuid = ${courseVersion.getCourseUUID} and name = ${courseVersion.getName}
	    """.first[String].get
    if (courseVersionExists == "0") {  
	    if (courseVersion.getUUID == null){
	      courseVersion.setUUID(UUID.random)
	    }
		courseVersion.setVersionCreatedAt(new Date());
		
	    sql"""
	    | insert into CourseVersion (uuid,name,course_uuid,versionCreatedAt,distributionPrefix,contentSpec,disabled,thumbUrl) 
	    | values(
	    | ${courseVersion.getUUID},
	    | ${courseVersion.getName},
	    | ${courseVersion.getCourseUUID}, 
	    | ${courseVersion.getVersionCreatedAt},
	    | ${courseVersion.getDistributionPrefix},
	    | ${courseVersion.getContentSpec.toString},
	    | ${courseVersion.isDisabled},
	    | ${courseVersion.getThumbUrl})""".executeUpdate
	    
	    //log creation event
	    EventsRepo.logEntityChange(institutionUUID, AuditedEntityType.courseVersion, courseVersion.getUUID, null, courseVersion)
	    
	    courseVersion
    } else {
      throw new EntityConflictException("courseVersionAlreadyExists")
    }
  }  
  
  def byInstitution(institutionUUID: String, searchTerm: String, pageSize: Int, pageNumber: Int) = {
    val resultOffset = (pageNumber.max(1) - 1) * pageSize
    val filteredSearchTerm = '%' + Option(searchTerm).getOrElse("") + '%'
    
    val courseVersionsTO = newCourseVersionsTO(sql"""
	  	select cv.* from CourseVersion cv
		join Course c on cv.course_uuid = c.uuid
		where c.institutionUUID = $institutionUUID
		and cv.name like ${filteredSearchTerm}
		order by c.title, cv.versionCreatedAt desc limit ${resultOffset}, ${pageSize}
	  """.map[CourseVersion](toCourseVersion))
	  courseVersionsTO.setPageSize(pageSize)
	  courseVersionsTO.setPageNumber(pageNumber.max(1))
	  courseVersionsTO.setCount({
	    sql"""select count(cv.uuid) from CourseVersion cv
	    	join Course c on cv.course_uuid = c.uuid
			where c.institutionUUID = $institutionUUID
	    	""".first[String].get.toInt
	  })
	  courseVersionsTO.setSearchCount({
    	  if (searchTerm == "")
    		  0
		  else
		    sql"""select count(cv.uuid) from CourseVersion cv
	    	join Course c on cv.course_uuid = c.uuid
			where c.institutionUUID = $institutionUUID
			and cv.name like ${filteredSearchTerm}
	    	""".first[String].get.toInt
	  })
	  courseVersionsTO
  }
  
  def byCourse(courseUUID: String) = newCourseVersionsTO(
    sql"""
	  	select cv.* from CourseVersion cv
		join Course c on cv.course_uuid = c.uuid
		where cv.disabled = 0 and c.uuid = $courseUUID
		order by cv.versionCreatedAt desc
	  """.map[CourseVersion])

  def byParentVersionUUID(parentVersionUUID: String) = sql"""
    select * from CourseVersion where parentVersionUUID = ${parentVersionUUID}
  """.map[CourseVersion]

  
  def byEnrollment(enrollmentUUID: String) = {
    sql"""
	    | select cv.* from 
  		| CourseVersion cv
  		| join Enrollment e on e.courseVersionUUID = cv.uuid
  		| where e.uuid = ${enrollmentUUID}
	    | and cv.disabled = 0
	    """.first[CourseVersion](toCourseVersion)
  }
  
  
}