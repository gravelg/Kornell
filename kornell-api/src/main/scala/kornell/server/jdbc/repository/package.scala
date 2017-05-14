package kornell.server.jdbc

import java.sql.ResultSet
import java.util.logging.Logger
import kornell.core.entity.Assessment
import kornell.core.entity.AuditedEntityType
import kornell.core.entity.AuthClientType
import kornell.core.entity.BillingType
import kornell.core.entity.ChatThread
import kornell.core.entity.ChatThreadParticipant
import kornell.core.entity.Course
import kornell.core.entity.CourseClass
import kornell.core.entity.CourseClassState
import kornell.core.entity.CourseVersion
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.Institution
import kornell.core.entity.InstitutionRegistrationPrefix
import kornell.core.entity.InstitutionType
import kornell.core.entity.Person
import kornell.core.entity.RegistrationType
import kornell.core.entity.RegistrationType
import kornell.core.entity.RoleCategory
import kornell.core.entity.RoleType
import kornell.core.event.EntityChanged
import kornell.core.to.ChatThreadMessageTO
import kornell.core.to.CourseClassTO
import kornell.core.to.CourseVersionTO
import kornell.core.to.EnrollmentTO
import kornell.core.to.PersonTO
import kornell.core.to.RoleTO
import kornell.core.to.SimplePersonTO
import kornell.core.to.TokenTO
import kornell.core.to.UnreadChatThreadTO
import kornell.server.repository.Entities._
import kornell.server.repository.Entities
import kornell.server.repository.TOs._
import kornell.server.repository.TOs
import java.util.UUID
import kornell.core.to.DashboardLeaderboardTO
import kornell.core.to.DashboardLeaderboardItemTO
import kornell.core.entity.CourseDetailsHint
import kornell.core.entity.CourseDetailsEntityType
import kornell.core.entity.CourseDetailsLibrary
import kornell.core.entity.CourseDetailsSection
import kornell.core.to.CourseTO
import kornell.core.entity.ContentRepository
import kornell.core.entity.RepositoryType
import kornell.core.entity.CertificateDetails
import kornell.core.entity.CertificateType
import kornell.core.entity.EnrollmentSource
import kornell.core.entity.PostbackType
import kornell.core.entity.PostbackConfig
import kornell.core.entity.ContentSpec

/**
 * Classes in this package are Data Access Objects for JDBC Databases
 *
 * This is the naming convention for methods in this package:
 *
 * first() => Return Option[T], when the result may not exist
 * get() => Returns T, presuming the result exists
 * find() => Return Collection[T], as the result of a query
 */
