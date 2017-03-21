package kornell.server.repository

import java.math.BigDecimal
import java.util.Date
import scala.collection.JavaConverters.seqAsJavaListConverter
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.entity.AuthClientType
import kornell.core.entity.ChatThreadType
import kornell.core.entity.Course
import kornell.core.entity.CourseClass
import kornell.core.entity.CourseVersion
import kornell.core.entity.Enrollment
import kornell.core.entity.InstitutionRegistrationPrefix
import kornell.core.entity.Person
import kornell.core.entity.RegistrationType
import kornell.core.entity.Role
import kornell.core.to.ChatThreadMessageTO
import kornell.core.to.CourseClassTO
import kornell.core.to.CourseVersionTO
import kornell.core.to.CourseVersionsTO
import kornell.core.to.CoursesTO
import kornell.core.to.EnrollmentRequestTO
import kornell.core.to.EnrollmentRequestsTO
import kornell.core.to.EnrollmentTO
import kornell.core.to.EnrollmentsTO
import kornell.core.to.LibraryFileTO
import kornell.core.to.PersonTO
import kornell.core.to.RegistrationRequestTO
import kornell.core.to.RoleTO
import kornell.core.to.SimplePersonTO
import kornell.core.to.TOFactory
import kornell.core.to.UnreadChatThreadTO
import kornell.core.to.report.CertificateInformationTO
import kornell.core.to.report.CourseClassAuditTO
import kornell.core.to.report.CourseClassReportTO
import kornell.core.to.report.EnrollmentsBreakdownTO
import kornell.core.to.report.InstitutionBillingEnrollmentReportTO
import kornell.core.to.report.InstitutionBillingMonthlyReportTO
import kornell.core.util.StringUtils
import kornell.core.entity.InstitutionRegistrationPrefix
import kornell.core.to.PersonTO
import kornell.core.entity.AuthClientType
import kornell.core.to.SimplePersonTO
import kornell.core.to.EntityChangedEventsTO
import kornell.core.event.EntityChanged
import kornell.core.event.EventFactory
import kornell.core.entity.AuditedEntityType
import kornell.server.content.ContentManagers
import kornell.core.entity.RoleType
import kornell.server.util.DateConverter
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.core.to.CourseClassesTO
import kornell.core.to.DashboardLeaderboardItemTO

//TODO: Consider turning to Object
object TOs {
  val tos = AutoBeanFactorySource.create(classOf[TOFactory])
  val events = AutoBeanFactorySource.create(classOf[EventFactory])

  def newUserInfoTO = tos.newUserInfoTO.as
  def newUserHelloTO = tos.newUserHelloTO.as
  def newEnrollmentsTO: EnrollmentsTO = tos.newEnrollmentsTO.as
  def newCoursesTO: CoursesTO = tos.newCoursesTO.as
  def newCourseVersionsTO: CourseVersionsTO = tos.newCourseVersionsTO.as
  def newCourseClassesTO: CourseClassesTO = tos.newCourseClassesTO.as
  def newLibraryFileTO: LibraryFileTO = tos.newLibraryFileTO.as
  def newEntityChangedEventsTO: EntityChangedEventsTO = tos.newEntityChangedEventsTO.as

  def newEnrollmentsTO(enrollmentList: List[EnrollmentTO]): EnrollmentsTO = {
    val enrollments:EnrollmentsTO = newEnrollmentsTO
    enrollments.setEnrollmentTOs(enrollmentList asJava)
    enrollments.setPageCount(enrollmentList.length)
    enrollments
  }

  def newCoursesTO(coursesList: List[Course]): CoursesTO = {
    val courses = newCoursesTO
    courses.setCourses(coursesList asJava)
    courses.setPageCount(coursesList.length)
    courses
  }

  def newCourseVersionsTO(courseVersionsList: List[CourseVersion]): CourseVersionsTO = {
    val courseVersions = newCourseVersionsTO
    courseVersions.setCourseVersions(courseVersionsList asJava)
    courseVersions.setPageCount(courseVersionsList.length)
    courseVersions
  }

