package kornell.server.helper

import java.util.Date
import org.scalatest.BeforeAndAfter
import org.scalatest.Suite
import org.scalatest.SuiteMixin
import kornell.core.entity.Course
import kornell.core.entity.CourseClass
import kornell.core.entity.EntityState
import kornell.core.entity.CourseVersion
import kornell.core.entity.Institution
import kornell.core.entity.Person
import kornell.core.entity.RoleType
import kornell.server.api.CourseClassesResource
import kornell.server.api.EnrollmentsResource
import kornell.server.api.UserResource
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.repository.Entities
import kornell.core.entity.BillingType
import kornell.core.entity.InstitutionType

trait SimpleInstitution extends SuiteMixin with Generator with BeforeAndAfter{ this: Suite =>
	
  val userResource = UserResource()
  val courseClassesResource =  CourseClassesResource()
  val enrollmentsResource = new EnrollmentsResource
	val className = randStr
	val classUUID = randUUID
	val fullName = randName
  val email = randEmail
  val cpf = randStr
  val platformAdminCPF = randStr
  val institutionAdminCPF = randStr
  val notAnAdminCPF = randStr
  
  var mockHttpServletResponse: MockHttpServletResponse = null
  
	var course: Course = null
	var courseVersion: CourseVersion = null
	var courseClass: CourseClass = null
	var courseClass2: CourseClass = null
	var courseClass3: CourseClass = null
  
  var institution: Institution = null
  
  var platformAdmin: Person = null
  var platformAdminSecurityContext: MockSecurityContext = null
  var institutionAdmin: Person = null
  var institutionAdminSecurityContext: MockSecurityContext = null
  var notAnAdmin: Person = null
  var notAnAdminSecurityContext: MockSecurityContext = null
  
  
  abstract override def withFixture(test: NoArgTest) = {
    
    institution = InstitutionsRepo.create(Entities.newInstitution(randUUID, randStr, randStr, randStr, randURL, false, false, false, false, null, "", BillingType.enrollment, InstitutionType.DEFAULT, null, false, false, null, "America/Sao_Paulo"))
	  course = CoursesRepo.create(Entities.newCourse(randUUID, randStr, randStr, randStr, randStr, institution.getUUID, false))
	  courseVersion = CourseVersionsRepo.create(Entities.newCourseVersion(randUUID, randStr, course.getUUID, new Date, randStr, "KNL", false), institution.getUUID)
	  courseClass = Entities.newCourseClass(classUUID, className, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, false, 23451, new Date(), null, EntityState.active)
	  courseClass2 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, false, 23451, new Date(), null, EntityState.active)
	  courseClass3 = Entities.newCourseClass(randUUID, randStr, courseVersion.getUUID, institution.getUUID, new java.math.BigDecimal(60), true, false, false, 23451, new Date(), null, EntityState.active)
    
	  platformAdmin = {
	    val platformAdmin = PeopleRepo.createPersonCPF(institution.getUUID, platformAdminCPF, randName)
	    PersonRepo(platformAdmin.getUUID).setPassword(institution.getUUID, platformAdmin.getCPF, platformAdmin.getCPF)
	    sql"""
	    	insert into Role (uuid, username, role, institutionUUID, course_classUUID)
	    	values (${randUUID}, ${platformAdmin.getCPF}, 
	    	${RoleType.platformAdmin.toString}, 
	    	${null}, 
	    	${null})
		    """.executeUpdate
		  platformAdmin
	  }
	  
	  platformAdminSecurityContext = new MockSecurityContext(platformAdmin.getCPF)
	  
	  institutionAdmin = {
	    val institutionAdmin = PeopleRepo.createPersonCPF(institution.getUUID, institutionAdminCPF, randName)
	    PersonRepo(institutionAdmin.getUUID).setPassword(institution.getUUID, institutionAdmin.getCPF, institutionAdmin.getCPF)
	    
	    sql"""
	    	insert into Role (uuid, username, role, institutionUUID, course_classUUID)
	    	values (${randUUID}, ${institutionAdmin.getCPF}, 
	    	${RoleType.institutionAdmin.toString}, 
	    	${institution.getUUID}, 
	    	${null} )
		    """.executeUpdate
		  institutionAdmin
	  }
	  
	  institutionAdminSecurityContext = new MockSecurityContext(institutionAdmin.getCPF)
	  
	  notAnAdmin = {
	    val notAnAdmin = PeopleRepo.createPersonCPF(institution.getUUID, notAnAdminCPF, randName)
	    PersonRepo(notAnAdmin.getUUID).setPassword(institution.getUUID, notAnAdmin.getCPF, notAnAdmin.getCPF)
		  notAnAdmin
	  }
	  
	  notAnAdminSecurityContext = new MockSecurityContext(notAnAdmin.getCPF)
	  
	  mockHttpServletResponse = new MockHttpServletResponse(0, null)
    
    try super.withFixture(test) // To be stackable, must call super.withFixture
    
	  
  }
}
