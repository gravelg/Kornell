package kornell.server.util

import java.io.File
import java.net.URL
import org.apache.commons.io.FileUtils
import kornell.core.entity.Institution
import kornell.core.entity.Person
import java.util.UUID
import kornell.core.entity.Course
import kornell.core.entity.PersonCategory
import kornell.core.entity.CourseClass
import  kornell.core.util.StringUtils._
import kornell.server.jdbc.repository.PersonRepo
import kornell.core.entity.Enrollment
import kornell.server.jdbc.repository.InstitutionEmailWhitelistRepo
import kornell.core.entity.ChatThread
import kornell.server.util.Settings._
import java.nio.file.Path
import java.nio.file.Paths
import java.util.logging.Logger
import java.util.logging.Level


object EmailService {
  
  val logger = Logger.getLogger("kornell.server.email")
  
  def sendEmailBatchEnrollment(person: Person, institution: Institution, courseClass: CourseClass) = {
    if (checkWhitelistForDomain(institution, person.getEmail)) {
      val subject = "A geração de matrículas foi concluída."
      val from = getFromEmail(institution)
      val to = person.getEmail
      val body = wrapBody("""
    		<p>Ol&aacute;, <b>""" + person.getFullName + """</b></p>
    		<p>&nbsp;</p>
    		<p>A geração de matrículas na turma """ + courseClass.getName + """ foi concluída.</p> """ +
    		getActionButton(institution.getBaseURL+"#a.courseClass:"+courseClass.getUUID, "Ir para a turma") + """
    		<p>&nbsp;</p>""" +
    		getSignature(institution))
			
      val imgFile = getInstitutionLogoImage(institution)
      EmailSender.sendEmail(subject, from, to, body, imgFile)
    }
  }
  
  def sendEmailConfirmation(person: Person, institution: Institution) = {
    if (checkWhitelistForDomain(institution, person.getEmail)) {
      val subject = "Bem-vind" + PersonCategory.getSexSuffix(person) + " à " + institution.getFullName + "!"
      val from = getFromEmail(institution)
      val to = person.getEmail
      val body = wrapBody("""
    		<p>Ol&aacute;, <b>""" + person.getFullName + """</b></p>
    		<p>&nbsp;</p>
    		<p>Bem-vind""" + PersonCategory.getSexSuffix(person) + """ &agrave; """ + institution.getFullName + """.</p>
    		<p>Por favor, confirme seu cadastro para ativarmos a sua conta.</p> """ +
    		getActionButton(institution.getBaseURL+"#vitrine:", "Confirmar agora") + """
    		<p>Depois da ativa&ccedil;&atilde;o voc&ecirc;  poder&aacute; acessar os cursos dispon&iacute;veis para voc&ecirc;, assim como todos os recursos deste ambiente.</p>
    		<p>Aproveite para trocar experi&ecirc;ncias e ampliar o seu conhecimento.</p>
    		<p>&nbsp;</p>""" +
    		getSignature(institution))
			
      val imgFile = getInstitutionLogoImage(institution)
      EmailSender.sendEmail(subject, from, to, body, imgFile)
    }
  }
  
  def sendEmailRequestPasswordChange(person: Person, institution: Institution, requestPasswordChangeUUID: String) = {
    if (checkWhitelistForDomain(institution, person.getEmail)) {
      val subject = "Você requisitou uma nova senha da " + institution.getFullName
      val from = getFromEmail(institution)
      val to = person.getEmail
      val actionLink = institution.getBaseURL+"#vitrine:" + requestPasswordChangeUUID
      val body = wrapBody("""
    		<p>Ol&aacute;, <b>""" + person.getFullName + """</b></p>
    		<p>&nbsp;</p>
    		<p>Voc&ecirc; recentemente fez uma requisi&ccedil;&atilde;o de altera&ccedil;&atilde;o de senha da """+ institution.getFullName +""".</p>
    		<p>Clique no bot&atilde;o abaixo para fazer a altera&ccedil;&atilde;o da senha.</p> """ +
    		getActionButton(actionLink, "Alterar senha") + """
    		<p>Caso n&atilde;o tenha requisitado esta mudan&ccedil;a, favor ignorar esta mensagem.</p>
    		<p>&nbsp;</p>""" +
    		getSignature(institution))
			
      val imgFile = getInstitutionLogoImage(institution)
      EmailSender.sendEmail(subject, from, to, body, imgFile)
    }
  }
  