  def newCourseClassesTO(l: List[CourseClassTO]) = {
    val to = tos.newCourseClassesTO.as
    to.setCourseClasses(l asJava)
    to.setPageCount(l.length)
    to
  }

  def newCourseClassTO(course: Course, version: CourseVersion, clazz: CourseClass, registrationPrefix: String): CourseClassTO = {
    val classTO = tos.newCourseClassTO.as
    classTO.setCourseVersionTO(newCourseVersionTO(course, version))
    classTO.setCourseClass(clazz)
    classTO.setRegistrationPrefix(registrationPrefix)
    classTO
  }

  def newEnrollmentTO(enrollment: Enrollment, personUUID: String, fullName: String, username: String): EnrollmentTO = {
    val enrollmentTO:EnrollmentTO = tos.newEnrollmentTO.as
    enrollmentTO.setEnrollment(enrollment)
    enrollmentTO.setPersonUUID(personUUID)
    enrollmentTO.setFullName(fullName)
    enrollmentTO.setUsername(username)
    enrollmentTO
  }

  def newCourseVersionTO(course: Course, version: CourseVersion): CourseVersionTO = {
    val versionTO = tos.newCourseVersionTO.as
    val institutionRepo = InstitutionRepo(course.getInstitutionUUID)
    val repositoryUUID = institutionRepo.get.getAssetsRepositoryUUID
    val repo = ContentManagers.forRepository(repositoryUUID)
    versionTO.setDistributionURL(repo.url(""))
    versionTO.setCourse(course)
    versionTO.setCourseVersion(version)
    versionTO
  }

  def newRegistrationRequestTO: RegistrationRequestTO = tos.newRegistrationRequestTO.as
  def newRegistrationRequestTO(institutionUUID: String, fullName: String, email: String, password: String,cpf:String=null,username:String=null, registrationType: RegistrationType=null): RegistrationRequestTO = {
    val to = newRegistrationRequestTO
    to.setInstitutionUUID(institutionUUID)
    to.setFullName(fullName)
    to.setEmail(email)
    to.setUsername(username)
    to.setPassword(password)
    to.setCPF(cpf)
    to
  }

  def newEnrollmentRequestTO: EnrollmentRequestTO = tos.newEnrollmentRequestTO.as
  def newEnrollmentRequestTO(institutionUUID: String, courseClassUUID: String, fullName: String, username: String, password: String, registrationType: RegistrationType, institutionRegistrationPrefixUUID: String, cancelEnrollment: Boolean): EnrollmentRequestTO = {
    val to = newEnrollmentRequestTO
    to.setInstitutionUUID(institutionUUID)
    to.setCourseClassUUID(courseClassUUID)
    to.setFullName(fullName)
    to.setUsername(username)
    to.setPassword(password)
    to.setRegistrationType(registrationType)
    to.setInstitutionRegistrationPrefixUUID(institutionRegistrationPrefixUUID)
    to.setCancelEnrollment(cancelEnrollment)
    to
  }

  def newEnrollmentRequestsTO: EnrollmentRequestsTO = tos.newEnrollmentRequestsTO.as
  def newEnrollmentRequestsTO(enrollmentRequests: java.util.List[EnrollmentRequestTO]): EnrollmentRequestsTO = {
    val to = newEnrollmentRequestsTO
    to.setEnrollmentRequests(enrollmentRequests)
    to
  }

  def newEnrollmentsBreakdownTO: EnrollmentsBreakdownTO = new EnrollmentsBreakdownTO
  def newEnrollmentsBreakdownTO(name: String, count: Integer): EnrollmentsBreakdownTO = {
    val to = newEnrollmentsBreakdownTO
    to.setName(name)
    to.setCount(count)
    to
  }

  def newRoleTO(role: Role, person: Person, username: String) = {
    val r = tos.newRoleTO.as
    r.setRole(role)
    r.setPerson(person)
    r.setUsername(username)
    r
  }

  def newRolesTO(roleTOs: List[RoleTO]) = {
    val rs = tos.newRolesTO.as
    rs.setRoleTOs(roleTOs.asJava)
    rs
  }

