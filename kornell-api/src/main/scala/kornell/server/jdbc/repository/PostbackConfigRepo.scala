package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.core.entity.PostbackType
import kornell.core.entity.PostbackConfig

object PostbackConfigRepo {
  
  def getConfig(institutionUUID: String, postbackType: PostbackType) = {
    sql"""select * from PostbackConfig where institutionUUID = ${institutionUUID} and postbackType = ${postbackType.toString}""".first[PostbackConfig]
  }
  
  def checkConfig(institutionUUID: String, postbackType: PostbackType, config: String) = {
    sql"""select * from PostbackConfig where institutionUUID = ${institutionUUID} 
      and postbackType = ${postbackType.toString} and contents = ${config}""".first[PostbackConfig]
  }
}