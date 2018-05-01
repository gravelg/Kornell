package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.TrackItem

class TrackItemRepo(uuid: String) {

  val finder = sql"select * from TrackItem where uuid=$uuid"

  def get: TrackItem = finder.get[TrackItem]
  def first: Option[TrackItem] = finder.first[TrackItem]

  def update(trackItem: TrackItem): TrackItem = {
    sql"""
      update TrackItem set
      | `order` = ${trackItem.getOrder},
      | havingPreRequirements = ${trackItem.isHavingPreRequirements},
      | startDate = ${trackItem.getStartDate}
      | where uuid = ${uuid}
    """.executeUpdate

    trackItem
  }

  def delete: TrackItem = {
    val trackItem = get

    sql"""
      delete from TrackItem where uuid = ${uuid}
    """.executeUpdate

    trackItem
  }
}

object TrackItemRepo {
  def apply(uuid: String) = new TrackItemRepo(uuid)
}
