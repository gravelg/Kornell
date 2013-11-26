package kornell.server.repository.jdbc

import javax.ws.rs.core.Context
import javax.ws.rs.core.SecurityContext
import kornell.core.to.CourseTO
import kornell.server.repository.jdbc.SQLInterpolation._

object Courses {
  @Deprecated //use withEnrollment
  def byUUID(uuid: String)(implicit @Context sc: SecurityContext): Option[CourseTO] =
    Auth.withPerson { p => apply(uuid).withEnrollment(p) }

  def apply(uuid:String) = CourseRepository(uuid)
  /* {
    sql"""
    | select * from Course where institution_uuid = $institutionUUID
    """.map[CourseTO](newCourseTO)   
  } 
  */
  
}