  def newLibraryFilesTO(libraryFileTOs: List[LibraryFileTO]) = {
    val lf = tos.newLibraryFilesTO.as
    lf.setLibraryFiles(libraryFileTOs.asJava)
    lf
  }

  def newUnreadChatThreadsTO(l: List[UnreadChatThreadTO]) = {
    val to = tos.newUnreadChatThreadsTO.as
    to.setUnreadChatThreadTOs(l asJava)
    to
  }

  def newUnreadChatThreadTO: UnreadChatThreadTO = tos.newUnreadChatThreadTO.as 
  def newUnreadChatThreadTO(unreadMessages: String, chatThreadUUID: String, supportType: String, creatorName: String, entityUUID: String, entityName: String): UnreadChatThreadTO = {
    val to = newUnreadChatThreadTO
    to.setUnreadMessages(unreadMessages)
    to.setChatThreadUUID(chatThreadUUID)
    to.setThreadType(ChatThreadType.valueOf(supportType))
    to.setChatThreadCreatorName(creatorName)
    to.setEntityUUID(entityUUID)
    to.setEntityName(entityName)
    to
  }

  def newChatThreadMessagesTO(l: List[ChatThreadMessageTO]) = {
    val to = tos.newChatThreadMessagesTO.as
    to.setChatThreadMessageTOs(l asJava)
    to.setServerTime(new Date)
    to
  }

  def newChatThreadMessageTO: ChatThreadMessageTO = tos.newChatThreadMessageTO.as 
  def newChatThreadMessageTO(senderFullName: String, senderRole: RoleType, sentAt: Date, message: String): ChatThreadMessageTO = {
    val to = newChatThreadMessageTO
    to.setSenderFullName(senderFullName)
    to.setSenderRole(senderRole)
    to.setSentAt(sentAt)
    to.setMessage(message)
    to
  }

  def newInstitutionRegistrationPrefixesTO(l: List[InstitutionRegistrationPrefix]) = {
    val to = tos.newInstitutionRegistrationPrefixesTO.as
    to.setInstitutionRegistrationPrefixes(l asJava)
    to
  }
  
  def newInstitutionHostNamesTO(l: List[String]) = {
    val to = tos.newInstitutionHostNamesTO().as
    to.setInstitutionHostNames(l asJava)
    to
  }
  
  def newInstitutionEmailWhitelistTO(l: List[String]) = {
    val to = tos.newInstitutionEmailWhitelistTO().as
    to.setDomains(l asJava)
    to
  }

  def newInstitutionBillingEnrollmentReportTO: InstitutionBillingEnrollmentReportTO = new InstitutionBillingEnrollmentReportTO
  def newInstitutionBillingEnrollmentReportTO(enrollmentUUID: String, courseTitle: String, courseVersionName: String, courseClassName: String, fullName: String, username: String, firstEventFiredAt: Date): InstitutionBillingEnrollmentReportTO = {
    val to = newInstitutionBillingEnrollmentReportTO
    to.setEnrollmentUUID(enrollmentUUID)
    to.setCourseTitle(courseTitle)
    to.setCourseVersionName(courseVersionName)
    to.setCourseClassName(courseClassName)
    to.setFullName(fullName)
    to.setUsername(username)
    to.setFirstEventFiredAt(DateConverter.convertDate(firstEventFiredAt))
    to
  }

  def newInstitutionBillingMonthlyReportTO: InstitutionBillingMonthlyReportTO = new InstitutionBillingMonthlyReportTO
  def newInstitutionBillingMonthlyReportTO(personUUID: String, fullName: String, username: String): InstitutionBillingMonthlyReportTO = {
    val to = newInstitutionBillingMonthlyReportTO
    to.setPersonUUID(personUUID)
    to.setFullName(fullName)
    to.setUsername(username)
    to
  }

