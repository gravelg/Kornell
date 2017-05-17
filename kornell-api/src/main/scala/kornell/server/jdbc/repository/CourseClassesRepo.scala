package kornell.server.jdbc.repository

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.bufferAsJavaListConverter
import scala.collection.JavaConverters.seqAsJavaListConverter
import kornell.core.entity.CourseClass
import kornell.core.entity.Person
import kornell.core.entity.Role
import kornell.core.entity.RoleCategory
import kornell.core.entity.RoleType
import kornell.core.to.CourseClassTO
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.repository.TOs
import kornell.core.entity.Roles
import kornell.core.util.UUID
import java.util.Date
import kornell.core.entity.CourseClassState
import java.sql.ResultSet
import kornell.core.error.exception.EntityConflictException
import kornell.core.error.exception.EntityNotFoundException
import kornell.core.util.StringUtils
import kornell.core.entity.AuditedEntityType
import kornell.core.to.CourseClassesTO
import kornell.core.entity.CourseVersion
import com.sun.xml.internal.bind.v2.TODO
import kornell.core.entity.Course
import java.util.ArrayList
import kornell.core.entity.CourseDetailsEntityType
import scala.collection.JavaConverters._
import kornell.core.entity.CourseDetailsSection
import scala.collection.mutable.Buffer
import kornell.core.entity.AssetEntity
import kornell.core.entity.CourseDetailsHint
import kornell.core.entity.CourseDetailsLibrary
import kornell.server.jdbc.PreparedStmt

class CourseClassesRepo {
}

object CourseClassesRepo {

  def apply(uuid: String) = CourseClassRepo(uuid)

  def create(courseClass: CourseClass): CourseClass = {
    val courseClassExists = sql"""
	    select count(*) from CourseClass where courseVersion_uuid = ${courseClass.getCourseVersionUUID} and name = ${courseClass.getName}
	    """.first[String].get
    if (courseClassExists == "0") {
      if (courseClass.getUUID == null) {
        courseClass.setUUID(UUID.random)
      }
      sql""" 
	    	insert into CourseClass(uuid,
                name,
                courseVersion_uuid,
                institution_uuid,
                publicClass,
                requiredScore,
                overrideEnrollments,
                invisible,
                maxEnrollments,
                createdAt,
                createdBy,
                registrationType,
                institutionRegistrationPrefixUUID, 
                courseClassChatEnabled, 
                chatDockEnabled, 
                allowBatchCancellation, 
                tutorChatEnabled,
                approveEnrollmentsAutomatically,
                startDate,
                pagseguroId,
                thumbUrl)
	    	values(${courseClass.getUUID},
	             ${courseClass.getName},
	             ${courseClass.getCourseVersionUUID},
	             ${courseClass.getInstitutionUUID},
	             ${courseClass.isPublicClass},
	             ${courseClass.getRequiredScore},
	             ${courseClass.isOverrideEnrollments},
	             ${courseClass.isInvisible},
	             ${courseClass.getMaxEnrollments},
	             ${new Date()},
	             ${courseClass.getCreatedBy},
	             ${courseClass.getRegistrationType.toString},
	             ${courseClass.getInstitutionRegistrationPrefixUUID},
	             ${courseClass.isCourseClassChatEnabled},
	             ${courseClass.isChatDockEnabled},
	             ${courseClass.isAllowBatchCancellation},
	             ${courseClass.isTutorChatEnabled},
	             ${courseClass.isApproveEnrollmentsAutomatically},
               ${courseClass.getStartDate},
               ${courseClass.getPagseguroId},
               ${courseClass.getThumbUrl}
               )
	    """.executeUpdate
      ChatThreadsRepo.addParticipantsToCourseClassThread(courseClass)

      //log creation event
      EventsRepo.logEntityChange(courseClass.getInstitutionUUID, AuditedEntityType.courseClass, courseClass.getUUID, null, courseClass)
      courseClass
    } else {
      throw new EntityConflictException("courseClassAlreadyExists")
    }
  }

  private def getAllClassesByInstitution(institutionUUID: String): kornell.core.to.CourseClassesTO =
    getAllClassesByInstitutionPaged(institutionUUID, "", Int.MaxValue, 1, "cc.name", true, "", null, null)

  def getCourseClassTO(institutionUUID: String, courseClassUUID: String) = {
    val courseClassesTO = getAllClassesByInstitutionPaged(institutionUUID, "", Int.MaxValue, 1, "cc.name", true, "", null, courseClassUUID)
    if (courseClassesTO.getCourseClasses.size > 0) {
      courseClassesTO.getCourseClasses.get(0)
    }
  }

