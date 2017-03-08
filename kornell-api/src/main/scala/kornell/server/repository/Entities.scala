package kornell.server.repository

import java.math.BigDecimal
import java.util.Date
import java.util.HashMap
import java.util.Map
import scala.collection.JavaConverters.seqAsJavaListConverter
import org.joda.time.LocalDate
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.entity.ActomEntries
import kornell.core.entity.Assessment
import kornell.core.entity.BillingType
import kornell.core.entity.ContentSpec
import kornell.core.entity.Course
import kornell.core.entity.CourseClassState
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentEntries
import kornell.core.entity.EnrollmentState
import kornell.core.entity.EntityFactory
import kornell.core.entity.InstitutionRegistrationPrefix
import kornell.core.entity.InstitutionType
import kornell.core.entity.Person
import kornell.core.entity.RegistrationType
import kornell.core.entity.Role
import kornell.core.entity.RoleType
import kornell.core.util.StringUtils
import kornell.core.util.UUID
import kornell.server.util.DateConverter
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.jdbc.repository.CourseRepo
import kornell.core.entity.RepositoryType
import kornell.core.entity.CourseDetailsEntityType
import kornell.core.entity.CertificateType
import kornell.core.entity.EnrollmentSource
import kornell.core.entity.PostbackType


//TODO: Remove this class without spreading dependency on AutoBeanFactorySource
object Entities {
  val factory = AutoBeanFactorySource.create(classOf[EntityFactory])

  def randUUID = UUID.randomUUID.toString

  def newPerson: Person = factory.newPerson.as

  def newPerson(
    uuid: String = null,
    fullName: String = null,
    lastPlaceVisited: String = null,
    email: String = null,
    company: String = null,
    title: String = null,
    sex: String = null,
    birthDate: Date = null,
    confirmation: String = null,
    telephone: String = null,
    country: String = null,
    state: String = null,
    city: String = null,
    addressLine1: String = null,
    addressLine2: String = null,
    postalCode: String = null,
    cpf: String = null,
    institutionUUID: String = null,
    termsAcceptedOn: Date = null,
    registrationType: RegistrationType = null,
    institutionRegistrationPrefixUUID: String = null,
    receiveEmailCommunication: Boolean = true,
    forcePasswordUpdate: Boolean = false) = {
    //in some case, we new a person and no one is authenticated
	val dateConverter = DateConverter()
    val person = factory.newPerson.as
    person.setUUID(uuid)
    person.setFullName(fullName)
    person.setLastPlaceVisited(lastPlaceVisited)
    person.setEmail(email)
    person.setCompany(company)
    person.setTitle(title)
    person.setSex(sex)
    person.setBirthDate(new LocalDate(birthDate).toDate)
    person.setConfirmation(confirmation)
    person.setTelephone(telephone)
    person.setCountry(country)
    person.setState(state)
    person.setCity(city)
    person.setAddressLine1(addressLine1)
    person.setAddressLine2(addressLine2)
    person.setPostalCode(postalCode)
    person.setCPF(cpf)
    person.setInstitutionUUID(institutionUUID)
    person.setTermsAcceptedOn(dateConverter.dateToInstitutionTimezone(termsAcceptedOn, institutionUUID))
    person.setRegistrationType(registrationType)
    person.setInstitutionRegistrationPrefixUUID(institutionRegistrationPrefixUUID)
    person.setReceiveEmailCommunication(receiveEmailCommunication)
    person.setForcePasswordUpdate(forcePasswordUpdate)
    person
  }

  def newPeople(people: List[Person]) = {
    val ps = factory.newPeople.as
    ps.setPeople(people.asJava)
    ps
  }

  def newPrincipal(uuid: String, personUUID: String, username: String) = {
    val principal = factory.newPrincipal.as
    principal.setUUID(uuid)
    principal.setPersonUUID(personUUID)
    principal.setUsername(username)
    principal
  }

  def newCourse(uuid: String = randUUID, code: String = null,
    title: String = null, description: String = null,
    infoJson: String = null,
    institutionUUID: String = null,
    childCourse: Boolean): Course = {
    val c = factory.newCourse.as
    c.setUUID(uuid)
    c.setCode(code)
    c.setDescription(description)
    c.setTitle(title)
    c.setInfoJson(infoJson)
    c.setInstitutionUUID(institutionUUID)
    c.setChildCourse(childCourse)
    c
  }

