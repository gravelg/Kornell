package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.Track
import kornell.core.error.exception.EntityConflictException

class TrackRepo(uuid: String) {

  val finder = sql"select * from Track where uuid=$uuid"

  def get: Track = finder.get[Track]
  def first: Option[Track] = finder.first[Track]

  def update(track: Track): Track = {
    sql"""update Track set name = ${track.getName} where uuid = ${uuid}""".executeUpdate
    track
  }

  def delete: Track = {
    val track = get

    val trackItemCount = sql"""select count(*) as trackItems from TrackItem where trackUUID = ${track.getUUID}""".first[Integer] { rs => rs.getInt("trackItems") }.get
    val trackEnrollmentCount = sql"""select count(*) as trackEnrollments from TrackEnrollment where trackUUID = ${track.getUUID}""".first[Integer] { rs => rs.getInt("trackEnrollments") }.get

    if (trackItemCount == 0 && trackEnrollmentCount == 0) {
      sql"""
        delete from Track where uuid = ${track.getUUID}
      """.executeUpdate
    } else {
      throw new EntityConflictException("constraintViolatedTrackEntities")
    }

    track
  }
}

object TrackRepo {
  def apply(uuid: String) = new TrackRepo(uuid)
}
