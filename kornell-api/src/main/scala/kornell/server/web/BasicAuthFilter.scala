
package kornell.server.web

import java.util.Date
import java.util.logging.Logger

import javax.servlet._
import javax.servlet.http._
import kornell.core.entity.AuthClientType
import kornell.core.error.KornellErrorTO
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.server.jdbc.repository.TokenRepo

class BasicAuthFilter extends Filter {
  val log: Logger = Logger.getLogger(classOf[BasicAuthFilter].getName)
  val tokenRepo = TokenRepo()
  val X_KNL_TOKEN = "X-KNL-TOKEN"
  val pubPaths = Set(
    "/newrelic",
    "/api",
    "/user/login",
    "/user/check",
    "/user/registrationRequest",
    "/user/requestPasswordChange",
    "/user/changePassword",
    "/user/resetPassword",
    "/user/updatePassword",
    "/enrollments/leaderboard",
    "/institutions",
    "/healthCheck",
    "/auth",
    "/postback")

  override def doFilter(sreq: ServletRequest, sres: ServletResponse, chain: FilterChain): Unit =
    (sreq, sres) match {
      case (hreq: HttpServletRequest, hres: HttpServletResponse) => {
        if (hreq.getRequestURI.startsWith("/api")) {
          doFilter(hreq, hres, chain)
        } else {
          chain.doFilter(hreq, hres)
        }
      }
    }

  def getCredentials(req: HttpServletRequest): String = {
    if (req.getHeader(X_KNL_TOKEN) != null)
      req.getHeader(X_KNL_TOKEN)
    else if (req.getCookies != null && req.getCookies.exists(c => X_KNL_TOKEN.equals(c.getName)))
      req.getCookies.filter(c => X_KNL_TOKEN.equals(c.getName)).head.getValue
    else
      null
  }

  def hasCredentials(req: HttpServletRequest): Boolean = getCredentials(req) != null

  def isPrivate(req: HttpServletRequest, resp: HttpServletResponse): Boolean = !isPublic(req, resp)

  def doFilter(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain): Unit =
    if (hasCredentials(req) || isPrivate(req, resp))
      checkCredentials(req, resp, chain)
    else {
      chain.doFilter(req, resp)
    }

  def isPublic(req: HttpServletRequest, resp: HttpServletResponse): Boolean = {
    val path = req.getRequestURI
    val isPublic = path == "/api" || path == "/api/" || pubPaths.exists { p => path.startsWith(s"/api${p}") }
    val isOption = "OPTIONS".equals(req.getMethod)
    isOption || isPublic
  }

  def checkCredentials(req: HttpServletRequest, resp: HttpServletResponse, chain: FilterChain): Unit = {
    val auth = getCredentials(req)

    if (auth != null && auth.length() > 0) {
      val token = tokenRepo.checkToken(auth)
      if (token.isEmpty || (token.get.getClientType == AuthClientType.web && token.get.getExpiry.before(new Date))) {
        writeErrorMessage("mustAuthenticate", req, resp)
      } else {
        ThreadLocalAuthenticator.setAuthenticatedPersonUUID(token.get.getPersonUUID)
        chain.doFilter(req, resp)
      }
      logout()
    } else {
      writeErrorMessage("mustAuthenticate", req, resp)
    }
  }

  def writeErrorMessage(messageKey: String, req: HttpServletRequest, resp: HttpServletResponse): Unit = {
    resp.setContentType(KornellErrorTO.TYPE)
    resp.setStatus(401)
    resp.setCharacterEncoding("UTF-8")
    resp.getWriter.write("{\"messageKey\":\"" + messageKey + "\"}")
  }

  override def init(cfg: FilterConfig): Unit = {}

  override def destroy(): Unit = {}

  def logout(): Unit = ThreadLocalAuthenticator.clearAuthenticatedPersonUUID()
}

