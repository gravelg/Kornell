package kornell.server.web

import java.sql.SQLException
import java.util.logging.Logger

import javax.servlet._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import kornell.server.jdbc.ConnectionHandler

class TransactionManagementFilter extends Filter {

  val logger: Logger = Logger.getLogger("kornell.server.web")
  val DOMAIN_HEADER = "X-KNL-DOMAIN"

  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain): Unit =
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) => {
        doFilter(hreq, hres, chain)
      }
    }

  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain): Unit = {
    try {
      chain.doFilter(req, resp)
      ConnectionHandler.commit()
    } catch {
      case e: Throwable => {
        // Gotta do this cause wildfly wraps uncaught exceptions
        e.getCause match {
          case _ => {
            ConnectionHandler.rollback()
            e.printStackTrace()
          }
        }
        throw e
      }
    }
  }

  override def init(cfg: FilterConfig): Unit = {}

  override def destroy(): Unit = {}
}