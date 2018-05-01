package kornell.server.util

import java.io.File
import java.util.{Date, Properties}
import java.util.concurrent.{ExecutorService, Executors}
import java.util.logging.{Level, Logger}

import javax.mail.{Message, Multipart, Part, Session}
import javax.mail.internet.{InternetAddress, MimeBodyPart, MimeMessage, MimeMultipart}
import kornell.server.util.Settings._

object EmailSender {
  val logger: Logger = Logger.getLogger("kornell.server.email")

  val executor: ExecutorService = Executors.newSingleThreadExecutor

  def sendEmailSync(subject: String,
                    from: String,
                    to: String,
                    replyTo: String,
                    body: String,
                    imgFile: File = null,
                    personUUID: String = null): Unit = try {
    getEmailSession match {
      case Some(session) => {
        val message = new MimeMessage(session)
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
        transport.connect(SMTP_USERNAME, SMTP_PASSWORD)

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
    override def run(): Unit = sendEmailSync(subject, from, to, replyTo, body, imgFile, personUUID)
  })

  private def getEmailSession: Option[Session] =
    SMTP_HOST.getOpt map { host =>
      logger.finer(s"Creating smtp session for host [$host]")
      val props = new Properties()
      props.put("mail.smtp.auth", "true")
      props.put("mail.smtp.host", host)
      props.put("mail.smtp.port", SMTP_PORT.get)
      props.put("mail.smtp.ssl.enable", "true")
      props.put("mail.transport.protocol", "smtp")
      props.put("mail.smtp.starttls.enable", "true")
      Session.getDefaultInstance(props);
    }
}