  def newCourseClassAuditTO: CourseClassAuditTO = new CourseClassAuditTO
  def newCourseClassAuditTO(eventFiredAt: Date, eventType: String, adminFullName: String, adminUsername: String, participantFullName: String, participantUsername: String, fromCourseClassName: String, toCourseClassName: String, fromState: String, toState: String, adminUUID: String, participantUUID: String, enrollmentUUID: String, fromCourseClassUUID: String, toCourseClassUUID: String): CourseClassAuditTO = {
    val to = newCourseClassAuditTO
    to.setEventFiredAt(DateConverter.convertDate(eventFiredAt))
  	to.setEventType(eventType)
  	to.setAdminFullName(adminFullName)
  	to.setAdminUsername(adminUsername)
  	to.setParticipantFullName(participantFullName)
  	to.setParticipantUsername(participantUsername)
  	to.setFromCourseClassName(fromCourseClassName)
  	to.setToCourseClassName(toCourseClassName)
  	to.setFromState(fromState)
  	to.setToState(toState)
  	to.setAdminUUID(adminUUID)
  	to.setParticipantUUID(participantUUID)
  	to.setEnrollmentUUID(enrollmentUUID)
  	to.setFromCourseClassUUID(fromCourseClassUUID)
  	to.setToCourseClassUUID(toCourseClassUUID)
    to
  }

  def newPeopleTO(people: List[PersonTO]) = {
    val ps = tos.newPeopleTO.as
    ps.setPeopleTO(people.asJava)
    ps
  }
  
  def newPersonTO(person: Person, username: String) = {
    val p = tos.newPersonTO.as
    p.setPerson(person)
    p.setUsername(username)
    p
  }
  
  def newTokenTO(token: String, expiry: Date, personUUID: String, clientType: AuthClientType) = {
    val to = tos.newTokenTO.as
    to.setToken(token)
    to.setExpiry(expiry)
    to.setPersonUUID(personUUID)
    to.setClientType(clientType)
    to
  }
  
  def newSimplePersonTO(personUUID: String, fullName: String, username: String) = {
    val to = tos.newSimplePersonTO.as
    to.setPersonUUID(personUUID)
    to.setFullName(fullName)
    to.setUsername(username)
    to
  }
  
  def newSimplePeopleTO(simplePeople: List[SimplePersonTO]) = {
    val to = tos.newSimplePeopleTO.as
    to.setSimplePeopleTO(simplePeople.asJava)
    to
  }
  
  def newDashboardLeaderboardItemTO(personUUID: String, fullName: String, attribute: String) = {
    val to = tos.newDashboardLeaderboardItemTO.as
    to.setPersonUUID(personUUID)
    to.setFullName(fullName)
    to.setAttribute(attribute)
    to
  }
  
  def newDashboardLeaderboardTO(dashboardLeaderboardItems: List[DashboardLeaderboardItemTO]) = {
    val to = tos.newDashboardLeaderboardTO.as
    to.setDashboardLeaderboardItems(dashboardLeaderboardItems.asJava)
    to
  }

  def newEnrollmentLaunchTO() = {
    val to = tos.newEnrollmentLaunchTO().as()
    to
  }
  
  def newEntityChanged(uuid: String, eventFiredAt: Date, institutionUUID: String, fromPersonUUID: String, entityType: AuditedEntityType, entityUUID: String, fromValue: String, toValue: String, entityName: String, fromPersonName: String, fromUsername: String) = {
    val event = events.newEntityChanged.as
    event.setUUID(uuid)
    event.setEventFiredAt(DateConverter.convertDate(eventFiredAt))
  	event.setInstitutionUUID(institutionUUID)
  	event.setFromPersonUUID(fromPersonUUID)
  	event.setEntityType(entityType)
  	event.setEntityUUID(entityUUID)
  	event.setFromValue(fromValue)
  	event.setToValue(toValue)
  	event.setEntityName(entityName)
  	event.setFromPersonName(fromPersonName)
  	event.setFromUsername(fromUsername)
    event
  }

  def newEntityChangedEventsTO(entitiesChangedList: List[EntityChanged]): EntityChangedEventsTO = {
    val courses = newEntityChangedEventsTO
    courses.setEntitiesChanged(entitiesChangedList asJava)
    courses.setPageCount(entitiesChangedList.length)
    courses
  }
}
