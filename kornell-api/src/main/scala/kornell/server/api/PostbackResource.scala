package kornell.server.api

import javax.ws.rs.Path
import javax.ws.rs.PathParam
import javax.ws.rs.POST
import kornell.server.service.PostbackService
import javax.ws.rs.FormParam
import kornell.server.util.Conditional.toConditional
import kornell.server.util.AccessDeniedErr


@Path("/postback")
class PostbackResource {
  
  @Path("/paypal/{env}")
  @POST
  def paypal(@PathParam("env") env: String, payload: String) = {
    //This call needs to return a 200 with empty response immediately
    //Service launches thread for rest of processing
    PostbackService.paypalPostback(env, payload)
    ""
  }
  
  @Path("/pagseguro/{institutionUUID}/{env}")
  @POST
  def pagseguro(@PathParam("env") env: String, @PathParam("institutionUUID") institutionUUID: String,
      @FormParam("notificationCode") notificationCode: String, @FormParam("notificationType") notificationType: String) = {
    //This call needs to return a 200 with empty response immediately
    //Service launches thread for rest of processing
    PostbackService.pagseguroPostback(env, institutionUUID, notificationCode)
    ""
  }
  
  @Path("/reprocess/pagseguro/{institutionUUID}")
  @POST
  def reprocessPagseguro(@PathParam("institutionUUID") institutionUUID: String, payload: String) = {
    PostbackService.processPagseguroResponse(institutionUUID, payload)
    ""
  }.requiring(isPlatformAdmin(institutionUUID), AccessDeniedErr()).get
}