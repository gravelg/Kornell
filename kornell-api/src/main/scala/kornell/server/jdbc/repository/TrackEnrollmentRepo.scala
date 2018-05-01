package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.TrackEnrollment

class TrackEnrollmentRepo(uuid: String) {

  val finder = sql"select * from TrackEnrollment where uuid=$uuid"

  def get: TrackEnrollment = finder.get[TrackEnrollment]
  def first: Option[TrackEnrollment] = finder.first[TrackEnrollment]

  def delete: TrackEnrollment = {
    val enrollment = get
    sql"""delete from TrackEnrollment where uuid=${uuid}""".executeUpdate

    enrollment
  }
}

object TrackEnrollmentRepo {
  def apply(uuid: String) = new TrackEnrollmentRepo(uuid)
}
