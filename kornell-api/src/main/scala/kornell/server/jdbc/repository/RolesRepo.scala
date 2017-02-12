package kornell.server.jdbc.repository

import java.sql.ResultSet
import scala.collection.JavaConverters._
import kornell.core.entity.Course
import kornell.core.entity.Course
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.server.repository.Entities
import kornell.server.repository.TOs
import kornell.core.entity.Role
import kornell.core.util.UUID
import kornell.core.entity.Roles
import kornell.core.entity.RoleType
import kornell.core.entity.RoleCategory
import kornell.core.to.RoleTO
import kornell.core.entity.AuditedEntityType
import kornell.core.error.exception.EntityConflictException

object RolesRepo {
  	
  def getUserRoles(personUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
		    | select *, pw.username, cc.name as courseClassName
	      	| from Role r
			  | join Password pw on pw.person_uuid = r.person_uuid
			  | left join CourseClass cc on r.course_class_uuid = cc.uuid
	        | where pw.person_uuid = ${personUUID}
	  		| order by r.role, pw.username
            """.map[RoleTO](toRoleTO(_,bindMode)))   
  	
  def getUsersWithRoleForCourseClass(courseClassUUID: String, bindMode: String, roleType: RoleType) =
	  TOs.newRolesTO(sql"""
		    | select *, pw.username, cc.name as courseClassName
	      	| from Role r
		        | join Password pw on pw.person_uuid = r.person_uuid
		        | left join CourseClass cc on r.course_class_uuid = cc.uuid
	        | where r.course_class_uuid = ${courseClassUUID}
	  			| and r.role = ${roleType.toString}
	  		| order by r.role, pw.username
            """.map[RoleTO](toRoleTO(_,bindMode)))   
  	
  def getInstitutionAdmins(institutionUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
		    | select *, pw.username, null as courseClassName
	      	| from Role r
			  | join Password pw on pw.person_uuid = r.person_uuid
	        | where r.institution_uuid = ${institutionUUID}
	  			| and r.role = ${RoleType.institutionAdmin.toString}
	  		| order by r.role, pw.username
            """.map[RoleTO](toRoleTO(_,bindMode)))   
  	