  def getAllClassesByInstitutionPaged(institutionUUID: String, searchTerm: String, pageSize: Int, pageNumber: Int, orderBy: String, asc: Boolean, adminUUID: String, courseVersionUUID: String, courseClassUUID: String): kornell.core.to.CourseClassesTO = {
    val resultOffset = (pageNumber.max(1) - 1) * pageSize
    val filteredSearchTerm = '%' + Option(searchTerm).getOrElse("") + '%'
    val orderColumn = if(orderBy != null && !orderBy.contains(";")) orderBy else "cc.name"
    val orderMod =  (if(asc) " asc" else " desc")
    val order = orderColumn + orderMod + (if(orderColumn.contains("cc.state")) (", cc.publicClass desc, cc.invisible desc ") else "")
    println(order)

    val courseClassesTO = TOs.newCourseClassesTO(
      new PreparedStmt(s"""
			select     
				c.uuid as courseUUID, 
			    c.code,
			    c.title, 
			    c.description,
    			c.contentSpec as contentSpec,
			    c.infoJson,
      		c.childCourse,
          c.thumbUrl as courseThumbUrl,
			    cv.uuid as courseVersionUUID,
			    cv.name as courseVersionName,
			    cv.versionCreatedAt as versionCreatedAt,
		  		cv.distributionPrefix as distributionPrefix,
    			cv.disabled as disabled,
    			cv.parentVersionUUID as parentVersionUUID,
    			cv.instanceCount as instanceCount,
    			cv.label as label,
          cv.thumbUrl as courseVersionThumbUrl,
			    cc.uuid as courseClassUUID,
			    cc.name as courseClassName,
			    cc.institution_uuid as institutionUUID,
		  		cc.requiredScore as requiredScore,
		  		cc.publicClass as publicClass,
    			cc.overrideEnrollments as overrideEnrollments,
    			cc.invisible as invisible,
	  		  cc.maxEnrollments as maxEnrollments,
    			cc.createdAt as createdAt,
    			cc.createdBy as createdBy,
    			cc.state,
		  		cc.registrationType as registrationType,
		  		cc.institutionRegistrationPrefixUUID as institutionRegistrationPrefixUUID, 
		  		cc.courseClassChatEnabled as courseClassChatEnabled, 
		  		cc.chatDockEnabled as chatDockEnabled, 
		  		cc.allowBatchCancellation as allowBatchCancellation, 
		  		cc.tutorChatEnabled as tutorChatEnabled, 
		  		cc.approveEnrollmentsAutomatically as approveEnrollmentsAutomatically,
          cc.pagseguroId as pagseguroId,
          cc.thumbUrl as courseClassThumbUrl,
      		irp.name as institutionRegistrationPrefixName
			from Course c
				join CourseVersion cv on cv.course_uuid = c.uuid
				join CourseClass cc on cc.courseVersion_uuid = cv.uuid and cc.institution_uuid = '${institutionUUID}'
			    left join InstitutionRegistrationPrefix irp on irp.uuid = cc.institutionRegistrationPrefixUUID
      	  	where cc.state <> '${CourseClassState.deleted.toString}' and
      	  	    (cc.courseVersion_uuid = '${courseVersionUUID}' or ${StringUtils.isNone(courseVersionUUID)}) and
      	  	    (cc.uuid = '${courseClassUUID}' or ${StringUtils.isNone(courseClassUUID)}) and
		    	cc.institution_uuid = '${institutionUUID}' and
	            (cv.name like '${filteredSearchTerm}' or cc.name like '${filteredSearchTerm}') and 
	            (${StringUtils.isNone(adminUUID)} or
				(select count(*) from Role r where person_uuid = '${adminUUID}' and (
					(r.role = '${RoleType.platformAdmin.toString}' and r.institution_uuid = '${institutionUUID}') or 
					(r.role = '${RoleType.institutionAdmin.toString}' and r.institution_uuid = '${institutionUUID}') or 
				( (r.role = '${RoleType.courseClassAdmin.toString}' or r.role = '${RoleType.observer.toString}' or r.role = '${RoleType.tutor.toString}') and r.course_class_uuid = cc.uuid)
			)) > 0)
      	  	order by ${order}, cc.state, c.title, cv.versionCreatedAt desc, cc.name limit ${resultOffset}, ${pageSize};
		""", List[String]()).map[CourseClassTO](toCourseClassTO))
    courseClassesTO.setCount(
      sql"""select count(cc.uuid) from CourseClass cc where cc.state <> ${CourseClassState.deleted.toString} and (${StringUtils.isSome(adminUUID)} and
					(select count(*) from Role r where person_uuid = ${adminUUID} and (
						(r.role = ${RoleType.platformAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
						(r.role = ${RoleType.institutionAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
						( (r.role = ${RoleType.courseClassAdmin.toString} or r.role = ${RoleType.observer.toString} or r.role = ${RoleType.tutor.toString}) and r.course_class_uuid = cc.uuid)
					)) > 0)
      	  	    and (cc.courseVersion_uuid = ${courseVersionUUID} or ${StringUtils.isNone(courseVersionUUID)})
      	  	    and (cc.uuid = ${courseClassUUID} or ${StringUtils.isNone(courseClassUUID)})
		    	and cc.institution_uuid = ${institutionUUID}""".first[String].get.toInt)
    courseClassesTO.setPageSize(pageSize)
    courseClassesTO.setPageNumber(pageNumber.max(1))
    courseClassesTO.setSearchCount({
      if (searchTerm == "")
        0
      else
        sql"""select count(cc.uuid) from CourseClass cc 
		    	join CourseVersion cv on cc.courseVersion_uuid = cv.uuid
		    	where cc.state <> ${CourseClassState.deleted.toString} and
            	(cv.name like ${filteredSearchTerm}
            	or cc.name like ${filteredSearchTerm}) and (${StringUtils.isSome(adminUUID)} and
				(select count(*) from Role r where person_uuid = ${adminUUID} and (
					(r.role = ${RoleType.platformAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
					(r.role = ${RoleType.institutionAdmin.toString} and r.institution_uuid = ${institutionUUID}) or 
					( (r.role = ${RoleType.courseClassAdmin.toString} or r.role = ${RoleType.observer.toString} or r.role = ${RoleType.tutor.toString}) and r.course_class_uuid = cc.uuid)
				)) > 0)
      	  	    and (cc.courseVersion_uuid = ${courseVersionUUID} or ${StringUtils.isNone(courseVersionUUID)})
      	  	    and (cc.uuid = ${courseClassUUID} or ${StringUtils.isNone(courseClassUUID)})
            	and cc.institution_uuid = ${institutionUUID}""".first[String].get.toInt
    })
    
    if(courseClassUUID != null && courseClassesTO.getCourseClasses.size == 1){
      bindClassroomDetails(courseClassesTO.getCourseClasses.get(0));
    } else {
      bindEnrollmentCounts(courseClassesTO)
    }
    
    courseClassesTO
  }
  
