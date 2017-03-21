package kornell.server.web

import javax.servlet._
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import java.util.logging.Logger
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.util.DateConverter
import kornell.core.entity.Institution
import java.net.URL
import java.net.URI
import org.apache.http.client.utils.URLEncodedUtils
import java.nio.charset.Charset

class InstitutionHostnameFilter extends Filter {
  
  val logger = Logger.getLogger("kornell.server.web")
  val DOMAIN_HEADER = "X-KNL-DOMAIN"
  
  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) = 
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) => {
        if (hreq.getRequestURI.startsWith("/api")) {
        	doFilter(hreq, hres, chain)
        } else {
          chain.doFilter(hreq, hres)
        }
      }
    }
  
  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = {
    val institution = getInstitution(req)
    
    if (institution.isDefined) {
      DateConverter.setTimeZone(institution.get.getTimeZone)
      chain.doFilter(req, resp)
      clearTimeZone
    } else {
      logAndContinue(req, resp, chain)
    }
  }
  
  def logAndContinue(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = {
    logger.warning("Request did not contain a valid 'X-KNL-DOMAIN' header, could not initialize DateConverter for URL " + req.getRequestURL)
    chain.doFilter(req, resp)
  }
  
  def getInstitution(req: HttpServletRequest): Option[Institution] = {
    if (req.getHeader(DOMAIN_HEADER) != null) {
      InstitutionsRepo.getByHostName(req.getHeader(DOMAIN_HEADER))
    } else if (req.getHeader("Referer") != null) {
      getInstitutionFromHeader(req.getHeader("Referer"))
    } else {
      None
    }
  }
  
  def getInstitutionFromHeader(header: String): Option[Institution] = {
    val pattern = """institution=([a-z]+)$""".r
    val institutionName = pattern findFirstIn header match {
      case Some(pattern(c)) => Option(c)
      case None => None
    }
    if (institutionName.isDefined) {
      InstitutionsRepo.getByName(institutionName.get)
    } else {
      None
    }
  }
  
//  def getInstitution(req: HttpServletRequest): Option[Institution] = {
//    if (req.getHeader(DOMAIN_HEADER) != null) {
//      InstitutionsRepo.getByHostName(req.getHeader(DOMAIN_HEADER))
//    } else if (req.getHeader("Referer") != null && req.getParameter("institution") != null) {
//      InstitutionsRepo.getByName(req.getParameter("institution"))
//    } else {
//      None
//    }
//  }
  
  override def init(cfg: FilterConfig) {}

  override def destroy() {}
  
  def clearTimeZone = DateConverter.clearTimeZone
}