  implicit def toEnrollments(enrollments: List[Enrollment]) = {
    val es = factory.newEnrollments.as
    es.setEnrollments(enrollments.asJava)
    es
  }

  def newRoles(roles: List[Role]) = {
    val rs = factory.newRoles.as
    rs.setRoles(roles.asJava)
    rs
  }

  def newEnrollment(uuid: String = randUUID, enrolledOn: Date = null,
    courseClassUUID: String, personUUID: String,
    progress: Integer = 0, notes: String = null,
    state: EnrollmentState, lastProgressUpdate: Date = null,
    assessment: Assessment = null, lastAssessmentUpdate: Date = null,
    assessmentScore: BigDecimal = null, certifiedAt: Date = null,
    courseVersionUUID: String = null, parentEnrollmentUUID:String = null,
    startDate:Date=null,endDate:Date=null,
    preAssessment:BigDecimal=null,postAssessment:BigDecimal=null, enrollmentSource:EnrollmentSource=null): Enrollment = {
    val dateConverter = new DateConverter(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.getOrElse(null))
    val e = factory.enrollment.as
    e.setUUID(uuid)
    e.setCourseClassUUID(courseClassUUID)
    e.setPersonUUID(personUUID)
    e.setProgress(progress)
    e.setNotes(notes)
    e.setState(state)
    e.setAssessment(assessment)
    e.setAssessmentScore(assessmentScore)
    e.setCourseVersionUUID(courseVersionUUID)
    e.setParentEnrollmentUUID(parentEnrollmentUUID)
    e.setStartDate(startDate)
    e.setEndDate(endDate)
    e.setPreAssessmentScore(preAssessment)
    e.setPostAssessmentScore(postAssessment)
    e.setEnrollmentSource(enrollmentSource)
    if (courseClassUUID != null){
      e.setEnrolledOn(dateConverter.dateToInstitutionTimezoneFromCourseClass(enrolledOn, courseClassUUID))
      e.setLastProgressUpdate(dateConverter.dateToInstitutionTimezoneFromCourseClass(lastProgressUpdate, courseClassUUID))
      e.setLastAssessmentUpdate(dateConverter.dateToInstitutionTimezoneFromCourseClass(lastAssessmentUpdate, courseClassUUID))
      e.setCertifiedAt(dateConverter.dateToInstitutionTimezoneFromCourseClass(certifiedAt, courseClassUUID))
    } else {
      e.setEnrolledOn(dateConverter.dateToInstitutionTimezone(enrolledOn))
      e.setLastProgressUpdate(dateConverter.dateToInstitutionTimezone(lastProgressUpdate))
      e.setLastAssessmentUpdate(dateConverter.dateToInstitutionTimezone(lastAssessmentUpdate))
      e.setCertifiedAt(dateConverter.dateToInstitutionTimezone(certifiedAt))
    }
    e
  }

  def newEnrollments(enrollments: List[Enrollment]) = {
    val ps = factory.newEnrollments.as
    ps.setEnrollments(enrollments.asJava)
    ps
  }

  //FTW: Default parameter values
  def newInstitution(uuid: String = randUUID, name: String, fullName: String, terms: String, baseURL: String, 
      demandsPersonContactDetails: Boolean, validatePersonContactDetails: Boolean, allowRegistration: Boolean, allowRegistrationByUsername: Boolean, 
      activatedAt: Date, skin: String, billingType: BillingType, institutionType: InstitutionType, dashboardVersionUUID: String, internationalized: Boolean, 
      useEmailWhitelist: Boolean = false,assetsRepositoryUUID:String=null, timeZone: String, institutionSupportEmail: String) = {
    val i = factory.newInstitution.as
    i.setName(name)
    i.setFullName(fullName)
    i.setUUID(uuid)
    if (terms != null)
      i.setTerms(terms.stripMargin)
    i.setAssetsRepositoryUUID(assetsRepositoryUUID);
    i.setBaseURL(baseURL)
    i.setDemandsPersonContactDetails(demandsPersonContactDetails)
    i.setValidatePersonContactDetails(validatePersonContactDetails)
    i.setAllowRegistration(allowRegistration)
    i.setAllowRegistrationByUsername(allowRegistrationByUsername)
    i.setActivatedAt(activatedAt)
    i.setSkin(skin)
    i.setBillingType(billingType)
    i.setInstitutionType(institutionType)
    i.setDashboardVersionUUID(dashboardVersionUUID)
    i.setInternationalized(internationalized)
    i.setUseEmailWhitelist(useEmailWhitelist)
    i.setTimeZone(timeZone)
    i.setInstitutionSupportEmail(institutionSupportEmail)
    i
  }

