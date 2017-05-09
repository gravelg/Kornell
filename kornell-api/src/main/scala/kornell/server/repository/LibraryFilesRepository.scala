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
import kornell.server.dev.util.LibraryFilesParser
import kornell.core.util.StringUtils
import scala.util.Try
import kornell.core.to.LibraryFileTO
import kornell.server.content.ContentManagers
import kornell.core.util.StringUtils._
import kornell.server.jdbc.repository.InstitutionsRepo
import kornell.server.jdbc.repository.CourseRepo


object LibraryFilesRepository {
//TODO: Review
  def findLibraryFiles(courseClassUUID: String) =  {
    val classRepo = CourseClassesRepo(courseClassUUID)
    val institutionRepo = classRepo.institution
    val repositoryUUID = institutionRepo.get.getAssetsRepositoryUUID
    val repo = ContentManagers.forRepository(repositoryUUID)
    val versionRepo = classRepo.version
    val version = versionRepo.get
    val course = CourseRepo(version.getCourseUUID).get
    val filesURL = StringUtils.mkurl(course.getCode, version.getDistributionPrefix(), "classroom/library")
    try {
      val structureSrc = repo.source(filesURL, "libraryFiles.knl")
      val libraryFilesText = structureSrc.get.mkString("")
      val fullURL = repo.url(course.getCode, version.getDistributionPrefix(), "classroom", "library")
      val contents = LibraryFilesParser.parse(fullURL, libraryFilesText)
      contents
    } catch {
      case e:Exception => TOs.newLibraryFilesTO(List[LibraryFileTO]())
    }
  }
}
