package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.TrackEnrollment

class TrackEnrollmentRepo(uuid: String) {

  val finder = sql"select * from TrackEnrollment where uuid=$uuid"

  def get = finder.get[TrackEnrollment]
  def first = finder.first[TrackEnrollment]

  def delete = {
    val enrollment = get
    sql"""delete from TrackEnrollment where uuid=${uuid}""".executeUpdate

    enrollment
  }
}

object TrackEnrollmentRepo {
  def apply(uuid: String) = new TrackEnrollmentRepo(uuid)
}
