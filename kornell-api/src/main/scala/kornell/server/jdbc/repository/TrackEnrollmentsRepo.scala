package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.TrackEnrollment
import kornell.core.util.UUID

class TrackEnrollmentsRepo {

  def create(trackEnrollment: TrackEnrollment): TrackEnrollment = {
    if (trackEnrollment.getUUID == null) {
      trackEnrollment.setUUID(UUID.random)
    }
    sql"""
      insert into TrackEnrollment (uuid, personUUID, trackUUID) values
      | (${trackEnrollment.getUUID},
      | ${trackEnrollment.getPersonUUID},
      | ${trackEnrollment.getTrackUUID})
    """.executeUpdate

    trackEnrollment
  }

}
