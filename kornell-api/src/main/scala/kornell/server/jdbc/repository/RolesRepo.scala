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
import kornell.core.util.StringUtils

object RolesRepo {
  	
  def getUserRoles(personUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
	    | select *, pw.username, cc.name as courseClassName
      | from Role r
		  | join Password pw on pw.personUUID = r.personUUID
		  | left join CourseClass cc on r.courseClassUUID = cc.uuid
      | where pw.personUUID = ${personUUID}
  		| order by r.role, pw.username
      """.map[RoleTO](toRoleTO(_,bindMode)))   
  	
  def getUsersForCourseClassByRole(courseClassUUID: String, roleType: RoleType, bindMode: String) =
	  TOs.newRolesTO(sql"""
	    | select *, pw.username, cc.name as courseClassName
    	| from Role r
      | join Password pw on pw.personUUID = r.personUUID
      | left join CourseClass cc on r.courseClassUUID = cc.uuid
      | where r.courseClassUUID = ${courseClassUUID}
			| and r.role = ${roleType.toString}
  		| order by r.role, pw.username
    """.map[RoleTO](toRoleTO(_,bindMode)))  
  	
  def getAllUsersWithRoleForCourseClass(courseClassUUID: String) = 
	  TOs.newRolesTO(sql"""
	    | select *, pw.username, cc.name as courseClassName
    	| from Role r
      | join Password pw on pw.personUUID = r.personUUID
      | left join CourseClass cc on r.courseClassUUID = cc.uuid
      | where r.courseClassUUID = ${courseClassUUID}
  		| order by r.role, pw.username
    """.map[RoleTO](toRoleTO(_,RoleCategory.BIND_DEFAULT)))  
	  
  	
  def getInstitutionAdmins(institutionUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
	    | select *, pw.username, null as courseClassName
    	| from Role r
	    | join Password pw on pw.personUUID = r.personUUID
      | where r.institutionUUID = ${institutionUUID}
			| and r.role = ${RoleType.institutionAdmin.toString}
  		| order by r.role, pw.username
    """.map[RoleTO](toRoleTO(_,bindMode)))   
  	
  def getPlatformAdmins(institutionUUID: String, bindMode: String) =
	  TOs.newRolesTO(sql"""
	    | select *, pw.username, null as courseClassName
    	| from Role r
		  | join Password pw on pw.personUUID = r.personUUID
      | where r.institutionUUID = ${institutionUUID}
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
      | join Password pw on pw.personUUID = r.personUUID
      | left join CourseClass cc on r.courseClassUUID = cc.uuid
      | where (r.courseClassUUID = ${courseClassUUID}
			| 	and r.role = ${RoleType.courseClassAdmin.toString})
			| or (r.institutionUUID = ${institutionUUID}
			| 	and r.role = ${RoleType.institutionAdmin.toString})
			| or (r.institutionUUID = ${institutionUUID}
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
      | join Password pw on pw.personUUID = r.personUUID
      | where (r.institutionUUID = ${institutionUUID}
			| 	and r.role = ${RoleType.institutionAdmin.toString})
			| or (r.institutionUUID = ${institutionUUID}
			|   and r.role = ${RoleType.platformAdmin.toString})
			| group by pw.username
    """.map[RoleTO](toRoleTO(_,bindMode)))   
  
  def updateCourseClassAdmins(institutionUUID: String, courseClassUUID: String, roles: Roles) = updateCourseClassRole(institutionUUID, courseClassUUID, RoleType.courseClassAdmin, roles)
  
  def updateTutors(institutionUUID: String, courseClassUUID: String, roles: Roles) = updateCourseClassRole(institutionUUID, courseClassUUID, RoleType.tutor, roles)
  
  def updateObservers(institutionUUID: String, courseClassUUID: String, roles: Roles) = updateCourseClassRole(institutionUUID, courseClassUUID, RoleType.observer, roles)
  
  def updateCourseClassRole(institutionUUID: String, courseClassUUID: String, roleType: RoleType, roles: Roles) = {
    val from = getUsersForCourseClassByRole(courseClassUUID, roleType, RoleCategory.BIND_DEFAULT)
    
    removeCourseClassRole(courseClassUUID, roleType).addRoles(roles)
    
    val to = getUsersForCourseClassByRole(courseClassUUID, roleType, RoleCategory.BIND_DEFAULT)
    
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
    roles.getRoles.asScala.foreach(create _)
    roles
  }

  def create(role: Role) = {
      if(StringUtils.isNone(role.getUUID)){
        role.setUUID(UUID.random)
      }
    if(RoleType.courseClassAdmin.equals(role.getRoleType) || RoleType.tutor.equals(role.getRoleType)
        || RoleType.observer.equals(role.getRoleType)) {
	    sql"""
	    	insert into Role (uuid, personUUID, role, courseClassUUID) values (
        ${role.getUUID}, 
    		${role.getPersonUUID}, 
	    	${role.getRoleType.toString}, 
	    	${RoleCategory.getCourseClassUUID(role)})
	    """.executeUpdate
    }
    if (RoleType.institutionAdmin.equals(role.getRoleType)) {
      sql"""
        insert into Role (uuid, personUUID, role, institutionUUID) values (
        ${role.getUUID}, 
        ${role.getPersonUUID}, 
        ${role.getRoleType.toString}, 
        ${role.getInstitutionAdminRole.getInstitutionUUID})
      """.executeUpdate
    }
  }
  
  def removeCourseClassRole(courseClassUUID: String, roleType: RoleType) = {
    sql"""
    	delete from Role
    	where courseClassUUID = ${courseClassUUID}
        and role = ${roleType.toString}
    """.executeUpdate
    this
  }
  
  def removeInstitutionAdmins(institutionUUID: String) = {
    sql"""
        delete from Role
        where institutionUUID = ${institutionUUID}
        and role = ${RoleType.institutionAdmin.toString}
    """.executeUpdate
    this
  }
  
}