package kornell.server.api

import scala.collection.JavaConverters.setAsJavaSetConverter
import javax.ws.rs.Consumes
import javax.ws.rs.GET
import javax.ws.rs.PUT
import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.QueryParam
import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.entity.CourseClass
import kornell.core.entity.role.RoleCategory
import kornell.server.jdbc.repository.AuthRepo
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.util.Conditional.toConditional
import kornell.core.to.CourseClassesTO
import kornell.server.repository.Entities
import javax.ws.rs.POST
import kornell.core.entity.RegistrationType
import kornell.server.util.AccessDeniedErr
import kornell.core.to.CourseClassTO
import kornell.server.jdbc.repository.CourseClassRepo

@Path("courseClasses")
class CourseClassesResource {

  @Path("{uuid}")
  def get(@PathParam("uuid") uuid: String) = CourseClassResource(uuid)

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  def getClasses(implicit @Context sc: SecurityContext) =
    AuthRepo().withPerson { person =>
      {
        CourseClassesRepo.byPersonAndInstitution(person.getUUID, person.getInstitutionUUID)
      }
    }

  @POST
  @Consumes(Array(CourseClass.TYPE))
  @Produces(Array(CourseClass.TYPE))
  def create(courseClass: CourseClass) = {
    CourseClassesRepo.create(courseClass)
  }.requiring(isPlatformAdmin(courseClass.getInstitutionUUID), AccessDeniedErr())
    .or(isInstitutionAdmin(courseClass.getInstitutionUUID), AccessDeniedErr())
    .get

  @GET
  @Path("enrollment/{enrollmentUUID}")
  @Produces(Array(CourseClassTO.TYPE))
  def getByEnrollment(implicit @Context sc: SecurityContext, @PathParam("enrollmentUUID") enrollmentUUID: String) =
    AuthRepo().withPerson { person =>
      {
        CourseClassesRepo.byEnrollment(enrollmentUUID, person.getUUID, person.getInstitutionUUID, true);
      }
    }

  @GET
  @Produces(Array(CourseClassesTO.TYPE))
  @Path("administrated")
  def getAdministratedClasses(implicit @Context sc: SecurityContext, @QueryParam("courseVersionUUID") courseVersionUUID: String, @QueryParam("searchTerm") searchTerm: String,
    @QueryParam("ps") pageSize: Int, @QueryParam("pn") pageNumber: Int, @QueryParam("orderBy") orderBy: String, @QueryParam("asc") asc: String) =
    AuthRepo().withPerson { person =>
      {
        CourseClassesRepo.getAllClassesByInstitutionPaged(person.getInstitutionUUID, searchTerm, pageSize, pageNumber, orderBy, asc == "true", person.getUUID, courseVersionUUID, null, showSandbox = false)
      }
    }
}

object CourseClassesResource {
  def apply() = new CourseClassesResource()
}
