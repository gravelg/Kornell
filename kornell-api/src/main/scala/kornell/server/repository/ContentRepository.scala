package kornell.server.repository

import kornell.server.dev.util.ContentsParser
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.core.entity.Person
import scala.collection.mutable.ListBuffer
import kornell.core.util.StringUtils._
import kornell.server.jdbc.repository.PersonRepo
import kornell.core.entity.Enrollment
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.content.ContentManagers
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.server.service.S3Service
import scala.xml.XML
import kornell.server.scorm12.ManifestParser
import kornell.server.jdbc.repository.CoursesRepo
import kornell.core.entity.ContentSpec
import kornell.core.error.exception.ServerErrorException

object ContentRepository {

  def findVisitedContent(enrollment: Enrollment, person: Person) = {
    val contentSpec = CoursesRepo.byCourseClassUUID(enrollment.getCourseClassUUID).get.getContentSpec
    contentSpec match {
      case ContentSpec.KNL => findKNLVisitedContent(enrollment, person)
      case ContentSpec.SCORM12 => findSCORM12VisitedContent(enrollment, person)
      case ContentSpec.WIZARD => throw new ServerErrorException("Wizard content not supported")
    }
  }

  def findSCORM12Actoms(courseClassUUID: String) = {
    val structureIn = getManifestContent(courseClassUUID, "imsmanifest.xml")
    val contents = XML.load(structureIn)

    val nodes = (contents \\ "resource")
    val result = ListBuffer[String]()
    nodes.map(x => result += x.attribute("href").get.toString)
    result.toList
  }

  def findKNLVisitedContent(enrollment: Enrollment, person: Person) = {
    val prefix = getPrefix(enrollment, person)
    val visited = getVisited(enrollment)
    val structureSrc = getManifestContent(enrollment.getCourseClassUUID, "structure.knl")
    val content = ContentsParser.parse(prefix, structureSrc, visited)
    content
  }

  def findSCORM12VisitedContent(enrollment: Enrollment, person: Person) = {
    val prefix = getPrefix(enrollment, person)
    val visited = getVisited(enrollment)
    val structureSrc = getManifestContent(enrollment.getCourseClassUUID, "imsmanifest.xml")
    val contents = ManifestParser.parse(prefix, structureSrc, visited)
    contents
  }


  private def getPrefix(enrollment: Enrollment, person: Person): String = {
    val institutionRepo = InstitutionRepo(person.getInstitutionUUID)
    val repositoryUUID = institutionRepo.get.getAssetsRepositoryUUID
    val repo = ContentManagers.forRepository(repositoryUUID)
    val version = {
      if (enrollment.getCourseVersionUUID != null)
        CourseVersionRepo(enrollment.getCourseVersionUUID)
       else 
        CourseClassesRepo(enrollment.getCourseClassUUID).version
    }.get
    val course = CourseRepo(version.getCourseUUID).get
    val prefix = repo.url(S3Service.CLASSROOMS, course.getCode, version.getDistributionPrefix)
    prefix
  }

  private def getVisited(enrollment: Enrollment): List[String] = {
    val personRepo = PersonRepo(enrollment.getPersonUUID)
    val visited = personRepo.actomsVisitedBy(enrollment.getUUID)
    visited
  }

  private def getManifestContent(courseClassUUID: String, filename: String) = {
    val classRepo = CourseClassesRepo(courseClassUUID)
    val institutionRepo = classRepo.institution
    val repositoryUUID = institutionRepo.get.getAssetsRepositoryUUID
    val versionRepo = classRepo.version
    val version = versionRepo.get
    val repo = ContentManagers.forRepository(repositoryUUID)
    val course = CourseRepo(version.getCourseUUID).get
    val structureIn = repo.inputStream(S3Service.CLASSROOMS, mkurl(course.getCode, version.getDistributionPrefix, filename)).get
    structureIn
  }
}