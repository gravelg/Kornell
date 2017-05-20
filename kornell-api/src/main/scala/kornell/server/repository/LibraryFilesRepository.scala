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
import kornell.server.service.S3Service


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
    val filesURL = StringUtils.mkurl(S3Service.CLASSROOMS, course.getCode, version.getDistributionPrefix(), "classroom/library")
    try {
      val librarySrc = repo.source(filesURL, "libraryFiles.knl")
      val libraryFilesText = librarySrc.get.mkString("")
      val contents = LibraryFilesParser.parse(repo.url(filesURL), libraryFilesText)
      contents
    } catch {
      case e:Exception => TOs.newLibraryFilesTO(List[LibraryFileTO]())
    }
  }
}
