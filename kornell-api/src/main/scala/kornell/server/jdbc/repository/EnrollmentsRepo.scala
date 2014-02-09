package kornell.server.jdbc.repository

import kornell.server.repository.TOs
import kornell.core.to.RegistrationsTO
import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities
import java.sql.ResultSet
import kornell.core.entity.Registration
import kornell.core.entity.Institution
import scala.collection.JavaConverters._
import kornell.core.entity.Person
import kornell.server.repository.Entities._
import kornell.server.repository.TOs._
import kornell.core.entity.Enrollment
import scala.collection.mutable.ListBuffer
import kornell.core.to.CourseTO
import kornell.core.entity.EnrollmentState
import scala.None
import java.util.Date
import kornell.server.util.EmailSender
import kornell.core.entity.Course
import kornell.core.entity.CourseClass
import kornell.core.util.UUID
import kornell.server.repository.service.RegistrationEnrollmentService

object EnrollmentsRepo {

  def byUUID(enrollmentUUID: String): Option[Enrollment] =
    sql"""
	    | select e.uuid, e.enrolledOn, e.class_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e where uuid = ${enrollmentUUID} 
	    """.first[Enrollment]

  def byCourseClass(courseClassUUID: String) = newEnrollments(
    sql"""
	    | select e.uuid, e.enrolledOn, e.class_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e join Person p on e.person_uuid = p.uuid
        | where e.class_uuid = ${courseClassUUID}
        | order by e.state desc, p.fullName, p.email
	    """.map[Enrollment](toEnrollment))

  def byPerson(personUUID: String) =
    sql"""
	    | select e.uuid, e.enrolledOn, e.class_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e join Person p on e.person_uuid = p.uuid where
        | e.person_uuid = ${personUUID}
	    """.map[Enrollment](toEnrollment)

  def byCourseClassAndPerson(courseClassUUID: String, personUUID: String): Option[Enrollment] =
    sql"""
	    | select e.uuid, e.enrolledOn, e.class_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e join Person p on e.person_uuid = p.uuid
        | where e.class_uuid = ${courseClassUUID} and
        | e.person_uuid = ${personUUID}
	    """.first[Enrollment]

  def byStateAndPerson(state: EnrollmentState, personUUID: String) =
    sql"""
	    | select e.uuid, e.enrolledOn, e.class_uuid, e.person_uuid, e.progress, e.notes, e.state
      	| from Enrollment e join Person p on e.person_uuid = p.uuid
        | where e.person_uuid = ${personUUID}
	    | and e.state = ${state.toString()}
        | order by e.state desc, p.fullName, p.email
	    """.map[Enrollment](toEnrollment)

  def createEnrollment(courseClassUUID: String, person_uuid: String, state: EnrollmentState) = {
    val uuid = randomUUID
    sql""" 
    	insert into Enrollment(uuid,class_uuid,person_uuid,enrolledOn,state)
    	values($randomUUID,$courseClassUUID,$person_uuid,now(),${state.toString()})
    """.executeUpdate
  }
  
  def update(enrollment: Enrollment): Enrollment = {    
    sql"""
    | update Enrollment e
    | set e.enrolledOn = ${enrollment.getEnrolledOn},
    | e.class_uuid = ${enrollment.getCourseClassUUID},
    | e.person_uuid = ${enrollment.getPerson.getUUID},
    | e.progress = ${enrollment.getProgress.intValue},
    | e.notes = ${enrollment.getNotes},
    | e.state = ${enrollment.getState.toString}
    | where e.uuid = ${enrollment.getUUID}""".executeUpdate
    enrollment
  }

}
