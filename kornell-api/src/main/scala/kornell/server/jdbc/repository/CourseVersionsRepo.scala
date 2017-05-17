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
import kornell.core.util.StringUtils
import kornell.server.jdbc.PreparedStmt

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
	    | insert into CourseVersion (uuid,name,course_uuid,versionCreatedAt,distributionPrefix,disabled,thumbUrl) 
	    | values(
	    | ${courseVersion.getUUID},
	    | ${courseVersion.getName},
	    | ${courseVersion.getCourseUUID}, 
	    | ${courseVersion.getVersionCreatedAt},
	    | ${courseVersion.getDistributionPrefix},
	    | ${courseVersion.isDisabled},
	    | ${courseVersion.getThumbUrl})""".executeUpdate
	    
	    //log creation event
	    EventsRepo.logEntityChange(institutionUUID, AuditedEntityType.courseVersion, courseVersion.getUUID, null, courseVersion)
	    
	    courseVersion
    } else {
      throw new EntityConflictException("courseVersionAlreadyExists")
    }
  }  
  
  def getCourseVersionTO(institutionUUID: String, courseVersionUUID: String) = {
    val courseVersionsTO = byInstitution(institutionUUID, "", Int.MaxValue, 1, "cv.name", true, null, courseVersionUUID)
    if (courseVersionsTO.getCourseVersionTOs.size > 0) {
      courseVersionsTO.getCourseVersionTOs.get(0)
    }
  }
  
  def byInstitution(institutionUUID: String, searchTerm: String, pageSize: Int, pageNumber: Int, orderBy: String, asc: Boolean, courseUUID: String = null, courseVersionUUID: String = null) = {
    val resultOffset = (pageNumber.max(1) - 1) * pageSize
    val filteredSearchTerm = '%' + Option(searchTerm).getOrElse("") + '%'
    val orderColumn = if(orderBy != null &&  !orderBy.contains(";")) orderBy else "cv.name"
    val order = orderColumn + (if(asc) " asc" else " desc")
    
    val courseVersionsTO = newCourseVersionsTO(new PreparedStmt(s"""
      select
      cv.uuid as courseVersionUUID,
      cv.name as courseVersionName,
      cv.course_uuid as courseUUID,
      cv.versionCreatedAt as versionCreatedAt,
      cv.distributionPrefix as distributionPrefix,
      cv.disabled as courseVersionDisabled,
      cv.parentVersionUUID as parentVersionUUID,
      cv.instanceCount as instanceCount,
      cv.label as label,
      cv.thumbUrl as courseVersionThumbUrl,
      c.uuid as courseUUID,
      c.code as courseCode,
      c.title as courseTitle,
      c.description as courseDescription,
      c.contentSpec as contentSpec,
      c.infoJson as infoJson,
      c.institutionUUID as institutionUUID,
      c.childCourse as childCourse,
      c.thumbUrl as courseThumbUrl
      from CourseVersion cv
  		join Course c on cv.course_uuid = c.uuid
  		where c.institutionUUID = '$institutionUUID'
  		and cv.name like '${filteredSearchTerm}'
      and (cv.uuid = '${courseVersionUUID}'  or ${StringUtils.isNone(courseVersionUUID)})
      and (c.uuid = '${courseUUID}'  or ${StringUtils.isNone(courseUUID)})
  		order by ${order}, c.title, cv.versionCreatedAt desc limit ${resultOffset}, ${pageSize}
	  """, List[String]()).map[CourseVersionTO](toCourseVersionTO))
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
	  
    bindCourseClassesCounts(courseVersionsTO)
	  courseVersionsTO
  }
  
  private def bindCourseClassesCounts(courseVersionsTO: CourseVersionsTO) = {
    val versions = courseVersionsTO.getCourseVersionTOs.asScala
    versions.foreach(cv => cv.setCourseClassesCount(CourseClassesRepo.countByCourseVersion(cv.getCourseVersion.getUUID)))
    courseVersionsTO.setCourseVersionTOs(versions.asJava)
    courseVersionsTO
  }

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
  
  def countByCourse(courseUUID: String) = 
    sql"""select count(*) 
      from CourseVersion cv 
      where cv.course_uuid = ${courseUUID} 
    """.first[String].get.toInt
  
  
}