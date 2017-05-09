package kornell.server.service

import kornell.core.to.SimpleCVCTO
import kornell.core.entity.EntityFactory
import kornell.server.repository.Entities
import kornell.core.util.UUID
import java.util.Date
import kornell.core.entity.RegistrationType
import java.math.BigDecimal
import kornell.core.entity.CourseClassState
import kornell.server.authentication.ThreadLocalAuthenticator
import scala.collection.mutable.ArrayBuffer

object SimpleCVCCreationService {
  
  def generateInfoJson(to: SimpleCVCTO) = {
    val hintsList = ArrayBuffer[String]()
    
    val courseDurationJson = "{\"type\":\"fa fa-clock-o\",\"text\":\"" + to.getCourseDuration + "\"}"
    hintsList.append(courseDurationJson)
    val helpJson = "{\"type\":\"fa fa-question-circle\",\"text\":\"Se precisar entrar em contato, clique em ajuda no menu acima.\"}"
    hintsList.append(helpJson)
    
    if (to.isGenerateCertificate()) {
      val generateCertificateJson = "{\"type\":\"fa fa-certificate\",\"text\":\"Curso com certificação.\"}"
      hintsList.append(generateCertificateJson)
    }
    
    if (to.isMultimediaContent()) {
      val multimediaJson = "{\"type\":\"fa fa-warning\",\"text\":\"Este curso contém vídeo e áudio, certifique que seu dispositivo possa reproduzi-los.\"}"
      hintsList.append(multimediaJson)
    }
    
    val description = "\"infos\":[{\"type\":\"Apresentação\",\"text\":\"" + to.getDescription + "\"}]"
    
    val json = "{\"hints\":[" + hintsList.mkString(",") + "]," + description + "}"
    println(json)
    json
  }
  
  def createCourse(institutionUUID: String, to: SimpleCVCTO) = {
    val courseUUID = UUID.random
    val infoJson = generateInfoJson(to)
    val course = Entities.newCourse(
        courseUUID,
        courseUUID,
        to.getName,
        to.getDescription,
        infoJson,
        institutionUUID,
        false)
    course
  }
  
  def createCourseVersion(courseUUID: String, to: SimpleCVCTO) = {
    val versionUUID = UUID.random
    val version = Entities.newCourseVersion(
        versionUUID,
        to.getName,
        courseUUID,
        new Date(),
        versionUUID,
        to.getContentSpec.toString,
        false,
        null,
        1,
        null)
    version
  }
  
  def createCourseClass(institutionUUID: String, versionUUID: String, to: SimpleCVCTO) = {
    val classUUID = UUID.random
    val courseClass = Entities.newCourseClass(
        classUUID,
        to.getName,
        versionUUID,
        institutionUUID,
        new BigDecimal(60.00),
        to.isAutoApprove(),
        false,
        false,
        1000,
        new Date(),
        ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get,
        CourseClassState.active,
        RegistrationType.email,
        null,
        false,
        false,
        false,
        false,
        to.isAutoApprove(),
        null,
        null)
    courseClass
  }
  
  def simpleCreation(institutionUUID: String, to: SimpleCVCTO) = {
    val course = createCourse(institutionUUID, to);
    val version = createCourseVersion(course.getUUID, to)
    val courseClass = createCourseClass(institutionUUID, version.getUUID, to)
    
  }
}