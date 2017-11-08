package kornell.server.service

import java.util.Date

import kornell.core.entity.ContentSpec
import kornell.core.entity.Course
import kornell.core.entity.CourseDetailsEntityType
import kornell.core.util.StringUtils
import kornell.core.util.UUID
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.server.jdbc.repository.CourseDetailsHintsRepo
import kornell.server.jdbc.repository.CourseDetailsSectionsRepo
import kornell.server.jdbc.repository.CourseVersionsRepo
import kornell.server.jdbc.repository.CoursesRepo
import kornell.server.repository.Entities
import kornell.server.repository.TOs
import kornell.core.entity.RegistrationType
import kornell.core.entity.EntityState
import kornell.server.authentication.ThreadLocalAuthenticator
import java.math.BigDecimal
import java.text.SimpleDateFormat

object CourseCreationService {
  
  def generateCourseDetails(courseUUID: String, to: Course) = {
    val duration = CourseDetailsHintsRepo.create(Entities.newCourseDetailsHint(UUID.random, "duration",
        "Carga de estudo: XXX horas", CourseDetailsEntityType.COURSE, courseUUID, 0, "fa fa-clock-o"))
    val help = CourseDetailsHintsRepo.create(Entities.newCourseDetailsHint(UUID.random, "help", 
        "Se precisar entrar em contato, clique em ajuda no menu acima.", CourseDetailsEntityType.COURSE, courseUUID, 1, "fa fa-question-circle"))
    val multimedia = CourseDetailsHintsRepo.create(Entities.newCourseDetailsHint(UUID.random, "multimedia-warning",
          "Este curso contém vídeo e áudio, certifique que seu dispositivo possa reproduzi-los.", CourseDetailsEntityType.COURSE, courseUUID, 2, "fa fa-warning"))
    val certificate = CourseDetailsHintsRepo.create(Entities.newCourseDetailsHint(UUID.random, "certificate",
          "Curso com certificação.", CourseDetailsEntityType.COURSE, courseUUID, 3, "fa fa-certificate"))
    
    val description = CourseDetailsSectionsRepo.create(Entities.newCourseDetailsSection(UUID.random, "Apresentação", to.getDescription, CourseDetailsEntityType.COURSE, courseUUID, 0))
  }
  
  def createCourse(institutionUUID: String, to: Course) = {
    val courseUUID = UUID.random
    if (StringUtils.isNone(to.getCode)) {
      val formattedDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date)
      to.setCode(to.getName.toLowerCase.replaceAll(" ", "-") + "-" + formattedDate)
    }
    
    val course = CoursesRepo.create(Entities.newCourse(
        courseUUID,
        to.getCode,
        to.getName,
        to.getDescription,
        null,
        EntityState.active,
        institutionUUID,
        false,
        null,
        to.getContentSpec))
    generateCourseDetails(courseUUID, to)
    course
  }
  
  def createCourseVersion(institutionUUID: String, courseUUID: String, to: Course) = {
    val versionUUID = UUID.random
    val formattedDate = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date)
    val distributionPrefix = to.getName.toLowerCase.replaceAll(" ", "-") + "-" + formattedDate
    val version = CourseVersionsRepo.create(Entities.newCourseVersion(
        versionUUID,
        "v1",
        courseUUID,
        new Date(),
        distributionPrefix,
        EntityState.active,
        false,
        null,
        1,
        null), institutionUUID)
    version
  }
  
  def createCourseClass(institutionUUID: String, versionUUID: String, to: Course) = {
    val courseClass = CourseClassesRepo.create(Entities.newCourseClass(
        UUID.random,
        to.getName,
        versionUUID,
        institutionUUID,
        new BigDecimal(0.00),
        false,
        false,
        false,
        1000,
        new Date(),
        ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get,
        EntityState.active,
        RegistrationType.email,
        null,
        false,
        false,
        false,
        false,
        false,
        null,
        null))
    courseClass
  }
  
  def simpleCreation(institutionUUID: String, to: Course) = {
    val course = createCourse(institutionUUID, to);
    val courseVersion = createCourseVersion(institutionUUID, course.getUUID, to)
    val courseClass = createCourseClass(institutionUUID, courseVersion.getUUID, to)
    TOs.newCourseClassTO(course, courseVersion, courseClass, null)
  }
}