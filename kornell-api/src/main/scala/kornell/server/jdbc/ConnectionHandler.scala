package kornell.server.jdbc

import java.sql.Connection

object ConnectionHandler {
  val threadLocal = new ThreadLocal[Connection]

  def getConnection() = {
    if (threadLocal.get == null) {
      threadLocal.set(DataSources.connectionFactory())
    }
    threadLocal.get
  }

  def rollback() = {
    val trans = threadLocal.get
    if (trans != null) {
      trans.rollback
      trans.close
      threadLocal.remove
    }
  }

  def commit() = {
    val trans = threadLocal.get
    if (trans != null) {
      trans.commit
      trans.close
      threadLocal.remove
    }
  }
}
