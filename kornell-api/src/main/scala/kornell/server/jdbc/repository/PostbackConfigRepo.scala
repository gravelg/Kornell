package kornell.server.jdbc.repository

import kornell.core.entity.{PostbackConfig, PostbackType}
import kornell.server.jdbc.SQL._

object PostbackConfigRepo {

  def getConfig(institutionUUID: String, postbackType: PostbackType): Option[PostbackConfig] = {
    sql"""select * from PostbackConfig where institutionUUID = ${institutionUUID} and postbackType = ${postbackType.toString}""".first[PostbackConfig]
  }

  def checkConfig(institutionUUID: String, postbackType: PostbackType, config: String): Option[PostbackConfig] = {
    sql"""select * from PostbackConfig where institutionUUID = ${institutionUUID}
      and postbackType = ${postbackType.toString} and contents = ${config}""".first[PostbackConfig]
  }
}