  private def bindEnrollmentCounts(courseClassesTO: CourseClassesTO) = {
    val classes = courseClassesTO.getCourseClasses().asScala
    classes.foreach(cc => cc.setEnrollmentCount(EnrollmentsRepo.countByCourseClass(cc.getCourseClass.getUUID)))
    courseClassesTO.setCourseClasses(classes.asJava)
    courseClassesTO
  }
  
  def bindClassroomDetails(courseClassTO: CourseClassTO){
    val courseUUID = courseClassTO.getCourseVersionTO.getCourseTO.getCourse.getUUID
    val courseVersionUUID = courseClassTO.getCourseVersionTO.getCourseVersion.getUUID
    val courseClassUUID = courseClassTO.getCourseClass.getUUID
    
    courseClassTO.setCourseDetailsSections(mergeSections(courseUUID, courseVersionUUID, courseClassUUID))
    courseClassTO.setCourseDetailsHints(mergeHints(courseUUID, courseVersionUUID, courseClassUUID))
    courseClassTO.setCourseDetailsLibraries(mergeLibraries(courseUUID, courseVersionUUID, courseClassUUID))
  }
  
  def mergeSections(courseUUID: String, courseVersionUUID: String, courseClassUUID: String) = {
    val sectionsCourse = CourseDetailsSectionsRepo.getForEntity(courseUUID, CourseDetailsEntityType.COURSE).getCourseDetailsSections.asScala.asInstanceOf[Buffer[AssetEntity]]
    val sectionsCourseVersion = CourseDetailsSectionsRepo.getForEntity(courseVersionUUID, CourseDetailsEntityType.COURSE_VERSION).getCourseDetailsSections.asScala.asInstanceOf[Buffer[AssetEntity]]
    val sectionsCourseClass = CourseDetailsSectionsRepo.getForEntity(courseClassUUID, CourseDetailsEntityType.COURSE_CLASS).getCourseDetailsSections.asScala.asInstanceOf[Buffer[AssetEntity]]
    mergeAssets(sectionsCourse, sectionsCourseVersion, sectionsCourseClass).asInstanceOf[Buffer[CourseDetailsSection]].asJava
  }
  
