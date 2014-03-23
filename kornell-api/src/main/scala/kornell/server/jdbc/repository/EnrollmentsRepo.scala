package kornell.server.jdbc.repository

import kornell.core.entity.Enrollment
import kornell.core.entity.EnrollmentState
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.SQL.randomUUID
import kornell.server.repository.Entities.newEnrollments

object EnrollmentsRepo {

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

  def create(enrollment: Enrollment) = {
    if(enrollment.getUUID == null)
    	enrollment.setUUID(randomUUID)
    sql""" 
    	insert into Enrollment(uuid,class_uuid,person_uuid,enrolledOn,state)
    	values(
    		${enrollment.getUUID},
    		${enrollment.getCourseClassUUID},
    		${enrollment.getPerson.getUUID},
    		now(),
    		${enrollment.getState.toString}
    	)""".executeUpdate
    enrollment
  }

  def find(person: PersonRepo, course_uuid: String): Enrollment = sql"""
	  select * from Enrollment 
	  were person_uuid=${person.uuid}
	   and course_uuid=${course_uuid}"""
    .first[Enrollment]
    .get
}