  //TODO: Consider ASYNC
  def sendEmailEnrolled(person: Person, institution: Institution, course: Course, enrollment: Enrollment, courseClass: CourseClass) = {
    if (checkWhitelistForDomain(institution, person.getEmail) && person.isReceiveEmailCommunication) {
      val subject = "Você foi matriculado no curso " + course.getTitle
      val from = getFromEmail(institution)
      val to = person.getEmail
      val className = if(courseClass == null){
        " no"
      } else {
        """ na turma <b>"""+ courseClass.getName + """</b> do"""
      }
      val hasPassword = PersonRepo(person.getUUID).hasPassword(institution.getUUID)
      val hasPasswordStr = if(hasPassword) {
        ""
      } else {
        "<p>Caso seja seu primeiro acesso, cadastre-se primeiro no sistema utilizando este email: " + to + "</p>"
      }
      val actionLink = if(hasPassword) {
        institution.getBaseURL + "#classroom:" + enrollment.getUUID
      } else {
        institution.getBaseURL + "#vitrine:" + person.getEmail
      }
      val body = wrapBody("""
    		<p>Ol&aacute;, <b>""" + person.getFullName + """</b></p>
    		<p>&nbsp;</p>
    		<p>Voc&ecirc; foi matriculad""" + PersonCategory.getSexSuffix(person) + className + """ curso <b>"""+ course.getTitle +"""</b> oferecido pela <b>"""+ institution.getFullName +"""</b>.</p> 
    		<p>Clique no bot&atilde;o abaixo para ir ao curso.</p> """ +
    		getActionButton(actionLink, "Acessar o Curso") + 
    		hasPasswordStr +
    		"""<p>&nbsp;</p>""" +
    		getSignature(institution))
			
      val imgFile = getInstitutionLogoImage(institution)
      EmailSender.sendEmail(subject, from, to, body, imgFile)
    }
  }
  
  def sendEmailNewChatThread(person: Person, institution: Institution, courseClass: CourseClass, chatThread: ChatThread, message: String) = {
    if (checkWhitelistForDomain(institution, person.getEmail) && person.isReceiveEmailCommunication) {
      val subject = "Uma nova conversa " + {
        if("SUPPORT".equalsIgnoreCase(chatThread.getThreadType))
          "de ajuda criada para a turma: " + courseClass.getName
        else if("INSTITUTION_SUPPORT".equalsIgnoreCase(chatThread.getThreadType))
          "de ajuda de instituição criada para a turma: " + courseClass.getName
        else if("PLATFORM_SUPPORT".equalsIgnoreCase(chatThread.getThreadType))
          "de ajuda criada para a instituição: " + institution.getName
        else if("TUTORING".equalsIgnoreCase(chatThread.getThreadType))
          "de tutoria criada para a turma: " + courseClass.getName
      }
      val from = getFromEmail(institution)
      val to = person.getEmail
      val participant = PersonRepo(chatThread.getPersonUUID).get
      if(!participant.getUUID.equals(person.getUUID)){
	      val actionLink = institution.getBaseURL + "#message:"
	      val body = wrapBody("""
	            <p>Ol&aacute;, <b>""" + person.getFullName + """</b></p>
	            <p>&nbsp;</p>
	            <p>""" + subject +""".</p> 
	            <p>&nbsp;</p>
	            <p>Participante: """ + participant.getFullName + """  (""" + participant.getEmail + """)""" +""".</p> 
	            <p>&nbsp;</p>
	            <p>Mensagem: <br /><br /><i>""" + message.replace("\n", "<br />\n") + """</i></p>
	            <p>&nbsp;</p>
	            <p>Clique no bot&atilde;o abaixo para acessar a conversa.</p> """ +
	            getActionButton(actionLink, "Acessar a Conversa") + 
	            """<p>&nbsp;</p>""" +
	    		"""<img alt="" src="cid:logo" style="width: 300px;height: 80px;margin: 0 auto;display: block;">""")
	            
	      val imgFile = getInstitutionLogoImage(institution)
	      EmailSender.sendEmail(subject, from, to, body, imgFile)
      }
    }
  }
  
