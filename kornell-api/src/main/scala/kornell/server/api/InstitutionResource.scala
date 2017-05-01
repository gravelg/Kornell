package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.POST
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import kornell.core.entity.Institution
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.util.Conditional.toConditional
import kornell.core.to.InstitutionRegistrationPrefixesTO
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.core.entity.Roles
import kornell.server.jdbc.repository.RolesRepo
import kornell.core.to.RolesTO
import javax.ws.rs.QueryParam
import kornell.server.jdbc.repository.ChatThreadsRepo
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.InstitutionHostNameRepo
import kornell.server.jdbc.repository.InstitutionHostNameRepo
import kornell.core.to.InstitutionHostNamesTO
import kornell.core.to.InstitutionEmailWhitelistTO
import kornell.server.jdbc.repository.InstitutionEmailWhitelistRepo
import kornell.core.entity.ChatThreadType
import kornell.server.service.UploadService
import javax.ws.rs.PathParam


class InstitutionResource(uuid: String) {
  
  @GET
  @Produces(Array(Institution.TYPE))
  def get =  {
    InstitutionRepo(uuid).get
   }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
   .or(isInstitutionAdmin(uuid), AccessDeniedErr())
   .or(isControlPanelAdmin, AccessDeniedErr()).get
  
  @PUT
  @Consumes(Array(Institution.TYPE))
  @Produces(Array(Institution.TYPE))
  def update(institution: Institution) = {
    InstitutionRepo(uuid).update(institution)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
  .or(isInstitutionAdmin(uuid), AccessDeniedErr())
  .or(isControlPanelAdmin, AccessDeniedErr()).get
  
  @GET
  @Produces(Array(InstitutionRegistrationPrefixesTO.TYPE))
  @Path("registrationPrefixes")
  def getRegistrationPrefixes() = {
    InstitutionRepo(uuid).getInstitutionRegistrationPrefixes
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
  .or(isInstitutionAdmin(uuid), AccessDeniedErr()).get
  
  @PUT
  @Consumes(Array(Roles.TYPE))
  @Produces(Array(Roles.TYPE))
  @Path("admins")
  def updateAdmins(roles: Roles) = {
        val r = RolesRepo.updateInstitutionAdmins(uuid, roles)
        ChatThreadsRepo.updateParticipantsInCourseClassSupportThreadsForInstitution(uuid, ChatThreadType.SUPPORT)
        ChatThreadsRepo.updateParticipantsInCourseClassSupportThreadsForInstitution(uuid, ChatThreadType.INSTITUTION_SUPPORT)
        ChatThreadsRepo.updateParticipantsInCourseClassSupportThreadsForInstitution(uuid, ChatThreadType.PLATFORM_SUPPORT)
        r
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
   .or(isInstitutionAdmin(uuid), AccessDeniedErr())
   .get

  @GET
  @Produces(Array(RolesTO.TYPE))
  @Path("admins")
  def getAdmins(@QueryParam("bind") bindMode:String) = {
        RolesRepo.getInstitutionAdmins(uuid, bindMode)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
   .or(isInstitutionAdmin(uuid), AccessDeniedErr())
   .get
   
  @PUT
  @Consumes(Array(InstitutionHostNamesTO.TYPE))
  @Produces(Array(InstitutionHostNamesTO.TYPE))
  @Path("hostnames")
  def updateHostnames(hostnames: InstitutionHostNamesTO) = {
      InstitutionHostNameRepo(uuid).updateHostnames(hostnames)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
   .or(isInstitutionAdmin(uuid), AccessDeniedErr())
   .get

  @GET
  @Produces(Array(InstitutionHostNamesTO.TYPE))
  @Path("hostnames")
  def getHostnames() = {
        InstitutionHostNameRepo(uuid).get
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
   .or(isInstitutionAdmin(uuid), AccessDeniedErr())
   .get
   
   @PUT
  @Consumes(Array(InstitutionEmailWhitelistTO.TYPE))
  @Produces(Array(InstitutionEmailWhitelistTO.TYPE))
  @Path("emailWhitelist")
  def updateEmailWhitelist(domains: InstitutionEmailWhitelistTO) = {
      InstitutionEmailWhitelistRepo(uuid).updateDomains(domains)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
   .or(isInstitutionAdmin(uuid), AccessDeniedErr())
   .get

  @GET
  @Produces(Array(InstitutionEmailWhitelistTO.TYPE))
  @Path("emailWhitelist")
  def getEmailWhitelist() = {
        InstitutionEmailWhitelistRepo(uuid).get
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
   .or(isInstitutionAdmin(uuid), AccessDeniedErr())
   .get
   
   @GET
   @Path("uploadUrl/{filename}")
   @Produces(Array("application/octet-stream"))
   def getUploadUrl(@PathParam("filename") filename: String) : String = {
    UploadService.getInstitutionUploadUrl(uuid, filename)
  }.requiring(isPlatformAdmin(uuid), AccessDeniedErr())
   .or(isInstitutionAdmin(uuid), AccessDeniedErr())
   .get
   
}

object InstitutionResource {
    def apply(uuid: String) = new InstitutionResource(uuid)
}