  def mergeHints(courseUUID: String, courseVersionUUID: String, courseClassUUID: String) = {
    val hintsCourse = CourseDetailsHintsRepo.getForEntity(courseUUID, CourseDetailsEntityType.COURSE).getCourseDetailsHints.asScala.asInstanceOf[Buffer[AssetEntity]]
    val hintsCourseVersion = CourseDetailsHintsRepo.getForEntity(courseVersionUUID, CourseDetailsEntityType.COURSE_VERSION).getCourseDetailsHints.asScala.asInstanceOf[Buffer[AssetEntity]]
    val hintsCourseClass = CourseDetailsHintsRepo.getForEntity(courseClassUUID, CourseDetailsEntityType.COURSE_CLASS).getCourseDetailsHints.asScala.asInstanceOf[Buffer[AssetEntity]]
    mergeAssets(hintsCourse, hintsCourseVersion, hintsCourseClass).asInstanceOf[Buffer[CourseDetailsHint]].asJava
  }
  
  def mergeLibraries(courseUUID: String, courseVersionUUID: String, courseClassUUID: String) = {      
    val libraryFilesCourse = CourseDetailsLibrariesRepo.getForEntity(courseUUID, CourseDetailsEntityType.COURSE).getCourseDetailsLibraries.asScala.asInstanceOf[Buffer[AssetEntity]]
    val libraryFilesCourseVersion = CourseDetailsLibrariesRepo.getForEntity(courseVersionUUID, CourseDetailsEntityType.COURSE_VERSION).getCourseDetailsLibraries.asScala.asInstanceOf[Buffer[AssetEntity]]
    val libraryFilesCourseClass = CourseDetailsLibrariesRepo.getForEntity(courseClassUUID, CourseDetailsEntityType.COURSE_CLASS).getCourseDetailsLibraries.asScala.asInstanceOf[Buffer[AssetEntity]]
    mergeAssets(libraryFilesCourse, libraryFilesCourseVersion, libraryFilesCourseClass).asInstanceOf[Buffer[CourseDetailsLibrary]].asJava
  }
  
  def mergeAssets(assetsCourse: Buffer[AssetEntity], assetsCourseVersion: Buffer[AssetEntity], assetsCourseClass: Buffer[AssetEntity]) = {
    
    var assetsCourseVersionNew = assetsCourseVersion
    var assetsCourseClassNew = assetsCourseClass
    
    val assetsCourseWithVersion = assetsCourse.map { sc => {
        val assetCV = assetsCourseVersion.filter { _.getTitle.equals(sc.getTitle) }
        if(assetCV.size > 0){
          assetsCourseVersionNew = assetsCourseVersionNew.filterNot { _.getTitle.equals(sc.getTitle) }
          assetCV.head
        } else sc
      } 
    }
    
    val assetsCourseWithClass = assetsCourseWithVersion.map { sc => {
        val assetCC = assetsCourseClass.filter { _.getTitle.equals(sc.getTitle) }
        if(assetCC.size > 0){
          assetsCourseClassNew = assetsCourseClassNew.filterNot { _.getTitle.equals(sc.getTitle) }
          assetCC.head
        } else sc
      } 
    }
    
    (assetsCourseWithClass ++ assetsCourseVersionNew ++ assetsCourseClassNew)
  }

  def byPersonAndInstitution(personUUID: String, institutionUUID: String) = {
    bindEnrollments(personUUID, getAllClassesByInstitution(institutionUUID))
  }
  
  def byPagseguroId(pagseguroId: String) = {
    sql"""select * from CourseClass where pagseguroId = ${pagseguroId}""".first[CourseClass](toCourseClass)
  }
  
  def byEnrollment(enrollmentUUID: String) = {
    sql"""
	    | select cc.* from 
    	| CourseClass cc
    	| join Enrollment e on e.class_uuid = cc.uuid
    	| where e.uuid = ${enrollmentUUID}
	    | and cc.state <> ${CourseClassState.deleted.toString}
	    """.first[CourseClass](toCourseClass)
  }

