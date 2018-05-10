package kornell.server.util

import java.io.File
import java.util.Date
import java.util.Properties
import javax.mail.Message
import javax.mail.MessagingException
import javax.mail.Multipart
import javax.mail.Part
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import javax.mail.Authenticator
import kornell.core.util.StringUtils
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.logging.Logger
import kornell.server.util.Settings._
import java.util.logging.Level

object EmailSender {
  val logger = Logger.getLogger("kornell.server.email")

  val executor = Executors.newSingleThreadExecutor

  def sendEmailSync(subject: String,
                    from: String,
                    to: String,
                    replyTo: String,
                    body: String,
                    imgFile: File = null,
                    personUUID: String = null): Unit = try {
    getEmailSession match {
      case Some(session) => {
        val message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from))
        message.setRecipients(Message.RecipientType.TO, to)
        message.setSentDate(new Date())
        message.setSubject(subject, "UTF-8")
        message.setReplyTo(Array(new InternetAddress(replyTo)))
        // creates message part
        val messageBodyPart: MimeBodyPart = new MimeBodyPart()
        messageBodyPart.setContent(body, "text/html; charset=utf-8")

        // creates multi-part
        val multipart: Multipart = new MimeMultipart()
        multipart.addBodyPart(messageBodyPart)

        if (imgFile != null) {
          val imagePartLogo: MimeBodyPart = new MimeBodyPart()
          imagePartLogo.setHeader("Content-ID", "<logo>")
          imagePartLogo.setDisposition(Part.INLINE)
          imagePartLogo.attachFile(imgFile)
          multipart.addBodyPart(imagePartLogo)
        }
        if (personUUID != null) {
          message.setHeader("X-KNL-UUID", personUUID)
        }

        message.setContent(multipart)

        val transport = session.getTransport
        val username = SMTP_USERNAME
        val password = SMTP_PASSWORD
        transport.connect(username, password)

        val test_mode: String = Settings.TEST_MODE
        if (!"true".equals(test_mode.orNull)) {
          transport.sendMessage(message, Array(new InternetAddress(to)))
        }

        logger.finer(s"Email with subject [$subject] sent to [$to] by [$from]")
      }
      case None => logger.warning(s"No SMTP configured. Email could not be sent to [$to]")
    }
  } catch {
    case e: Exception => logger.log(Level.SEVERE, "Problem sending email ", e)
  }

  def sendEmail(subject: String,
                from: String,
                to: String,
                replyTo: String,
                body: String,
                imgFile: File = null,
                personUUID: String = null): Unit = executor.submit(new Runnable() {
    override def run: Unit = sendEmailSync(subject, from, to, replyTo, body, imgFile, personUUID)
  })

  private def getEmailSession =
    SMTP_HOST.getOpt map { host =>
      logger.finer(s"Creating smtp session for host [$host]")
      val props = new Properties()
      props.put("mail.smtp.auth", "true")
      props.put("mail.smtp.host", host)
      props.put("mail.smtp.port", SMTP_PORT.get)
      props.put("mail.smtp.ssl.enable", "true");
      props.put("mail.transport.protocol", "smtp");
      props.put("mail.smtp.starttls.enable", "true");
      Session.getDefaultInstance(props);
    }

}