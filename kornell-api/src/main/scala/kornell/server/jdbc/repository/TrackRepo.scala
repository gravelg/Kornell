package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.Track

class TrackRepo(uuid: String) {

  val finder = sql"select * from Track where uuid=$uuid"

  def get = finder.get[Track]
  def first = finder.first[Track]

  def update(track: Track): Track = {
    sql"""update Track set name = ${track.getName} where uuid = ${track.getUUID}""".executeUpdate
    track
  }
}

object TrackRepo {
  def apply(uuid: String) = new TrackRepo(uuid)
}
