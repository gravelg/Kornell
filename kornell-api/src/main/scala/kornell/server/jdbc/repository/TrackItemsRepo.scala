package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.TrackItem
import kornell.core.util.UUID

class TrackItemsRepo {

  def create(trackItem: TrackItem): TrackItem = {
    if (trackItem.getUUID == null) {
      trackItem.setUUID(UUID.random)
    }
    sql"""
      insert into TrackItem (uuid, courseVersionUUID, parentUUID, order hasPreRequirements, startDate) values
      | (${trackItem.getUUID},
      | ${trackItem.getCourseVersionUUID},
      | ${trackItem.getParentUUID},
      | ${trackItem.getOrder},
      | ${trackItem.isHavingPreRequirements()},
      | ${trackItem.getStartDate})
    """.executeUpdate

    trackItem
  }
}
