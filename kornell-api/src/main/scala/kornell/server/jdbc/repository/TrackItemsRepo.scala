package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.TrackItem
import kornell.core.util.UUID

object TrackItemsRepo {

  def create(trackItem: TrackItem): TrackItem = {
    if (trackItem.getUUID == null) {
      trackItem.setUUID(UUID.random)
    }
    sql"""
      insert into TrackItem (uuid, courseVersionUUID, trackUUID, parentUUID, `order`, havingPreRequirements, startDate) values
      | (${trackItem.getUUID},
      | ${trackItem.getCourseVersionUUID},
      | ${trackItem.getTrackUUID},
      | ${trackItem.getParentUUID},
      | ${trackItem.getOrder},
      | ${trackItem.isHavingPreRequirements},
      | ${trackItem.getStartDate})
    """.executeUpdate

    trackItem
  }
}