  def byEnrollment(enrollmentUUID: String, personUUID: String, institutionUUID: String): CourseClassTO = {
    val courseClass = byEnrollment(enrollmentUUID);

  	if(courseClass.isDefined){
  	    val courseClassesTO = getAllClassesByInstitutionPaged(institutionUUID, "", Int.MaxValue, 1, "cc.name", true, "", null, courseClass.get.getUUID)
  	    bindEnrollments(personUUID, courseClassesTO).getCourseClasses().get(0)
  	} else {
  		//@TODO what about disabled versions?
	    val courseVersion = sql"""
		    | select cv.* from 
  			| CourseVersion cv
  			| join Enrollment e on e.courseVersionUUID = cv.uuid
  			| where e.uuid = ${enrollmentUUID}
  		    """.first[CourseVersion](toCourseVersion)
	    
  		if(courseVersion.isDefined){
  		    val course = sql"""
  			    | select c.* from 
    				| Course c
    				| join CourseVersion cv on cv.course_uuid = c.uuid
    				| where cv.uuid = ${courseVersion.get.getUUID}
    			""".first[Course](toCourse)
  			
    			val parentCourseClass = sql"""
            select cc.* from Enrollment e
            left join Enrollment pe on e.parentEnrollmentUUID = pe.uuid
            left join CourseClass cc on cc.uuid = pe.class_uuid 
            where e.uuid = ${enrollmentUUID}
          """.first[CourseClass]
    			  
    			val courseClassesTO = TOs.newCourseClassesTO
    			val list = new ArrayList[CourseClassTO]
    			val courseClassTO = TOs.newCourseClassTO(course.get, courseVersion.get, parentCourseClass.get, null)
    			courseClassTO.setEnrolledOnCourseVersion(true)
    		  bindEnrollment(personUUID, courseClassTO)
    			list.add(courseClassTO)
    			courseClassesTO.setCourseClasses(list)
  		    courseClassesTO.setCount(1)
  		    courseClassesTO.setPageSize(1)
  		    courseClassesTO.setPageNumber(1)
  		    courseClassesTO.setSearchCount(1)
  		    courseClassTO
  		} else {
  	    	throw new EntityNotFoundException("")
  		}
  	}
  }
  
  def countByCourseVersion(courseVersionUUID: String) = 
    sql"""select count(*) 
      from CourseClass cc 
      where cc.courseVersion_uuid = ${courseVersionUUID} 
      and cc.state <> ${CourseClassState.deleted.toString}
    """.first[String].get.toInt
  
  private def bindEnrollments(personUUID: String, courseClassesTO: CourseClassesTO) = {
    val classes = courseClassesTO.getCourseClasses().asScala
    //bind enrollment if it exists
    classes.foreach(cc => bindEnrollment(personUUID, cc))
    //only return the valid classes for the user (for example, hide private classes)
    courseClassesTO.setCourseClasses(classes.filter(isValidClass _).asJava)
    courseClassesTO
  }

  private def isValidClass(cc: CourseClassTO): Boolean = {
    cc.getCourseClass().isPublicClass() || cc.getEnrollment() != null
  }

  private def isPlatformAdmin(institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = false
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.platformAdmin, institutionUUID, null))
    hasRole
  }

  private def isInstitutionAdmin(institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = isPlatformAdmin(institutionUUID, roles)
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.institutionAdmin, institutionUUID, null))
    hasRole
  }

  private def isCourseClassAdmin(courseClassUUID: String, institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = isInstitutionAdmin(institutionUUID, roles)
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.courseClassAdmin, null, courseClassUUID))
    hasRole
  }

  private def isCourseClassObserver(courseClassUUID: String, institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = isInstitutionAdmin(institutionUUID, roles)
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.observer, null, courseClassUUID))
    hasRole
  }

  private def isCourseClassTutor(courseClassUUID: String, institutionUUID: String, roles: List[Role]) = {
    var hasRole: Boolean = isInstitutionAdmin(institutionUUID, roles)
    roles.foreach(role => hasRole = hasRole
      || RoleCategory.isValidRole(role, RoleType.tutor, null, courseClassUUID))
    hasRole
  }

  private def bindEnrollment(personUUID: String, courseClassTO: CourseClassTO) = {
    val enrollment = 
      if(courseClassTO.getCourseClass() != null)
    	  EnrollmentsRepo.byCourseClassAndPerson(courseClassTO.getCourseClass.getUUID, personUUID, false)
      else
    	  EnrollmentsRepo.byCourseVersionAndPerson(courseClassTO.getCourseVersionTO.getCourseVersion.getUUID, personUUID)
    
    enrollment foreach courseClassTO.setEnrollment
  }

  implicit def toString(rs: ResultSet): String = rs.getString(1)
}
