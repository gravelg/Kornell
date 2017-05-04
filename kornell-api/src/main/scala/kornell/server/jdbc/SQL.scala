package kornell.server.jdbc

import java.sql.Connection
import kornell.core.util.UUID
import java.sql.ResultSet

object SQL {
  implicit def rsToString(rs: ResultSet): String = rs.getString(1)
  implicit def rsToBoolean(rs: ResultSet): Boolean = rs.getBoolean(1)

  type ConnectionFactory = () => Connection

  implicit class SQLHelper(val srtCtx: StringContext) extends AnyVal {
    def sql(args: Any*) = {
      val parts = srtCtx.parts.iterator
      val params = args.toList
      var query = new StringBuffer(parts.next)
      while (parts.hasNext) {
        query append "?"
        query append parts.next
      }
      new PreparedStmt(query.toString, params)
    }
  }

  def randomUUID = UUID.random()

}

