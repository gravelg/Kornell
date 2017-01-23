package kornell.server.repository.service

import org.apache.http.impl.client.HttpClients
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.util.EntityUtils
import org.apache.http.client.utils.URLEncodedUtils
import java.nio.charset.Charset
import scala.collection.JavaConverters._
import kornell.server.repository.TOs
import kornell.core.entity.RegistrationType
import java.util.logging.Logger
import java.util.logging.Level



object PostbackService {
  
  val logger = Logger.getLogger("kornell.server.repository.service.PostbackService")
  
  //One URL for sandbox transactions, one for live
  val sandbox_validation_url = "https://www.sandbox.paypal.com/cgi-bin/webscr"
  val validation_url = "https://www.paypal.com/cgi-bin/webscr"

  
  def paypalPostback(env: String, payload: String) = {
    //Need to prepend a string to message we send back to validate authenticity
    val validation_message = "?cmd=_notify-validate&" + payload
    val hello = new Thread(new Runnable {
      def run() {
        try {
          val current_url = if (env != "live") sandbox_validation_url else validation_url
          val client = HttpClients.createDefault
          val request = new HttpPost(current_url + validation_message)
          val response = client.execute(request)
          val paypal_confirmation = EntityUtils.toString(response.getEntity)
          response.close()
          client.close()
          if (paypal_confirmation == "VERIFIED") {
            logger.info("Verified request " + payload)
            //create enrollment
            val payloadMap = URLEncodedUtils.parse(payload, Charset.forName("utf-8")).asScala.map(t => t.getName -> t.getValue).toMap
            val enrollmentRequest = TOs.tos.newEnrollmentRequestTO.as
            enrollmentRequest.setFullName(payloadMap("address_name"))
            enrollmentRequest.setUsername(payloadMap("payer_email"))
            enrollmentRequest.setCourseClassUUID(payloadMap("item_number"))
            enrollmentRequest.setRegistrationType(RegistrationType.email)
            enrollmentRequest.setCancelEnrollment(false)
            RegistrationEnrollmentService.postbackRequestEnrollment(enrollmentRequest, payload)
          } else {
            logger.warning("Invalid request " + payload)
          }
        } catch {
          case e: Throwable=>logger.log(Level.SEVERE, "Exception while processing postback " + payload, e)
        }
      }
    })
    hello.start
  }
}