  lazy val newUserRole = {
    val role = factory.newRole().as
    role.setRoleType(RoleType.user)
    role.setUserRole(factory.newUserRole().as())
    role
  }

  def newRoleAsPlatformAdmin(person_uuid: String, institutionUUID: String): Role = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val platformAdminRole = factory.newPlatformAdminRole().as
    platformAdminRole.setInstitutionUUID(institutionUUID)
    role.setRoleType(RoleType.platformAdmin)
    role.setPlatformAdminRole(platformAdminRole)
    role
  }

  def newInstitutionAdminRole(person_uuid: String, institutionUUID: String) = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val institutionAdminRole = factory.newInstitutionAdminRole().as
    institutionAdminRole.setInstitutionUUID(institutionUUID)
    role.setRoleType(RoleType.institutionAdmin)
    role.setInstitutionAdminRole(institutionAdminRole)
    role
  }

  def newCourseClassAdminRole(person_uuid: String, courseClassUUID: String) = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val courseClassAdminRole = factory.newCourseClassAdminRole().as
    courseClassAdminRole.setCourseClassUUID(courseClassUUID)
    role.setRoleType(RoleType.courseClassAdmin)
    role.setCourseClassAdminRole(courseClassAdminRole)
    role
  }
  
  def newTutorRole(person_uuid: String, courseClassUUID: String) = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val tutorRole = factory.newTutorRole().as
    tutorRole.setCourseClassUUID(courseClassUUID)
    role.setRoleType(RoleType.tutor)
    role.setTutorRole(tutorRole)
    role
  }
  
  def newObserverRole(person_uuid: String, courseClassUUID: String) = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val observerRole = factory.newObserverRole().as
    observerRole.setCourseClassUUID(courseClassUUID)
    role.setRoleType(RoleType.observer)
    role.setObserverRole(observerRole)
    role
  }
  
  def newControlPanelAdminRole(person_uuid: String) = {
    val role = factory.newRole().as
    role.setPersonUUID(person_uuid)
    val controlPanelAdminRole = factory.newControlPanelAdminRole().as
    role.setRoleType(RoleType.controlPanelAdmin)
    role.setControlPanelAdminRole(controlPanelAdminRole)
    role
  }

  def newCourseVersion(
    uuid: String = randUUID, name: String = null, 
    courseUUID: String = null, versionCreatedAt: Date = new Date, distributionPrefix: String = null, 
    contentSpec: String = null, disabled: Boolean = false, parentVersionUUID: String = null,
    instanceCount: Integer = 1, label: String = null) = {
    val dateConverter = new DateConverter(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.getOrElse(""))
    val versionCreatedAtConverted = {
      if(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.isDefined) dateConverter.dateToInstitutionTimezone(versionCreatedAt)
      else dateConverter.dateToInstitutionTimezone(versionCreatedAt, CourseRepo(courseUUID).get.getInstitutionUUID)
    }
    val version = factory.newCourseVersion.as
    version.setUUID(uuid);
    version.setName(name);
    version.setCourseUUID(courseUUID);
    version.setVersionCreatedAt(versionCreatedAtConverted)
    version.setDistributionPrefix(distributionPrefix)
    version.setDisabled(disabled)
    version.setParentVersionUUID(parentVersionUUID)
    version.setInstanceCount(instanceCount)
    version.setLabel(label)
    Option(contentSpec) foreach { spec =>
      val cSpec = ContentSpec.valueOf(spec)
      version.setContentSpec(cSpec);
    }
    version
  }

  def newCourseClass(uuid: String = null, name: String = null,
    courseVersionUUID: String = null, institutionUUID: String = null,
    requiredScore: BigDecimal = null, publicClass: Boolean = false,
    overrideEnrollments: Boolean = false,
    invisible: Boolean = false, maxEnrollments: Integer = null,
    createdAt: Date = null, createdBy: String = null,
    state: CourseClassState = null,
    registrationType: RegistrationType = null,
    institutionRegistrationPrefixUUID: String = null,
    courseClassChatEnabled: Boolean = false,
    chatDockEnabled: Boolean = false,
    allowBatchCancellation: Boolean = false,
    tutorChatEnabled: Boolean = false,
    approveEnrollmentsAutomatically: Boolean = false,
    startDate:Date = null, pagseguroId: String = null) = {
    val dateConverter = new DateConverter(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.getOrElse(""))
    val createdAtConverted = {
      if(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.isDefined) dateConverter.dateToInstitutionTimezone(createdAt)
      else dateConverter.dateToInstitutionTimezone(createdAt, institutionUUID)
    }
    val clazz = factory.newCourseClass.as
    clazz.setUUID(uuid)
    clazz.setName(name)
    clazz.setCourseVersionUUID(courseVersionUUID)
    clazz.setInstitutionUUID(institutionUUID)
    clazz.setRequiredScore(requiredScore)
    clazz.setPublicClass(publicClass)
    clazz.setOverrideEnrollments(overrideEnrollments)
    clazz.setInvisible(invisible)
    clazz.setMaxEnrollments(maxEnrollments)
    clazz.setCreatedAt(createdAtConverted)
    clazz.setCreatedBy(createdBy)
    clazz.setState(state)
    clazz.setRegistrationType(registrationType)
    clazz.setInstitutionRegistrationPrefixUUID(institutionRegistrationPrefixUUID)
    clazz.setCourseClassChatEnabled(courseClassChatEnabled)
    clazz.setChatDockEnabled(chatDockEnabled)
    clazz.setAllowBatchCancellation(allowBatchCancellation)
	  clazz.setTutorChatEnabled(tutorChatEnabled)
	  clazz.setApproveEnrollmentsAutomatically(approveEnrollmentsAutomatically)
	  clazz.setStartDate(startDate);
    clazz.setPagseguroId(pagseguroId)
    clazz
  }

  def newActomEntries(enrollmentUUID: String, actomKey: String, entriesMap: Map[String, String]) = {
    val entries = factory.newActomEntries.as
    entries.setActomKey(actomKey)
    entries.setEnrollmentUUID(enrollmentUUID)
    entries.setEntries(entriesMap)
    entries
  }

  def newContentRepository(uuid: String = null,
    repositoryType: RepositoryType = null,
    accessKeyId: String = null,
    secretAccessKey: String = null,
    bucketName: String = null,
    prefix: String = null,
    region: String = null,
    institutionUUID: String = null,
    path: String = null) = {
    val repo = factory.newContentRepository.as
    repo.setUUID(uuid)
    repo.setRepositoryType(repositoryType)
    repo.setAccessKeyId(accessKeyId)
    repo.setBucketName(bucketName)
    repo.setPrefix(prefix)
    repo.setRegion(region)
    repo.setSecretAccessKey(secretAccessKey)
    repo.setInstitutionUUID(institutionUUID)
    repo.setPath(path)
    repo
  }

  def newChatThread(uuid: String = null, createdAt: Date = null, institutionUUID: String = null, courseClassUUID: String = null, personUUID: String = null, threadType: String = null, active: Boolean = true) = {
    val dateConverter = new DateConverter(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.getOrElse(null))
    val chatThread = factory.newChatThread.as
    chatThread.setUUID(uuid)
    chatThread.setCreatedAt(dateConverter.dateToInstitutionTimezoneFromCourseClass(createdAt, courseClassUUID))
    chatThread.setInstitutionUUID(institutionUUID)
    chatThread.setCourseClassUUID(courseClassUUID)
    chatThread.setPersonUUID(personUUID)
    chatThread.setThreadType(threadType)
    chatThread.setActive(active)
    chatThread
  }

  def newChatThreadParticipant(uuid: String = null, chatThreadUUID: String = null, personUUID: String = null, 
      chatThreadName: String = null, lastReadAt: Date = null, active: Boolean = false, lastJoinDate: Date = null) = {
    val dateConverter = new DateConverter(personUUID)
    val chatThreadParticipant = factory.newChatThreadParticipant.as
    chatThreadParticipant.setUUID(uuid)
    chatThreadParticipant.setThreadUUID(chatThreadUUID)
    chatThreadParticipant.setPersonUUID(personUUID)
    chatThreadParticipant.setLastReadAt(dateConverter.dateToInstitutionTimezone(lastReadAt))
    chatThreadParticipant.setActive(active)
    chatThreadParticipant.setLastJoinDate(dateConverter.dateToInstitutionTimezone(lastJoinDate))
    chatThreadParticipant
  }

  def newInstitutionRegistrationPrefix(uuid: String, name: String, institutionUUID: String = null, showEmailOnProfile: Boolean, showCPFOnProfile: Boolean, showContactInformationOnProfile: Boolean): InstitutionRegistrationPrefix = {
    val institutionRegistrationPrefix = factory.newInstitutionRegistrationPrefix.as
    institutionRegistrationPrefix.setUUID(uuid)
    institutionRegistrationPrefix.setName(name)
    institutionRegistrationPrefix.setInstitutionUUID(institutionUUID)
    institutionRegistrationPrefix.setShowEmailOnProfile(showEmailOnProfile)
    institutionRegistrationPrefix.setShowCPFOnProfile(showCPFOnProfile)
    institutionRegistrationPrefix.setShowContactInformationOnProfile(showContactInformationOnProfile)
    institutionRegistrationPrefix
  }

  def newEnrollmentsEntries() = {
    val esEntries = factory.newEnrollmentsEntries().as
    esEntries.setEnrollmentEntriesMap(new HashMap[String,EnrollmentEntries]())
    esEntries
  }
  
  def newEnrollmentEntries = {
    val eEntries = factory.newEnrollmentEntries.as
    eEntries.setActomEntriesMap(new HashMap[String,ActomEntries]())
    eEntries
  }

  def newCourseDetailsHint(uuid:String, text:String, entityType:CourseDetailsEntityType, entityUUID:String, index:Integer, fontAwesomeClassName:String) = {
    val hint = factory.newCourseDetailsHint.as
    hint.setUUID(uuid)
    hint.setText(text)
    hint.setEntityType(entityType)
    hint.setEntityUUID(entityUUID)
    hint.setIndex(index)
    hint.setFontAwesomeClassName(fontAwesomeClassName)
    hint
  }
  
  def newCourseDetailsLibrary(uuid: String, title: String, description: String, entityType:CourseDetailsEntityType, entityUUID:String, index:Integer, size:Integer, path:String, uploadDate:Date, fontAwesomeClassName:String) = {
    val library = factory.newCourseDetailsLibrary.as
    library.setUUID(uuid)
    library.setTitle(title)
    library.setDescription(description)
    library.setEntityType(entityType)
    library.setEntityUUID(entityUUID)
    library.setIndex(index)
    library.setFontAwesomeClassName(fontAwesomeClassName)
    library.setSize(size)
    library.setPath(path)
    library.setUploadDate(uploadDate)
    library
  }
  
  def newCourseDetailsSection(uuid: String, title: String, text: String, entityType:CourseDetailsEntityType, entityUUID:String, index:Integer) = {
    val section = factory.newCourseDetailsSection.as
    section.setUUID(uuid)
    section.setTitle(title)
    section.setText(text)
    section.setEntityType(entityType)
    section.setEntityUUID(entityUUID)
    section.setIndex(index)
    section
  }
  
  def newCertificateDetails(uuid: String, bgImage: String, certificateType: CertificateType, entityType:CourseDetailsEntityType, entityUUID:String) = {
    val certificateDetails = factory.newCertificateDetails().as
    certificateDetails.setUUID(uuid)
    certificateDetails.setBgImage(bgImage)
    certificateDetails.setCertificateType(certificateType)
    certificateDetails.setEntityType(entityType)
    certificateDetails.setEntityUUID(entityUUID)
    certificateDetails
  }
  
  def newPostbackConfig(uuid: String, instutionUUID: String, postbackType: PostbackType, contents: String) = {
    val config = factory.newPostbackConfig.as
    config.setUUID(uuid)
    config.setInstitutionUUID(uuid)
    config.setPostbackType(postbackType)
    config.setContents(contents)
    config
  }
}

