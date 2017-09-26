package kornell.server.web

import javax.servlet._
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.logging.Logger
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.core.entity.Institution
import java.net.URL
import java.net.URI
import org.apache.http.client.utils.URLEncodedUtils
import java.nio.charset.Charset
import kornell.server.jdbc.ConnectionHandler
import java.util.logging.Level
import java.sql.SQLException

class TransactionManagementFilter extends Filter {
  
  val logger = Logger.getLogger("kornell.server.web")
  val DOMAIN_HEADER = "X-KNL-DOMAIN"
  
  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) = 
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) => {
        	doFilter(hreq, hres, chain)
      }
    }
  
  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = {
    try {
      chain.doFilter(req, resp)
      ConnectionHandler.commit
    } catch {
      case e: Throwable => {
        // Gotta do this cause wildfly wraps uncaught exceptions
        e.getCause match {
          case sql: SQLException => ConnectionHandler.rollback
          case _ => {
            ConnectionHandler.rollback
            e.printStackTrace()
          }
        }
        throw e
      }
    }
  }

  override def init(cfg: FilterConfig) {}

  override def destroy() {}
}