package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.TrackItem

class TrackItemRepo(uuid: String) {

  val finder = sql"select * from TrackItem where uuid=$uuid"

  def get = finder.get[TrackItem]
  def first = finder.first[TrackItem]

  def update(trackItem: TrackItem): TrackItem = {
    sql"""
      update TrackItem set
      | order = ${trackItem.getOrder},
      | hasPreRequirements = ${trackItem.isHavingPreRequirements},
      | startDate = ${trackItem.getStartDate}
      | where uuid = ${trackItem.getUUID}
    """.executeUpdate

    trackItem
  }
}

object TrackItemRepo {
  def apply(uuid: String) = new TrackItemRepo(uuid)
}
