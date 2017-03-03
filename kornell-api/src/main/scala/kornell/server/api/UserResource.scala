package kornell.server.api

import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.setAsJavaSetConverter
import scala.collection.immutable.Set
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.Person
import kornell.core.entity.RegistrationType
import kornell.core.error.exception.EntityNotFoundException
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.to.RegistrationRequestTO
import kornell.core.to.UserInfoTO
import kornell.core.util.UUID
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.EnrollmentsRepo
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.PeopleRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.repository.Entities.newEnrollments
import kornell.server.repository.TOs
import kornell.server.repository.TOs.newUserHelloTO
import kornell.server.repository.TOs.newUserInfoTO
import kornell.server.service.RegistrationEnrollmentService
import kornell.server.util.EmailService
import kornell.server.web.BasicAuthFilter
import kornell.core.to.UserHelloTO
import kornell.server.jdbc.repository.TokenRepo
import kornell.server.util.Conditional.toConditional
import javax.ws.rs.POST
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.RolesRepo
import kornell.core.entity.RoleCategory
import kornell.server.jdbc.repository.EventsRepo
import kornell.core.entity.AuditedEntityType
import kornell.server.jdbc.repository.CourseClassesRepo
//TODO Person/People Resource
@Path("user")
class UserResource(private val authRepo: AuthRepo) {
  def this() = this(AuthRepo())

  @GET
  @Path("login")
  @Produces(Array(UserHelloTO.TYPE))
  def get(@Context req: HttpServletRequest,
    @QueryParam("name") name: String,
    @QueryParam("hostName") hostName: String) = {
    val userHello = newUserHelloTO

    val institution = {
      if(name != null) InstitutionsRepo.getByName(name)
      else if(hostName != null) InstitutionsRepo.getByHostName(hostName)
      else None
    }
    userHello.setInstitution(institution.getOrElse(null));
    val auth = req.getHeader("X-KNL-TOKEN")
    
    val token = TokenRepo().checkToken(auth)
    if (token.isDefined) {
      val person = PersonRepo(token.get.getPersonUUID).first.getOrElse(null)
      userHello.setUserInfoTO(getUser(person).getOrElse(null))
      userHello.setCourseClassesTO(CourseClassesRepo.byPersonAndInstitution(person.getUUID, person.getInstitutionUUID))
      
      if(institution.isDefined && person.getInstitutionUUID != institution.get.getUUID){
        throw new UnauthorizedAccessException("personDoesNotBelongToInstitution")
      }
    }

    userHello
  }

  def getUser(person: Person) = {
    val user = newUserInfoTO
    val username = PersonRepo(person.getUUID).getUsername
    user.setUsername(username)
    user.setPerson(person)
    user.setLastPlaceVisited(person.getLastPlaceVisited)
    val roleTOs = RolesRepo.getUserRoles(person.getUUID, RoleCategory.BIND_DEFAULT)
    user.setRoles(roleTOs.getRoleTOs)
    if (RegistrationType.username.equals(person.getRegistrationType)) {
      user.setInstitutionRegistrationPrefix(InstitutionRepo(person.getInstitutionUUID).getInstitutionRegistrationPrefixes.getInstitutionRegistrationPrefixes
        .asScala.filter(irp => irp.getUUID.equals(person.getInstitutionRegistrationPrefixUUID)).head)
    }

    Option(user)
  }

  @GET
  @Path("get/{personUUID}")
  @Produces(Array(UserInfoTO.TYPE))
  def getByPersonUUID(implicit @Context sc: SecurityContext,
    @PathParam("personUUID") personUUID: String): Option[UserInfoTO] =
    authRepo.withPerson { p =>
      val user = newUserInfoTO
      val person = PersonRepo(personUUID).first.get
      if (person != null) {
        user.setPerson(person)
        user.setUsername(PersonRepo(person.getUUID).getUsername)
        if (RegistrationType.username.equals(person.getRegistrationType)) {
          user.setInstitutionRegistrationPrefix(InstitutionRepo(person.getInstitutionUUID).getInstitutionRegistrationPrefixes.getInstitutionRegistrationPrefixes
            .asScala.filter(irp => irp.getUUID.equals(person.getInstitutionRegistrationPrefixUUID)).head)
        }
        Option(user)
      } else {
        throw new EntityNotFoundException("personNotFound")
      }
    }.requiring(PersonRepo(getAuthenticatedPersonUUID).hasPowerOver(personUUID), AccessDeniedErr()).get

  @GET
  @Path("check/{institutionUUID}/{username}")
  @Produces(Array(UserInfoTO.TYPE))
  def checkUsernameAndEmail(@PathParam("username") username: String,
    @PathParam("institutionUUID") institutionUUID: String): Option[UserInfoTO] = {
    val user = newUserInfoTO
    //verify if there's a password set for this email
    if (authRepo.hasPassword(institutionUUID, username))
      user.setUsername(username)
    Option(user)
  }

  @GET
  @Path("requestPasswordChange/{email}/{institutionName}")
  @Produces(Array("text/plain"))
  def requestPasswordChange(@PathParam("email") email: String,
    @PathParam("institutionName") institutionName: String) = {
    val institution = InstitutionsRepo.getByName(institutionName)
    val person = PeopleRepo.getByEmail(institution.get.getUUID, email)
    if (person.isDefined && institution.isDefined) {
      val requestPasswordChangeUUID = UUID.random
      
      AuthRepo().getUsernameByPersonUUID(person.get.getUUID) match {
        case Some(one) => authRepo.updateRequestPasswordChangeUUID(person.get.getUUID, requestPasswordChangeUUID)
        case None => authRepo.setPlainPassword(institution.get.getUUID, person.get.getUUID, person.get.getEmail, null, false, requestPasswordChangeUUID)
    	}
      
      EmailService.sendEmailRequestPasswordChange(person.get, institution.get, requestPasswordChangeUUID)
    } else {
      throw new EntityNotFoundException("personOrInstitutionNotFound")
    }
  }

