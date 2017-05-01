package kornell.server.jdbc.repository

import kornell.core.entity.CourseClass
import kornell.core.error.exception.EntityConflictException
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.SQL.rsToString
import kornell.core.entity.AuditedEntityType
import kornell.core.entity.ChatThreadType

class CourseClassRepo(uuid:String) {
  lazy val finder = sql"""
  select * from CourseClass where uuid=$uuid
  """
  
  def first = finder.first[CourseClass]
  
  def get = finder.get[CourseClass] 
  
  def version = CourseVersionRepo(get.getCourseVersionUUID)
  
  def institution = InstitutionRepo(get.getInstitutionUUID)
  
  def update(courseClass: CourseClass): CourseClass = { 
    //get previous version
    val oldCourseClass = CourseClassRepo(courseClass.getUUID).first.get

    val courseClassExists = sql"""
    select count(*) from CourseClass where courseVersion_uuid = ${courseClass.getCourseVersionUUID} and name = ${courseClass.getName} and uuid <> ${courseClass.getUUID}
    """.first[String].get
    if (courseClassExists == "0") {
	    sql"""
	      update CourseClass cc set
			    cc.name = ${courseClass.getName},
			    cc.institution_uuid = ${courseClass.getInstitutionUUID},
		  		cc.requiredScore = ${courseClass.getRequiredScore},
		  		cc.publicClass = ${courseClass.isPublicClass},
		  		cc.overrideEnrollments = ${courseClass.isOverrideEnrollments},
		  		cc.invisible = ${courseClass.isInvisible},
		  		cc.maxEnrollments = ${courseClass.getMaxEnrollments},
		  		cc.registrationType = ${courseClass.getRegistrationType.toString},
		  		cc.institutionRegistrationPrefixUUID = ${courseClass.getInstitutionRegistrationPrefixUUID},
		  		cc.courseClassChatEnabled = ${courseClass.isCourseClassChatEnabled},
		  		cc.chatDockEnabled = ${courseClass.isChatDockEnabled},
		  		cc.allowBatchCancellation = ${courseClass.isAllowBatchCancellation},
		  		cc.tutorChatEnabled = ${courseClass.isTutorChatEnabled},
		  		cc.approveEnrollmentsAutomatically = ${courseClass.isApproveEnrollmentsAutomatically},
          cc.pagseguroId = ${courseClass.getPagseguroId},
          cc.thumbUrl = ${courseClass.getThumbUrl}
	      where cc.uuid = ${courseClass.getUUID}""".executeUpdate 
	    
        //update course class threads active states per threadType and add participants to the global class chat, if applicable
	    ChatThreadsRepo.updateCourseClassChatThreadStatusByThreadType(courseClass.getUUID, ChatThreadType.COURSE_CLASS, courseClass.isCourseClassChatEnabled)
	    ChatThreadsRepo.updateCourseClassChatThreadStatusByThreadType(courseClass.getUUID, ChatThreadType.TUTORING, courseClass.isTutorChatEnabled)
	    ChatThreadsRepo.addParticipantsToCourseClassThread(courseClass)
	    
	    //log entity change
	    EventsRepo.logEntityChange(courseClass.getInstitutionUUID, AuditedEntityType.courseClass, courseClass.getUUID, oldCourseClass, courseClass)
	        
	    courseClass
    } else {
      throw new EntityConflictException("courseClassAlreadyExists")
    }   
  }
  
  def delete(courseClassUUID: String) = {    
    sql"""
      delete from CourseClass 
      where uuid = ${courseClassUUID}""".executeUpdate
  }
  
  def actomsVisitedBy(personUUID: String): List[String] = sql"""
  	select actomKey from ActomEntered ae
  	join Enrollment e on ae.enrollmentUUID=e.uuid
  	where e.class_uuid = ${uuid}
  	and person_uuid = ${personUUID}
  	order by eventFiredAt
  	""".map[String]({ rs => rs.getString("actomKey") })
}

object CourseClassRepo extends App {
  def apply(uuid:String) = new CourseClassRepo(uuid)
}
