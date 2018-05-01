package kornell.server.api

import javax.ws.rs._
import javax.ws.rs.core.{Context, SecurityContext}
import kornell.core.entity.CourseClass
import kornell.core.to.{CourseClassTO, CourseClassesTO}
import kornell.server.jdbc.repository.{AuthRepo, CourseClassesRepo}
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("courseClasses")
class CourseClassesResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = CourseClassResource(uuid)

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  def getClasses(implicit @Context sc: SecurityContext): CourseClassesTO =
    AuthRepo().withPerson { person =>
      {
        CourseClassesRepo.byPersonAndInstitution(person.getUUID, person.getInstitutionUUID)
      }
    }

  @POST
  @Consumes(Array(CourseClass.TYPE))
  @Produces(Array(CourseClass.TYPE))
  def create(courseClass: CourseClass): CourseClass = {
    CourseClassesRepo.create(courseClass)
  }.requiring(isPlatformAdmin(courseClass.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(courseClass.getInstitutionUUID), AccessDeniedErr())
    .get

  @GET
  @Path("enrollment/{enrollmentUUID}")
  @Produces(Array(CourseClassTO.TYPE))
  def getByEnrollment(implicit @Context sc: SecurityContext, @PathParam("enrollmentUUID") enrollmentUUID: String): CourseClassTO =
    AuthRepo().withPerson { person =>
      {
        CourseClassesRepo.byEnrollment(enrollmentUUID, person.getUUID, person.getInstitutionUUID, true)
      }
    }

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  @Path("administrated")
  def getAdministratedClasses(implicit @Context sc: SecurityContext, @QueryParam("courseVersionUUID") courseVersionUUID: String, @QueryParam("searchTerm") searchTerm: String,
    @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int, @QueryParam("orderBy") orderBy: String, @QueryParam("asc") asc: String): CourseClassesTO =
    AuthRepo().withPerson { person =>
      {
        CourseClassesRepo.getAllClassesByInstitutionPaged(person.getInstitutionUUID, searchTerm, pageSize, pageNumber, orderBy, asc == "true", person.getUUID, courseVersionUUID, null, showSandbox = false)
      }
    }
}

object CourseClassesResource {
  def apply() = new CourseClassesResource()
}