  def getPlatformAdmins(institutionUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
		    | select *, pw.username, null as courseClassName
	      	| from Role r
			  | join Password pw on pw.person_uuid = r.person_uuid
	        | where r.institution_uuid = ${institutionUUID}
	  			| and r.role = ${RoleType.platformAdmin.toString}
	  		| order by r.role, pw.username
            """.map[RoleTO](toRoleTO(_,bindMode)))
  	
  def getCourseClassSupportThreadParticipants(courseClassUUID: String, institutionUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
		    | select *, pw.username, cc.name as courseClassName
	      	| from (select * from Role 
				|  	order by case `role`
				|	when 'platformAdmin' then 1
				|	when 'institutionAdmin' then 2
				|	when 'courseClassAdmin' then 3
				|	END) r
	        | join Password pw on pw.person_uuid = r.person_uuid
	        | left join CourseClass cc on r.course_class_uuid = cc.uuid
	        | where (r.course_class_uuid = ${courseClassUUID}
	  			| 	and r.role = ${RoleType.courseClassAdmin.toString})
	  			| or (r.institution_uuid = ${institutionUUID}
	  			| 	and r.role = ${RoleType.institutionAdmin.toString})
	  			| or (r.institution_uuid = ${institutionUUID}
	  			|   and r.role = ${RoleType.platformAdmin.toString})
			| group by pw.username
            """.map[RoleTO](toRoleTO(_,bindMode)))    	

  	
  def getPlatformSupportThreadParticipants(institutionUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
		    | select *, pw.username, null as courseClassName
	      	| from (select * from Role 
				|  	order by case `role`
				|	when 'platformAdmin' then 1
				|	when 'institutionAdmin' then 2
				|	END) r
	        | join Password pw on pw.person_uuid = r.person_uuid
	        | where (r.institution_uuid = ${institutionUUID}
	  			| 	and r.role = ${RoleType.institutionAdmin.toString})
	  			| or (r.institution_uuid = ${institutionUUID}
	  			|   and r.role = ${RoleType.platformAdmin.toString})
			| group by pw.username
            """.map[RoleTO](toRoleTO(_,bindMode)))   
  
  def updateCourseClassAdmins(institutionUUID: String, courseClassUUID: String, roles: Roles) = updateCourseClassRole(institutionUUID, courseClassUUID, RoleType.courseClassAdmin, roles)
  
  def updateTutors(institutionUUID: String, courseClassUUID: String, roles: Roles) = updateCourseClassRole(institutionUUID, courseClassUUID, RoleType.tutor, roles)
  
  def updateObservers(institutionUUID: String, courseClassUUID: String, roles: Roles) = updateCourseClassRole(institutionUUID, courseClassUUID, RoleType.observer, roles)
  
  def updateCourseClassRole(institutionUUID: String, courseClassUUID: String, roleType: RoleType, roles: Roles) = {
    val from = getUsersWithRoleForCourseClass(courseClassUUID, RoleCategory.BIND_DEFAULT, roleType)
    
    removeCourseClassRole(courseClassUUID, roleType).addRoles(roles)
    
    val to = getUsersWithRoleForCourseClass(courseClassUUID, RoleCategory.BIND_DEFAULT, roleType)
    
    val auditedEntityType = {
      roleType match {
	      case RoleType.courseClassAdmin => AuditedEntityType.courseClassAdmin
	      case RoleType.tutor  => AuditedEntityType.courseClassTutor
	      case RoleType.observer => AuditedEntityType.courseClassObserver
	      case _ => throw new EntityConflictException("invalidValue")
      }
    }
    
    //log entity change
    EventsRepo.logEntityChange(institutionUUID, auditedEntityType, courseClassUUID, from, to)
    
    roles
  }
  
  def updateInstitutionAdmins(institutionUUID: String, roles: Roles) = {
    val from = getInstitutionAdmins(institutionUUID, RoleCategory.BIND_DEFAULT)
    
    removeInstitutionAdmins(institutionUUID).addRoles(roles)
    
    val to = getInstitutionAdmins(institutionUUID, RoleCategory.BIND_DEFAULT)
    
    //log entity change
    EventsRepo.logEntityChange(institutionUUID, AuditedEntityType.institutionAdmin, institutionUUID, from, to)
    
    roles
  }
  
  def addRoles(roles: Roles) = {
    roles.getRoles.asScala.foreach(addRole _)
    roles
  }

  private def addRole(role: Role) = {
    if(RoleType.courseClassAdmin.equals(role.getRoleType) || RoleType.tutor.equals(role.getRoleType)
        || RoleType.observer.equals(role.getRoleType)) {
      val courseClassUUID = {
        if (RoleType.courseClassAdmin.equals(role.getRoleType)) role.getCourseClassAdminRole.getCourseClassUUID
        else if (RoleType.tutor.equals(role.getRoleType)) role.getTutorRole.getCourseClassUUID
        else if (RoleType.observer.equals(role.getRoleType)) role.getObserverRole.getCourseClassUUID
        else ""
      }
	    sql"""
	    	insert into Role (uuid, person_uuid, role, course_class_uuid)
	    	values (${UUID.random}, 
    		${role.getPersonUUID}, 
	    	${role.getRoleType.toString}, 
	    	${courseClassUUID})
	    """.executeUpdate
    }
    if (RoleType.institutionAdmin.equals(role.getRoleType)) {
      sql"""
            insert into Role (uuid, person_uuid, role, institution_uuid)
            values (${UUID.random}, 
            ${role.getPersonUUID}, 
            ${role.getRoleType.toString}, 
            ${role.getInstitutionAdminRole.getInstitutionUUID})
        """.executeUpdate
    }
  }
  
  def removeCourseClassRole(courseClassUUID: String, roleType: RoleType) = {
    sql"""
    	delete from Role
    	where course_class_uuid = ${courseClassUUID}
        and role = ${roleType.toString}
    """.executeUpdate
    this
  }
  
  def removeInstitutionAdmins(institutionUUID: String) = {
    sql"""
        delete from Role
        where institution_uuid = ${institutionUUID}
        and role = ${RoleType.institutionAdmin.toString}
    """.executeUpdate
    this
  }
  
}