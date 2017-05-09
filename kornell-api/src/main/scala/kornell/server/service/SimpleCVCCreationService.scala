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
import kornell.core.entity.CourseDetailsEntityType
import kornell.server.jdbc.repository.CourseDetailsHintsRepo
import kornell.server.jdbc.repository.CourseDetailsSectionsRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.repository.TOs

object SimpleCVCCreationService {
  
  def generateCourseDetails(courseUUID: String, to: SimpleCVCTO) = {
    val duration = CourseDetailsHintsRepo.create(Entities.newCourseDetailsHint(UUID.random, "duration",
        to.getCourseDuration, CourseDetailsEntityType.COURSE, courseUUID, 0, "fa fa-clock-o"))
    val help = CourseDetailsHintsRepo.create(Entities.newCourseDetailsHint(UUID.random, "help", 
        "Se precisar entrar em contato, clique em ajuda no menu acima.", CourseDetailsEntityType.COURSE, courseUUID, 1, "fa fa-question-circle"))
    var hintIndex = 1
    
    if (to.isGenerateCertificate()) {
      hintIndex = hintIndex + 1
      val certificate = CourseDetailsHintsRepo.create(Entities.newCourseDetailsHint(UUID.random, "certificate",
          "Curso com certificação.", CourseDetailsEntityType.COURSE, courseUUID, hintIndex, "fa fa-certificate"))
    }
    
    if (to.isMultimediaContent()) {
      hintIndex = hintIndex + 1
      val multimedia = CourseDetailsHintsRepo.create(Entities.newCourseDetailsHint(UUID.random, "multimedia-warning",
          "Este curso contém vídeo e áudio, certifique que seu dispositivo possa reproduzi-los.", CourseDetailsEntityType.COURSE, courseUUID, hintIndex, "fa fa-warning"))
    }
    
    val description = CourseDetailsSectionsRepo.create(Entities.newCourseDetailsSection(UUID.random, "Apresentação", to.getDescription, CourseDetailsEntityType.COURSE, courseUUID, 0))
  }
  
  def createCourse(institutionUUID: String, to: SimpleCVCTO) = {
    val courseUUID = UUID.random
    val course = CoursesRepo.create(Entities.newCourse(
        courseUUID,
        courseUUID,
        to.getName,
        to.getDescription,
        null,
        institutionUUID,
        false))
    generateCourseDetails(courseUUID, to)
    course
  }
  
  def createCourseVersion(institutionUUID: String, courseUUID: String, to: SimpleCVCTO) = {
    val versionUUID = UUID.random
    val version = CourseVersionsRepo.create(Entities.newCourseVersion(
        versionUUID,
        to.getName,
        courseUUID,
        new Date(),
        versionUUID,
        to.getContentSpec.toString,
        false,
        null,
        1,
        null), institutionUUID)
    version
  }
  
  def createCourseClass(institutionUUID: String, versionUUID: String, to: SimpleCVCTO) = {
    val courseClass = CourseClassesRepo.create(Entities.newCourseClass(
        UUID.random,
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
        null))
    courseClass
  }
  
  def simpleCreation(institutionUUID: String, to: SimpleCVCTO) = {
    val course = createCourse(institutionUUID, to);
    val courseVersion = createCourseVersion(institutionUUID, course.getUUID, to)
    val courseClass = createCourseClass(institutionUUID, courseVersion.getUUID, to)
    TOs.newSimpleCVCResponseTO(course, courseVersion, courseClass)
  }
}