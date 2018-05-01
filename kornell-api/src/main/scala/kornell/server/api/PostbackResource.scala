package kornell.server.api

import javax.servlet.http.HttpServletRequest
import javax.ws.rs.{POST, Path, PathParam}
import javax.ws.rs.core.Context
import kornell.server.service.PostbackService
import kornell.server.util.AccessDeniedErr
import kornell.server.util.Conditional.toConditional

@Path("/postback")
class PostbackResource {

  @Path("/paypal/{env}")
  @POST
  def paypal(@PathParam("env") env: String, payload: String): String = {
    //This call needs to return a 200 with empty response immediately
    //Service launches thread for rest of processing
    PostbackService.paypalPostback(env, payload)
    ""
  }

  @Path("/{platform}/{institutionUUID}/{env}")
  @POST
  def pagseguro(@PathParam("env") env: String, @PathParam("institutionUUID") institutionUUID: String,
    @PathParam("platform") platform: String, @Context request: HttpServletRequest): String = {
    //can't auto-decode url params because they are sending windows-1252
    request.setCharacterEncoding("windows-1252")
    if (platform == "pagseguro") {
      PostbackService.pagseguroPostback(env, institutionUUID, request)
    } else if (platform == "paypal") {
      PostbackService.paypalWCPostback(env, institutionUUID, request)
    }
    ""
  }

  @Path("/reprocess/pagseguro/{institutionUUID}")
  @POST
  def reprocessPagseguro(@PathParam("institutionUUID") institutionUUID: String, payload: String): String = {
    PostbackService.processPagseguroResponse(institutionUUID, payload)
    ""
  }.requiring(isPlatformAdmin(institutionUUID), AccessDeniedErr()).get
}
