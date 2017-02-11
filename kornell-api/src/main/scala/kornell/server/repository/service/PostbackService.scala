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
import kornell.core.entity.PostbackType
import kornell.server.jdbc.repository.PostbackConfigRepo
import org.apache.http.client.methods.HttpGet
import kornell.server.jdbc.repository.CourseClassRepo


object PostbackService {
  
  val test_xml = <transaction>  
    <date>2011-02-10T16:13:41.000-03:00</date>  
    <code>9E884542-81B3-4419-9A75-BCC6FB495EF1</code>  
    <reference>REF1234</reference>
    <type>1</type>  
    <status>3</status>  
    <paymentMethod>  
        <type>1</type>  
        <code>101</code>  
    </paymentMethod>  
    <grossAmount>49900.00</grossAmount>  
    <discountAmount>0.00</discountAmount>
    <creditorFees>
        <intermediationRateAmount>0.40</intermediationRateAmount>
        <intermediationFeeAmount>1644.80</intermediationFeeAmount>
    </creditorFees> 
    <netAmount>49900.00</netAmount>  
    <extraAmount>0.00</extraAmount>  
    <installmentCount>1</installmentCount>  
    <itemCount>2</itemCount>  
    <items>  
        <item>  
            <id>0001</id>  
            <description>Produto PagSeguroI</description>  
            <quantity>1</quantity>  
            <amount>99999.99</amount>  
        </item>  
        <item>  
            <id>0002</id>  
            <description>Produto PagSeguroII</description>  
            <quantity>1</quantity>  
            <amount>99999.98</amount>  
        </item>  
    </items>  
    <sender>  
        <name>José Comprador</name>  
        <email>comprador@uol.com.br</email>  
        <phone>  
            <areaCode>99</areaCode>  
            <number>99999999</number>  
        </phone>  
    </sender>  
    <shipping>  
        <address>  
            <street>Av. PagSeguro</street>  
            <number>9999</number>  
            <complement>99o andar</complement>  
            <district>Jardim Internet</district>  
            <postalCode>99999999</postalCode>  
            <city>Cidade Exemplo</city>  
            <state>SP</state>  
            <country>ATA</country>  
        </address>  
        <type>1</type>  
        <cost>21.50</cost>  
    </shipping>
</transaction>
  
  val logger = Logger.getLogger("kornell.server.repository.service.PostbackService")
  
  //Paypal URLs
  //One URL for sandbox transactions, one for live
  val paypal_sandbox_validation_url = "https://www.sandbox.paypal.com/cgi-bin/webscr"
  val paypal_validation_url = "https://www.paypal.com/cgi-bin/webscr"

  //PagSeguro URLs
  //One for sandbox, one for live
  val pag_sandbox_get_trans_url = "https://ws.sandbox.pagseguro.uol.com.br/v3/transactions/notifications/"
  val pag_get_trans_url = "https://ws.pagseguro.uol.com.br/v3/transactions/notifications/"
  
  def paypalPostback(env: String, payload: String) = {
    //Need to prepend a string to message we send back to validate authenticity
    val validation_message = "?cmd=_notify-validate&" + payload
    val hello = new Thread(new Runnable {
      def run() {
        try {
          val current_url = if (env != "live") paypal_sandbox_validation_url else paypal_validation_url
          val postbackType = if (env != "live") PostbackType.PAYPAL_SANDBOX else PostbackType.PAYPAL
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
            
            val token = payloadMap("custom")
            val courseClassUUID = payloadMap("item_number")
            val institutionUUID = new CourseClassRepo(courseClassUUID).get.getInstitutionUUID
            val postbackConfig = PostbackConfigRepo.checkConfig(institutionUUID, postbackType, token).getOrElse(null)
            
            if (postbackConfig != null) {
              val enrollmentRequest = TOs.tos.newEnrollmentRequestTO.as
              enrollmentRequest.setFullName(payloadMap("address_name"))
              enrollmentRequest.setUsername(payloadMap("payer_email"))
              enrollmentRequest.setCourseClassUUID(courseClassUUID)
              enrollmentRequest.setInstitutionUUID(institutionUUID)
              enrollmentRequest.setRegistrationType(RegistrationType.email)
              enrollmentRequest.setCancelEnrollment(false)
              RegistrationEnrollmentService.postbackRequestEnrollment(enrollmentRequest, payload)
            } else {
              logger.severe("Mismatched token for institution " + payload)
            }
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
  
  def pagseguroPostback(env: String, institutionUUID: String, transactionId: String) = {
    val postbackType = if (env != "live") PostbackType.PAGSEGURO_SANDBOX else PostbackType.PAGESGURO
    val current_url = if (env != "live") pag_sandbox_get_trans_url else pag_get_trans_url
    val postbackConfig = PostbackConfigRepo.getConfig(institutionUUID, postbackType).getOrElse(null)
    if (postbackConfig == null) {
      logger.log(Level.SEVERE, "Missing postback config for Pagseguro transaction ID  " + transactionId + ", could not process")
    } else {
      val email = postbackConfig.getContents.split("##")(0)
      val token = postbackConfig.getContents.split("##")(1)
      val get_url = current_url + transactionId + "?email=" + email + "&token=" + token
           
      println((test_xml \\ "sender" \\ "email").text)
      println((test_xml \\ "sender" \\ "name").text)
    }
    
  }
}