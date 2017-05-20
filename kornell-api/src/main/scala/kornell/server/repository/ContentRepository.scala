package kornell.server.repository

import kornell.server.dev.util.ContentsParser
import kornell.server.jdbc.repository.CourseClassesRepo
import kornell.core.entity.Person
import javax.xml.parsers.DocumentBuilderFactory
import scala.collection.mutable.ListBuffer
import javax.xml.xpath.XPathFactory
import org.w3c.dom.NodeList
import javax.xml.xpath.XPathConstants
import java.io.ByteArrayInputStream
import kornell.core.util.StringUtils._
import kornell.server.jdbc.repository.PersonRepo
import kornell.core.entity.Enrollment
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.content.ContentManagers
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.server.service.S3Service

@Deprecated
object ContentRepository {

  def findKNLVisitedContent(enrollment: Enrollment, person: Person) = {
    val personRepo = PersonRepo(enrollment.getPersonUUID)
    val visited = personRepo.actomsVisitedBy(enrollment.getUUID)  
    val institutionRepo = InstitutionRepo(person.getInstitutionUUID)
    val repositoryUUID = institutionRepo.get.getAssetsRepositoryUUID  
    val version = {
      if(enrollment.getCourseVersionUUID != null)
        CourseVersionRepo(enrollment.getCourseVersionUUID)
       else 
        CourseClassesRepo(enrollment.getCourseClassUUID).version
    }.get
    val repo = ContentManagers.forRepository(repositoryUUID)
    val course = CourseRepo(version.getCourseUUID).get
    val structureSrc = repo.source(S3Service.CLASSROOM, course.getCode, version.getDistributionPrefix, "structure.knl")
    val structureText = structureSrc.get.mkString("")
    val prefix = repo.url(S3Service.CLASSROOM, course.getCode, version.getDistributionPrefix)
    val contents = ContentsParser.parse(prefix, structureText, visited)
    contents
  }

  val expression = "//resource/@href"
  lazy val xPath = XPathFactory.newInstance().newXPath()
  lazy val expr = xPath.compile(expression)
  
  def findSCORM12Actoms(courseClassUUID: String) = {
    /* i wish they were thread safe */
    val builderFactory = DocumentBuilderFactory.newInstance
    val builder = builderFactory.newDocumentBuilder
    /* </rant> */
    val classRepo = CourseClassesRepo(courseClassUUID)
    val institutionRepo = classRepo.institution
    val repositoryUUID = institutionRepo.get.getAssetsRepositoryUUID
    val versionRepo = classRepo.version
    val version = versionRepo.get
    val repo = ContentManagers.forRepository(repositoryUUID)
    val course = CourseRepo(version.getCourseUUID).get
    val structureIn = repo.inputStream(S3Service.CLASSROOM, mkurl(course.getCode, version.getDistributionPrefix, "imsmanifest.xml")).get
    val document = builder.parse(structureIn)
    val result = ListBuffer[String]()
    val nodes: NodeList = expr.evaluate(document, XPathConstants.NODESET).asInstanceOf[NodeList]
    for (i <- 0 until nodes.getLength) {
      result += nodes.item(i).getFirstChild.getNodeValue
    }
    structureIn.close
    result.toList
  }
}