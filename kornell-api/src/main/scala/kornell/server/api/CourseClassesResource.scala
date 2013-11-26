package kornell.server.api
import javax.ws.rs.Path
import javax.ws.rs.GET
import javax.ws.rs.PathParam
import javax.ws.rs.Produces
import javax.ws.rs.core.SecurityContext
import kornell.server.repository.jdbc.Auth
import javax.ws.rs.core.Context
import kornell.core.lom.Contents
import kornell.core.to.CoursesTO

@Path("courseClasses")
class CourseClassesResource {
  
  @Path("{uuid}")
  def getCourseClassResource(@PathParam("uuid") uuid:String) = CourseClassResource(uuid)
  
  @GET
  @Produces(Array(CoursesTO.TYPE))
  def getClasses(implicit @Context sc: SecurityContext, @PathParam("institutionUUID") institutionUUID:String):Contents = 
  Auth.withPerson { person =>
  	???
  }
}