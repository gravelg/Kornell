package kornell.server.repository

import java.math.BigDecimal
import java.util.Date
import java.util.UUID
import scala.collection.JavaConverters.seqAsJavaListConverter
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.entity.Course
import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.core.entity.EntityFactory
import kornell.core.entity.Institution
import kornell.core.entity.Person
import kornell.core.entity.Registration
import kornell.core.entity.RoleType
import kornell.server.jdbc.repository.PersonRepo
import java.util.Map

object Entities {
  val factory = AutoBeanFactorySource.create(classOf[EntityFactory])

  def randUUID = UUID.randomUUID.toString

  def newPerson: Person = factory.newPerson.as

  def newPerson(uuid: String, fullName: String, lastPlaceVisited: String = null,
      	email: String, company: String, title: String, sex: String, 
      	birthDate: Date, confirmation: String, telephone: String, country: String, 
      	state: String, city: String, addressLine1: String, addressLine2: String, postalCode: String) = {
    val person = factory.newPerson.as
    person.setUUID(uuid)
    person.setFullName(fullName)
    person.setLastPlaceVisited(lastPlaceVisited)
	person.setEmail(email)
    person.setCompany(company)
    person.setTitle(title)
    person.setSex(sex)
    person.setBirthDate(birthDate)
    person.setConfirmation(confirmation)
    person.setTelephone(telephone)
    person.setCountry(country)
    person.setState(state)
    person.setCity(city)
    person.setAddressLine1(addressLine1)
    person.setAddressLine2(addressLine2)
    person.setPostalCode(postalCode)
    person
  }

  def newPrincipal(uuid: String, personUUID: String, username: String) = {
    val principal = factory.newPrincipal.as
    principal.setUUID(uuid)
    principal.setPersonUUID(personUUID)
    principal.setUsername(username)
    principal
  }

  def newCourse: Course = factory.newCourse.as

  def newCourse(uuid: String, code: String,
    title: String, description: String,
    infoJson: String): Course = {
    val c = newCourse
    c.setUUID(uuid)
    c.setCode(code)
    c.setDescription(description)
    c.setTitle(title)
    c.setInfoJson(infoJson)
    c
  }
  
  def newEnrollments(enrollments: List[Enrollment]) = {
    val es = factory.newEnrollments.as
    es.setEnrollments(enrollments.asJava)
    es
  }

  def newEnrollment(uuid: String, enrolledOn: Date, courseClassUUID: String, personUUID: String, progress: Integer, notes: String, state: EnrollmentState): Enrollment = {
    val e = factory.newEnrollment.as
    e.setUUID(uuid)
    e.setEnrolledOn(enrolledOn)
    e.setCourseClassUUID(courseClassUUID)
    e.setPerson(PersonRepo(personUUID).get.get)
    e.setProgress(progress)
    e.setNotes(notes)
    e.setState(state)
    e
  }

  //FTW: Default parameter values
  def newInstitution(uuid: String = randUUID, name: String, fullName: String, terms: String, assetsURL: String, baseURL: String, demandsPersonContactDetails: Boolean) = {
    val i = factory.newInstitution.as
    i.setName(name)
    i.setFullName(fullName)
    i.setUUID(uuid)
    i.setTerms(terms.stripMargin)
    i.setAssetsURL(assetsURL)
    i.setBaseURL(baseURL)
    i.setDemandsPersonContactDetails(demandsPersonContactDetails)
    i
  }

  def newRegistration(p: Person, i: Institution): Registration = {
    val r = newRegistration
    r.setPersonUUID(p.getUUID)
    r.setInstitutionUUID(i.getUUID)
    r
  }

  def newRegistration = factory.newRegistration.as

  def newRegistration(personUUID: String, institutionUUID: String, termsAcceptedOn: Date): Registration = {
    val r = newRegistration
    r.setPersonUUID(personUUID)
    r.setInstitutionUUID(institutionUUID)
    r.setTermsAcceptedOn(termsAcceptedOn)
    r
  }

  def newRegistrations = factory.newRegistrations.as

  
  lazy val newUserRole = {
    val role = factory.newRole().as
    role.setRoleType(RoleType.user)
    role.setUserRole(factory.newUserRole().as())
    role
  }
  
  lazy val newPlatformAdminRole = {
    val role = factory.newRole().as
    role.setRoleType(RoleType.platformAdmin)
    role.setPlatformAdminRole(factory.newPlatformAdminRole().as())
    role
  }
  
  def newInstitutionAdminRole(institutionUUID:String) = {
    val role = factory.newRole().as
    val institutionAdminRole = factory.newInstitutionAdminRole().as
    institutionAdminRole.setInstitutionUUID(institutionUUID)
    role.setRoleType(RoleType.institutionAdmin)    
    role.setInstitutionAdminRole(institutionAdminRole)
    role
  }
  
  def newCourseClassAdminRole(courseClassUUID:String) = {
    val role = factory.newRole().as
    val courseClassAdminRole = factory.newCourseClassAdminRole().as
    courseClassAdminRole.setCourseClassUUID(courseClassUUID)
    role.setRoleType(RoleType.courseClassAdmin)    
    role.setCourseClassAdminRole(courseClassAdminRole)
    role
  }
 
  def newCourseVersion(uuid:String,name:String,courseUUID:String,repositoryUUID:String,versionCreatedAt:Date = new Date,distributionPrefix:String)={
    val version = factory.newCourseVersion.as
    version.setUUID(uuid);
    version.setName(name);
    version.setCourseUUID(courseUUID);
    version.setRepositoryUUID(repositoryUUID);
    version.setVersionCreatedAt(versionCreatedAt)
    version.setDistributionPrefix(distributionPrefix)
    version
  }
  
  def newCourseClass(uuid:String,name:String,courseVersionUUID:String,institutionUUID:String) = {
    val clazz = factory.newCourseClass.as
    clazz.setUUID(uuid)
    clazz.setName(name)
    clazz.setCourseVersionUUID(courseVersionUUID)
    clazz.setInstitutionUUID(institutionUUID)
    clazz
  }
  
  def newWebRepository(uuid:String,distributionURL:String,prefix:String) = {
    val webRepo = factory.newWebReposiory.as
    webRepo.setUUID(uuid)
    webRepo.setPrefix(prefix)
    webRepo.setDistributionURL(distributionURL)
    webRepo
  }
  
  def newActomEntries(enrollmentUUID:String,actomKey:String, entriesMap:Map[String,String]) = {
    val entries = factory.newActomEntries.as
    entries.setActomKey(actomKey)
    entries.setEnrollmentUUID(enrollmentUUID)
    entries.setEntries(entriesMap)
    entries
  }

}