  @PUT
  @Path("resetPassword/{passwordChangeUUID}")
  @Produces(Array(UserInfoTO.TYPE))
  def resetPassword(@PathParam("passwordChangeUUID") passwordChangeUUID: String, password: String) = {
    val person = authRepo.getPersonByPasswordChangeUUID(passwordChangeUUID)
    if (person.isDefined) {
      PersonRepo(person.get.getUUID).setPassword(password, false)

      //log entity change 
      EventsRepo.logEntityChange(person.get.getInstitutionUUID, AuditedEntityType.password, person.get.getUUID, null, null, person.get.getUUID)

      val user = newUserInfoTO
      user.setUsername(person.get.getEmail())
      Option(user)
    } else {
      throw new UnauthorizedAccessException("passwordChangeFailed")
    }
  }

  @PUT
  @Path("changePassword/{targetPersonUUID}/")
  @Produces(Array("text/plain"))
  def changePassword(implicit @Context sc: SecurityContext,
    @PathParam("targetPersonUUID") targetPersonUUID: String, password: String) = {
    authRepo.withPerson { p =>
      if (!PersonRepo(p.getUUID).hasPowerOver(targetPersonUUID))
        throw new UnauthorizedAccessException("passwordChangeDenied")
      else {
        val targetPersonRepo = PersonRepo(targetPersonUUID)
        targetPersonRepo.setPassword(password, true)

        //log entity change
        EventsRepo.logEntityChange(targetPersonRepo.get.getInstitutionUUID, AuditedEntityType.password, targetPersonUUID, null, null)

      }
    }
  }

  //Used when user has the forcePasswordUpdate flag on his account
  @PUT
  @Path("updatePassword/{username}")
  @Produces(Array(UserInfoTO.TYPE))
  def updatePassword(@PathParam("username") username: String, password: String) = {
    val person = authRepo.getPersonByUsernameAndPasswordUpdateFlag(username)
    if (person.isDefined) {
      PersonRepo(person.get.getUUID).updatePassword(person.get.getUUID, password, true)

      //log entity change
      EventsRepo.logEntityChange(person.get.getInstitutionUUID, AuditedEntityType.password, person.get.getUUID, null, null, person.get.getUUID)

      val user = newUserInfoTO
      user.setUsername(person.get.getEmail())
      Option(user)
    } else {
      throw new UnauthorizedAccessException("passwordChangeFailed")
    }
  }

  @GET
  @Path("hasPowerOver/{targetPersonUUID}")
  @Produces(Array("application/boolean"))
  def changePassword(implicit @Context sc: SecurityContext,
    @PathParam("targetPersonUUID") targetPersonUUID: String) = {
    authRepo.withPerson { p =>
      PersonRepo(p.getUUID).hasPowerOver(targetPersonUUID)
    }
  }

  @PUT
  @Path("registrationRequest")
  @Consumes(Array(RegistrationRequestTO.TYPE))
  @Produces(Array(UserInfoTO.TYPE))
  def createUser(regReq: RegistrationRequestTO) = RegistrationEnrollmentService.userRequestRegistration(regReq)

  @PUT
  @Path("{personUUID}")
  @Consumes(Array(UserInfoTO.TYPE))
  @Produces(Array(UserInfoTO.TYPE))
  def update(implicit @Context sc: SecurityContext,
    userInfo: UserInfoTO,
    @PathParam("personUUID") personUUID: String) = authRepo.withPerson { p =>
    if (userInfo != null) {
      if (!PersonRepo(p.getUUID).hasPowerOver(personUUID))
        throw new UnauthorizedAccessException("passwordChangeDenied")
      else {
        val from = PersonRepo(personUUID).first.get

        PersonRepo(personUUID).update(userInfo.getPerson)

        //log entity change
        EventsRepo.logEntityChange(p.getInstitutionUUID, AuditedEntityType.person, personUUID, from, userInfo.getPerson)
        val roleTOs = RolesRepo.getUserRoles(personUUID, RoleCategory.BIND_DEFAULT)
        userInfo.setRoles(roleTOs.getRoleTOs)
        userInfo
      }
    }
  }

  @PUT
  @Path("acceptTerms")
  @Consumes(Array("text/plain"))
  @Produces(Array(UserInfoTO.TYPE))
  def acceptTerms() = AuthRepo().withPerson { p =>
    val from = PersonRepo(p.getUUID).first.get

    PersonRepo(p.getUUID).acceptTerms(from)

    val to = PersonRepo(p.getUUID).first.get

    //log entity change
    EventsRepo.logEntityChange(p.getInstitutionUUID, AuditedEntityType.person, p.getUUID, from, to)

    getUser(p)
  }

  def createUser(institutionUUID: String, fullName: String, email: String, cpf: String, username: String, password: String): String = {
    val regreq = TOs.newRegistrationRequestTO(institutionUUID, fullName, email, password, cpf, username, RegistrationType.email)
    createUser(regreq).getPerson.getUUID
  }
}

object UserResource {
  def apply(authRepo: AuthRepo): UserResource = new UserResource(AuthRepo())
  def apply(): UserResource = apply(AuthRepo())
}
