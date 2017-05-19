package kornell.server.api

import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import kornell.server.jdbc.SQL._
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.EventsRepo
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.core.event.ActomEntered
import kornell.core.event.AttendanceSheetSigned
import kornell.core.event.CourseClassStateChanged
import kornell.core.event.EnrollmentStateChanged
import kornell.core.event.EnrollmentTransferred
import kornell.core.event.EntityChanged
import kornell.core.entity.AuditedEntityType
import kornell.core.to.EntityChangedEventsTO
import kornell.server.util.Conditional.toConditional
import kornell.server.util.AccessDeniedErr
import kornell.server.jdbc.repository.EnrollmentRepo
import javax.ws.rs.core.Response

@Path("events")
class EventsResource {
  
  @PUT 
  @Path("actomEntered")
  @Consumes(Array(ActomEntered.TYPE))
  def putActomEntered(event:ActomEntered):Response = {
	 EventsRepo.logActomEntered(event)
	 Response.noContent().build()
  }.requiring(EnrollmentRepo(event.getEnrollmentUUID).get.getPersonUUID == getAuthenticatedPersonUUID, AccessDeniedErr()).get
  
  //TODO permissions
  @PUT
  @Path("enrollmentStateChanged")
  @Consumes(Array(EnrollmentStateChanged.TYPE))
  def putEnrollmentStateChanged(event:EnrollmentStateChanged):Response = {
     EventsRepo.logEnrollmentStateChanged(event)
     Response.noContent().build()
  }.requiring(event.getFromPersonUUID == getAuthenticatedPersonUUID, AccessDeniedErr()).get
  
  @PUT
  @Path("courseClassStateChanged")
  @Consumes(Array(CourseClassStateChanged.TYPE))
  def putCourseClassStateChanged(event:CourseClassStateChanged):Response = {
     EventsRepo.logCourseClassStateChanged(event)
     Response.noContent().build()
  }.requiring(event.getFromPersonUUID == getAuthenticatedPersonUUID, AccessDeniedErr()).get
  
  @PUT
  @Path("attendanceSheetSigned")
  @Consumes(Array(AttendanceSheetSigned.TYPE))
  def putAttendanceSheetSigned(event:AttendanceSheetSigned):Response = {
     EventsRepo.logAttendanceSheetSigned(event)
     Response.noContent().build()
  }.requiring(event.getPersonUUID == getAuthenticatedPersonUUID, AccessDeniedErr()).get
  
  @PUT
  @Path("enrollmentTransferred")
  @Consumes(Array(EnrollmentTransferred.TYPE))
  def putEnrollmentTransferred(event:EnrollmentTransferred):Response = {
     EventsRepo.logEnrollmentTransferred(event)
     Response.noContent().build()
  }.requiring(event.getFromPersonUUID == getAuthenticatedPersonUUID, AccessDeniedErr()).get
  
  @GET
  @Produces(Array(EntityChangedEventsTO.TYPE))
  @Path("entityChanged")
  def getEntityChangedEvents(@QueryParam("entityType") entityType: String, @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int) = 
  AuthRepo().withPerson { person =>
     EventsRepo.getEntityChangedEvents(person.getInstitutionUUID, AuditedEntityType.valueOf(entityType), pageSize, pageNumber)
  }
	
}