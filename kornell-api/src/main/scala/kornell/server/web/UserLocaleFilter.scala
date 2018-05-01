package kornell.server.web

import javax.servlet._
import javax.servlet.http.{HttpServletRequest, HttpServletResponse}
import kornell.server.util.{Settings, UserLocale}

class UserLocaleFilter extends Filter {

  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain): Unit =
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) => {
        if (hreq.getRequestURI.startsWith("/api") && !hreq.getRequestURI.equals("/api")) {
          doFilter(hreq, hres, chain)
        } else {
          chain.doFilter(hreq, hres)
        }
      }
    }

  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain): Unit = {
    getLocale(req)
    chain.doFilter(req, resp)
    UserLocale.clearLocale()
  }

  def getLocale(req: HttpServletRequest): String = {
    if (req.getCookies != null) {
      val cookie = req.getCookies.find(p => p.getName == "knlLocale")
      if (cookie.isEmpty) {
        //No cookie, set default locale
        UserLocale.setLocale(Settings.DEFAULT_LOCALE)
      } else {
        UserLocale.setLocale(cookie.get.getValue)
      }
    } else {
      //No cookies at all, set default locale
      UserLocale.setLocale(Settings.DEFAULT_LOCALE)
    }
  }

  override def init(cfg: FilterConfig): Unit = {}

  override def destroy(): Unit = {}
}