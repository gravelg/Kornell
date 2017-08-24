package kornell.server.web

import javax.servlet.Filter
import javax.servlet.ServletRequest
import javax.servlet.ServletResponse
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import kornell.server.util.UserLocale
import javax.servlet.FilterConfig

class UserLocaleFilter extends Filter {
  
  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain) = 
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) => {
        if (hreq.getRequestURI.startsWith("/api") && !hreq.getRequestURI.equals("/api")) {
        	doFilter(hreq, hres, chain)
        } else {
          chain.doFilter(hreq, hres)
        }
      }
    }
  
  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain) = {
//    debugLogRequest(req)
    
      UserLocale.setLocale("")
      chain.doFilter(req, resp)
      UserLocale.clearLocale
  }
  
  override def init(cfg: FilterConfig) {}

  override def destroy() {}
}