  def sendEmailEspinafreReminder(person: Person, institution: Institution) = {
    if (person.isReceiveEmailCommunication) {
      val subject = "Espinafre Reminder! " + institution.getFullName
      val from = getFromEmail(institution)
      val to = person.getEmail
      val actionLink = institution.getBaseURL
      val body = wrapBody("""
    	espinafre reminder body	
        """ +
    		getSignature(institution))
			
      val imgFile = getInstitutionLogoImage(institution)
      EmailSender.sendEmail(subject, from, to, body, imgFile)
    }
  }
  
  private def getFromEmail(institution: kornell.core.entity.Institution):String = {
    if (institution.getInstitutionSupportEmail != null)
      institution.getInstitutionSupportEmail
    else
      SMTP_FROM.get
  }
  
  
  private def wrapBody(bodyText: String) = 
    """
    	<div style="width: 700px;margin: 0 auto;padding: 20px 50px;border: 1px solid #CACACA;color: #444444;font-size: 18px;font-family: Helvetica,Arial,sans-serif;-webkit-box-shadow: 14px 14px 5px #AFAFAF;-moz-box-shadow: 14px 14px 5px #AFAFAF;box-shadow: 14px 14px 5px #AFAFAF;margin-bottom: 30px;border-radius: 10px;">""" +
			bodyText + """
		</div>
    """
  
  private def getSignature(institution: Institution): String = 
    """
	  	<p>Cordialmente,</p>
	  	<p><b>Equipe """ + institution.getFullName + """</b></p>
	  	<img alt="" src="cid:logo" style="width: 300px;height: 80px;margin: 0 auto;display: block;">
	"""

  private def getInstitutionLogoImage(institution: Institution): java.io.File = {
    val logoImageName: String = "logo300x80.png"
    val tempDir: Path = Paths.get(System.getProperty("java.io.tmpdir"))
    val imgPath = tempDir.resolve(institution.getUUID + "-" + logoImageName)
    val imgFile: File = imgPath.toFile()
    
    val purgeTime = System.currentTimeMillis - (1 * 24 * 60 * 60 * 1000) //one day
    if(imgFile.lastModified < purgeTime && !imgFile.delete)
      logger.warning("Unable to delete file: " + imgFile)
      
    if (!imgFile.exists) {
      //TODO: Use ContentStore API
      val url = new URL(mkurl(institution.getBaseURL, "repository", institution.getAssetsRepositoryUUID, logoImageName))
      try {
        FileUtils.copyURLToFile(url, imgFile)
      } catch {
        case e: Exception => logger.log(Level.SEVERE, "Cannot copy institution logo ", e)
      }
    }
    imgFile
  }

  def getActionButton(actionLink: String, actionButtonText: String) = 
    """
		<div style="width: 300px;margin: 0 auto;margin-bottom: 50px;margin-top: 50px;text-align: center;display: block;height: auto;">
			<a href="""" + actionLink + """" target="_blank" style="text-decoration: none;">
				<div style="display: block;width: 220px;height: 30px;line-height: 28px;margin-right: 30px;text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.5);font-size: 14px;font-weight: bold;color: #e9e9e9;border-radius: 5px;display: block;border: 0px;background: #4c4f54;background: -moz-linear-gradient(top, #4c4f54 0%, #252629 100%);background: -webkit-gradient(linear, left top, left bottom, color-stop(0%, #4c4f54), color-stop(100%, #252629));background: -webkit-linear-gradient(top, #4c4f54 0%, #252629 100%);background: -o-linear-gradient(top, #4c4f54 0%, #252629 100%);background: -ms-linear-gradient(top, #4c4f54 0%, #252629 100%);background: linear-gradient(to bottom, #4c4f54 0%, #252629 100%);filter: progid:DXImageTransform.Microsoft.gradient( startColorstr='#4c4f54', endColorstr='#252629',GradientType=0 );margin: 0 auto;">""" + actionButtonText + """</div>
			</a>
		</div>
	"""
				
  def checkWhitelistForDomain(institution: Institution, email: String) = {
    //If we don't use the whitelist, just continue with the sending
    if (!institution.isUseEmailWhitelist) {
      true
    } else {
      //If we use the whitelist, we have to check that the domain works.
      InstitutionEmailWhitelistRepo(institution.getUUID).get.getDomains.contains(email.split("@")(1))
    }
  }				
}