package object repository {
  
  val logger = Logger.getLogger("kornell.server.jdbc.repository")


  //TODO: Move converters to their repos
  implicit def toInstitution(rs:ResultSet):Institution = 
    newInstitution(rs.getString("uuid"), 
        rs.getString("name"),  
        rs.getString("fullName"), 
        rs.getString("terms"),
        rs.getString("baseURL"),
        rs.getBoolean("demandsPersonContactDetails"),
        rs.getBoolean("validatePersonContactDetails"),
        rs.getBoolean("allowRegistration"),
        rs.getBoolean("allowRegistrationByUsername"),
        rs.getDate("activatedAt"),
        rs.getString("skin"),
        BillingType.valueOf(rs.getString("billingType")),
        InstitutionType.valueOf(rs.getString("institutionType")),
        rs.getString("dashboardVersionUUID"),
        rs.getBoolean("internationalized"),
        rs.getBoolean("useEmailWhitelist"),
        rs.getString("assetsRepositoryUUID"),
        rs.getString("timeZone"),
        rs.getString("institutionSupportEmail"),
        rs.getBoolean("advancedMode"))
        
  implicit def toContentRepository(rs:ResultSet):ContentRepository = 
    newContentRepository(rs.getString("uuid"),
        RepositoryType.valueOf(rs.getString("repositoryType")),
        rs.getString("accessKeyId"),
        rs.getString("secretAccessKey"),
        rs.getString("bucketName"),
        rs.getString("prefix"),
        rs.getString("region"),
        rs.getString("institutionUUID"),
        rs.getString("path"))
  
  implicit def toCourseClass(r: ResultSet): CourseClass = 
    newCourseClass(r.getString("uuid"), r.getString("name"), 
        r.getString("courseVersion_uuid"), r.getString("institution_uuid"),
        r.getBigDecimal("requiredScore"), r.getBoolean("publicClass"), 
        r.getBoolean("overrideEnrollments"),
        r.getBoolean("invisible"), r.getInt("maxEnrollments"), 
        r.getDate("createdAt"), r.getString("createdBy"), 
        CourseClassState.valueOf(r.getString("state")), 
        RegistrationType.valueOf(r.getString("registrationType")),
        r.getString("institutionRegistrationPrefixUUID"), r.getBoolean("courseClassChatEnabled"), 
        r.getBoolean("chatDockEnabled"), r.getBoolean("allowBatchCancellation"),  
        r.getBoolean("tutorChatEnabled"), r.getBoolean("approveEnrollmentsAutomatically"),
        r.getDate("startDate"), r.getString("pagseguroId"), r.getString("thumbUrl")) 

  implicit def toCourse(rs: ResultSet): Course = newCourse(
    rs.getString("uuid"),
    rs.getString("code"),
    rs.getString("title"),
    rs.getString("description"),
    rs.getString("infoJson"),
    rs.getString("institutionUUID"),
    rs.getBoolean("childCourse"),
    rs.getString("thumbUrl"),
    ContentSpec.valueOf(rs.getString("contentSpec")))
    
  implicit def toCourseTO(rs: ResultSet): CourseTO = newCourseTO(
      toCourse(rs))

  implicit def toCourseVersion(rs: ResultSet): CourseVersion = newCourseVersion(
    rs.getString("uuid"), 
    rs.getString("name"), 
    rs.getString("course_uuid"), 
    rs.getDate("versionCreatedAt"),
    rs.getString("distributionPrefix"),
    rs.getBoolean("disabled"),
    rs.getString("parentVersionUUID"),
    rs.getInt("instanceCount"),
    rs.getString("label"),
    rs.getString("thumbUrl"))    
  
  implicit def toCourseClassTO(rs: ResultSet): CourseClassTO = {
    val course = newCourse(
    		rs.getString("courseUUID"), 
		    rs.getString("code"), 
		    rs.getString("title"),
		    rs.getString("description"), 
		    rs.getString("infoJson"),
		    rs.getString("institutionUUID"),
		    rs.getBoolean("childCourse"),
		    rs.getString("courseThumbUrl"),
		    ContentSpec.valueOf(rs.getString("contentSpec")));

    val version = newCourseVersion(
        rs.getString("courseVersionUUID"), 
		    rs.getString("courseVersionName"), 
		    rs.getString("courseUUID"), 
		    rs.getDate("versionCreatedAt"),
		    rs.getString("distributionPrefix"),
		    rs.getBoolean("disabled"),
        rs.getString("parentVersionUUID"),
        rs.getInt("instanceCount"),
        rs.getString("label"),
        rs.getString("courseVersionThumbUrl"));

    val clazz = newCourseClass(
			rs.getString("courseClassUUID"),
			rs.getString("courseClassName"), 
			rs.getString("courseVersionUUID"), 
			rs.getString("institutionUUID"),
			rs.getBigDecimal("requiredScore"),
			rs.getBoolean("publicClass"),
			rs.getBoolean("overrideEnrollments"),
			rs.getBoolean("invisible"),
			rs.getInt("maxEnrollments"),
			rs.getDate("createdAt"),
			rs.getString("createdBy"), 
			CourseClassState.valueOf(rs.getString("state")), 
			RegistrationType.valueOf(rs.getString("registrationType")),
			rs.getString("institutionRegistrationPrefixUUID"),
			rs.getBoolean("courseClassChatEnabled"),
			rs.getBoolean("chatDockEnabled"),
			rs.getBoolean("allowBatchCancellation"),  
			rs.getBoolean("tutorChatEnabled"),
			rs.getBoolean("approveEnrollmentsAutomatically"),
			null,
			rs.getString("pagseguroId"),
			rs.getString("courseClassThumbUrl"))
    		
    TOs.newCourseClassTO(course, version, clazz, rs.getString("institutionRegistrationPrefixName"))
  }
  
  implicit def toCourseVersionTO(rs: ResultSet): CourseVersionTO = {
    val courseVersion = newCourseVersion(
        rs.getString("courseVersionUUID"), 
        rs.getString("courseVersionName"), 
        rs.getString("courseUUID"), 
        rs.getDate("versionCreatedAt"), 
        rs.getString("distributionPrefix"), 
        rs.getBoolean("courseVersionDisabled"),
        rs.getString("parentVersionUUID"),
        rs.getInt("instanceCount"),
        rs.getString("label"),
        rs.getString("courseVersionThumbUrl"))
        
    val course = newCourse(
        rs.getString("courseUUID"), 
        rs.getString("courseCode"), 
        rs.getString("courseTitle"), 
        rs.getString("courseDescription"), 
        rs.getString("infoJson"),
        rs.getString("institutionUUID"),
        rs.getBoolean("childCourse"),
        rs.getString("courseThumbUrl"),
        ContentSpec.valueOf(rs.getString("contentSpec")))
        
    TOs.newCourseVersionTO(course, courseVersion)
  }
  
  implicit def toEnrollment(rs: ResultSet): Enrollment = {
    newEnrollment(
      rs.getString("uuid"),
      rs.getTimestamp("enrolledOn"),
      rs.getString("class_uuid"),
      rs.getString("person_uuid"),
      rs.getInt("progress"),
      rs.getString("notes"),      
      EnrollmentState.valueOf(rs.getString("state")),
      rs.getTimestamp("lastProgressUpdate"),
      Option(rs.getString("assessment"))
      	.map(Assessment.valueOf)
      	.getOrElse(null),
      rs.getTimestamp("lastAssessmentUpdate"),
      rs.getBigDecimal("assessmentScore"),
      rs.getTimestamp("certifiedAt"),
      rs.getString("courseVersionUUID"),
      rs.getString("parentEnrollmentUUID"),
      rs.getDate("start_date"),
      rs.getDate("end_date"),
      rs.getBigDecimal("preAssessmentScore"),
      rs.getBigDecimal("postAssessmentScore"),
      EnrollmentSource.valueOf(rs.getString("enrollmentSource"))
    )
  }
    

  implicit def toEnrollmentTO(rs: ResultSet): EnrollmentTO = {
    val enrollment = newEnrollment(
      rs.getString("uuid"),
      rs.getDate("enrolledOn"),
      rs.getString("class_uuid"),
      rs.getString("person_uuid"),
      rs.getInt("progress"),
      rs.getString("notes"),      
      EnrollmentState.valueOf(rs.getString("state")),
      rs.getTimestamp("lastProgressUpdate"),
      Option(rs.getString("assessment"))
      	.map(Assessment.valueOf)
      	.getOrElse(null),
      rs.getTimestamp("lastAssessmentUpdate"),
      rs.getBigDecimal("assessmentScore"),
      rs.getTimestamp("certifiedAt")
    )
    
    TOs.newEnrollmentTO(enrollment, rs.getString("personUUID"), rs.getString("fullName"), rs.getString("username"))
  }
	
	implicit def toPerson(rs:ResultSet):Person = newPerson(
	    rs.getString("uuid"),
	    rs.getString("fullName"), 
	    rs.getString("lastPlaceVisited"),
	    rs.getString("email"),
	    rs.getString("company"),
	    rs.getString("title"),
	    rs.getString("sex"),
	    rs.getDate("birthDate"),
	    rs.getString("confirmation"),
	    rs.getString("telephone"),
	    rs.getString("country"),
	    rs.getString("state"),
	    rs.getString("city"),
	    rs.getString("addressLine1"),
	    rs.getString("addressLine2"),
	    rs.getString("postalCode"),
	    rs.getString("cpf"),
	    rs.getString("institutionUUID"),
	    rs.getTimestamp("termsAcceptedOn"),
	    RegistrationType.valueOf(rs.getString("registrationType")),
	    rs.getString("institutionRegistrationPrefixUUID"),
	    rs.getBoolean("receiveEmailCommunication"),
	    rs.getBoolean("forcePasswordUpdate"))
	
	implicit def toPersonTO(rs:ResultSet):PersonTO = newPersonTO(toPerson(rs),
	    rs.getString("username"))

  implicit def toRole(rs: java.sql.ResultSet): kornell.core.entity.Role = {
    val roleType = RoleType.valueOf(rs.getString("role"))
    val role = roleType match {
      case RoleType.user => Entities.newUserRole
      case RoleType.platformAdmin => Entities.newRoleAsPlatformAdmin(rs.getString("person_uuid"), rs.getString("institution_uuid"))
      case RoleType.institutionAdmin => Entities.newInstitutionAdminRole(rs.getString("person_uuid"), rs.getString("institution_uuid"))
      case RoleType.courseClassAdmin => Entities.newCourseClassAdminRole(rs.getString("person_uuid"), rs.getString("course_class_uuid"))
      case RoleType.tutor => Entities.newTutorRole(rs.getString("person_uuid"), rs.getString("course_class_uuid"))
      case RoleType.observer => Entities.newObserverRole(rs.getString("person_uuid"), rs.getString("course_class_uuid"))
      case RoleType.controlPanelAdmin => Entities.newControlPanelAdminRole(rs.getString("person_uuid"))
    }
    role
  }

  implicit def toRoleTO(rs: java.sql.ResultSet, bindMode: String): RoleTO = {
    val role = toRole(rs)
    TOs.newRoleTO(role, {
      if(role != null && RoleCategory.BIND_WITH_PERSON.equals(bindMode))
        PeopleRepo.getByUUID(role.getPersonUUID).get
      else
        null
    }, rs.getString("username"))
  }
	
  implicit def toInstitutionRegistrationPrefix(rs:ResultSet):InstitutionRegistrationPrefix = 
    newInstitutionRegistrationPrefix(rs.getString("uuid"), 
        rs.getString("name"),  
        rs.getString("institutionUUID"), 
        rs.getBoolean("showEmailOnProfile"),
        rs.getBoolean("showCPFOnProfile"),
        rs.getBoolean("showContactInformationOnProfile"))
	
	implicit def toUnreadChatThreadTO(rs:ResultSet):UnreadChatThreadTO = newUnreadChatThreadTO(
	    rs.getString("unreadMessages"),
	    rs.getString("chatThreadUUID"), 
	    rs.getString("threadType"),
	    rs.getString("creatorName"),
	    rs.getString("entityUUID"),
	    rs.getString("entityName"))
	
	implicit def toChatThreadMessageTO(rs:ResultSet):ChatThreadMessageTO = newChatThreadMessageTO(
	    rs.getString("senderFullName"),
	    if(rs.getString("senderRole") == null)
	      RoleType.user
	    else
	      RoleType.valueOf(rs.getString("senderRole")),
	    rs.getTimestamp("sentAt"), 
	    rs.getString("message"))
  
	implicit def toChatThreadParticipant(rs: ResultSet): ChatThreadParticipant = newChatThreadParticipant(
	    rs.getString("uuid"),
	    rs.getString("chatThreadUUID"),
	    rs.getString("personUUID"),
	    rs.getString("chatThreadName"),
	    rs.getTimestamp("lastReadAt"),
	    rs.getBoolean("active"),
	    rs.getTimestamp("lastJoinDate"))
	    
	implicit def toChatThread(rs: ResultSet): ChatThread = newChatThread(
	    rs.getString("uuid"), 
	    rs.getTimestamp("createdAt"), 
	    rs.getString("institutionUUID"), 
	    rs.getString("courseClassUUID"), 
	    rs.getString("personUUID"), 
	    rs.getString("threadType"), 
	    rs.getBoolean("active"))
	    
	implicit def toTokenTO(rs: ResultSet): TokenTO = newTokenTO(
	    rs.getString("token"),
	    rs.getTimestamp("expiry"),
	    rs.getString("personUUID"),
	    AuthClientType.valueOf(rs.getString("clientType")))
	    
  implicit def toSimplePersonTO(rs: ResultSet): SimplePersonTO = newSimplePersonTO(
      rs.getString("uuid"),
      rs.getString("fullName"),
      rs.getString("username"))
    
  implicit def toDashboardLeaderboardItemTO(rs: ResultSet): DashboardLeaderboardItemTO = newDashboardLeaderboardItemTO(
      rs.getString("uuid"),
      rs.getString("fullName"),
      rs.getString("attribute"))
        
   
  implicit def toEntityChanged(rs: ResultSet): EntityChanged = newEntityChanged(
    rs.getString("uuid"), 
    rs.getTimestamp("eventFiredAt"),
    rs.getString("institutionUUID"),
    rs.getString("personUUID"),
    AuditedEntityType.valueOf(rs.getString("entityType")),
    rs.getString("entityUUID"),
    rs.getString("fromValue"),
    rs.getString("toValue"),
    rs.getString("entityName"),
    rs.getString("fromPersonName"),
    rs.getString("fromUsername")) 
    
  implicit def toCourseDetailsHint(rs: ResultSet): CourseDetailsHint = newCourseDetailsHint(
      rs.getString("uuid"), 
      rs.getString("title"),
      rs.getString("text"), 
      CourseDetailsEntityType.valueOf(rs.getString("entityType")), 
      rs.getString("entityUUID"), 
      rs.getInt("index"), 
      rs.getString("fontAwesomeClassName"))
      
  implicit def toCourseDetailsLibrary(rs: ResultSet): CourseDetailsLibrary = newCourseDetailsLibrary(
      rs.getString("uuid"), 
      rs.getString("title"), 
      rs.getString("description"), 
      CourseDetailsEntityType.valueOf(rs.getString("entityType")), 
      rs.getString("entityUUID"), 
      rs.getInt("index"), 
      rs.getInt("size"), 
      rs.getString("path"), 
      rs.getTimestamp("uploadDate"), 
      rs.getString("fontAwesomeClassName"))
      
  implicit def toCourseDetailsSection(rs: ResultSet): CourseDetailsSection = newCourseDetailsSection(
      rs.getString("uuid"), 
      rs.getString("title"), 
      rs.getString("text"), 
      CourseDetailsEntityType.valueOf(rs.getString("entityType")), 
      rs.getString("entityUUID"), 
      rs.getInt("index"))
      
  implicit def toCertificateDetails(rs: ResultSet): CertificateDetails = newCertificateDetails(
      rs.getString("uuid"), 
      rs.getString("bgImage"),
      CertificateType.valueOf(rs.getString("certificateType")),
      CourseDetailsEntityType.valueOf(rs.getString("entityType")), 
      rs.getString("entityUUID"))
    
  implicit def toPostbackConfig(rs: ResultSet): PostbackConfig = newPostbackConfig(
      rs.getString("uuid"),
      rs.getString("institutionUUID"),
      PostbackType.valueOf(rs.getString("postbackType")),
      rs.getString("contents"))
}
