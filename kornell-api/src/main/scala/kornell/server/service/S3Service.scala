package kornell.server.service

import java.net.URL
import java.util.Date

import com.amazonaws.HttpMethod
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import kornell.core.entity.{ContentRepository, RepositoryType}
import kornell.core.util.StringUtils
import kornell.core.util.StringUtils._
import kornell.server.jdbc.repository.{ContentRepositoriesRepo, CourseClassRepo, CourseRepo, CourseVersionRepo, CoursesRepo, InstitutionRepo}
import org.joda.time.DateTime

object S3Service {

  def PREFIX = "knl"
  def CLASSROOMS = "classrooms"
  def INSTITUTION = "institution"
  def COURSES = "courses"
  def COURSE_VERSIONS = "courseVersions"
  def COURSE_CLASSES = "courseClasses"
  def REPORTS = "reports"
  def CERTIFICATES = "certificates"
  def CLASS_INFO = "classInfo"
  def CERTIFICATE_FILENAME = "certificate-bg.jpg"
  def THUMB_FILENAME = "thumb.jpg"

  def getCourseVersionContentUploadUrl(courseVersionUUID: String): String = {
    val courseVersion = CourseVersionRepo(courseVersionUUID).get
    val course = CourseRepo(courseVersion.getCourseUUID).get
    val fullPath = mkurl(getRepositoryUrl(course.getInstitutionUUID), CLASSROOMS, course.getCode, courseVersion.getDistributionPrefix, "upload" + new Date().getTime + ".zip")
    getUploadUrl(CourseRepo(courseVersion.getCourseUUID).get.getInstitutionUUID, fullPath, "application/zip")
  }

  def getCourseWizardContentUploadUrl(courseUUID: String, fileName: String): String = {
    val course = CourseRepo(courseUUID).get
    getCourseUploadUrl(course.getUUID, fileName, "__wizard", isAngularComponent = true)
  }

  def getCourseAssetUrl(institutionUUID: String, courseUUID: String, fileName: String, path: String): String = {
    mkurl(getRepositoryUrl(institutionUUID), mkurl(PREFIX, COURSES, courseUUID, path, fileName))
  }

  def getCourseUploadUrl(courseUUID: String, fileName: String, path: String, isAngularComponent: Boolean = false): String = {
    val institutionUUID = CourseRepo(courseUUID).get.getInstitutionUUID
    getUploadUrl(institutionUUID, getCourseAssetUrl(institutionUUID, courseUUID, fileName, path), getContentType(fileName, isAngularComponent))
  }

  def getCourseVersionAssetUrl(institutionUUID: String, courseVersionUUID: String, fileName: String, path: String): String = {
    mkurl(getRepositoryUrl(institutionUUID), mkurl(PREFIX, COURSE_VERSIONS, courseVersionUUID, path, fileName))
  }

  def getCourseVersionUploadUrl(courseVersionUUID: String, fileName: String, path: String): String = {
    val institutionUUID = CoursesRepo.byCourseVersionUUID(courseVersionUUID).get.getInstitutionUUID
    getUploadUrl(institutionUUID, getCourseVersionAssetUrl(institutionUUID, courseVersionUUID, fileName, path), getContentType(fileName))
  }

  def getCourseClassAssetUrl(institutionUUID: String, courseClassUUID: String, fileName: String, path: String): String = {
    mkurl(getRepositoryUrl(institutionUUID), mkurl(PREFIX, COURSE_CLASSES, courseClassUUID, path, fileName))
  }

  def getCourseClassUploadUrl(courseClassUUID: String, fileName: String, path: String): String = {
    val institutionUUID = CourseClassRepo(courseClassUUID).get.getInstitutionUUID
    getUploadUrl(institutionUUID, getCourseClassAssetUrl(institutionUUID, courseClassUUID, fileName, path), getContentType(fileName))
  }

  def getInstitutionUploadUrl(institutionUUID: String, fileName: String): String = {
    val path = mkurl(getRepositoryUrl(institutionUUID), PREFIX, INSTITUTION, fileName)
    getUploadUrl(institutionUUID, path, getContentType(fileName))
  }

  def getRepositoryUrl(institutionUUID: String): String = {
    val repo = getRepo(institutionUUID)
    mkurl("repository", repo.getUUID)
  }

  def getUploadUrl(institutionUUID: String, path: String, contentType: String): String = {
    ContentRepositoriesRepo
      .firstRepositoryByInstitution(institutionUUID)
      .map {
        case x if x.getRepositoryType == RepositoryType.S3 => getUploadUrlS3(institutionUUID, path, contentType)
        case x if x.getRepositoryType == RepositoryType.FS => getUploadUrlFS(institutionUUID, path)
        case _ => throw new IllegalStateException("Unknown repository type")
      }.getOrElse(throw new IllegalArgumentException(s"Could not find repository for institution [$institutionUUID]"))
  }

  def getUploadUrlFS(institutionUUID: String, path: String): String = {
    val institution = InstitutionRepo(institutionUUID).get
    val institutionBaseUrl = new URL(institution.getBaseURL)
    val baseUrl = institutionBaseUrl.getProtocol + "://" + institutionBaseUrl.getHost +
      (if (institutionBaseUrl.getPort != -1 && institutionBaseUrl.getPort != 80 && institutionBaseUrl.getPort != 443) ":" + institutionBaseUrl.getPort else "")
    val fullPath = StringUtils.mkurl(baseUrl, "upload", path)
    fullPath
  }

  def getUploadUrlS3(institutionUUID: String, path: String, contentType: String): String = {
    val repo = getRepo(institutionUUID)
    val presignedRequest = new GeneratePresignedUrlRequest(repo.getBucketName, path)
    presignedRequest.setMethod(HttpMethod.PUT)
    presignedRequest.setExpiration(new DateTime().plusMinutes(1).toDate)
    presignedRequest.setContentType(contentType)
    getAmazonS3Client(institutionUUID).generatePresignedUrl(presignedRequest).toString
  }

  def getAmazonS3Client(institutionUUID: String): AmazonS3Client = {
    val repo = getRepo(institutionUUID)
    val s3 = if (isSome(repo.getAccessKeyId))
      new AmazonS3Client(new BasicAWSCredentials(repo.getAccessKeyId, repo.getSecretAccessKey))
    else
      new AmazonS3Client

    s3
  }

  def getRepo(institutionUUID: String): ContentRepository = {
    val institution = InstitutionRepo(institutionUUID).get
    ContentRepositoriesRepo.firstRepository(institution.getAssetsRepositoryUUID).get
  }

  def getContentType(fileName: String, isAngularComponent: Boolean = false): String = {
    if (isAngularComponent) {
      getFileExtension(fileName) match {
        case "png" => "image/png"
        case "jpg" => "image/jpeg"
        case "jpeg" => "image/jpeg"
        case "mp4" => "video/mp4"
        case _ => "application/octet-stream"
      }
    } else {
      getFileExtension(fileName) match {
        case "png" => "image/png"
        case "jpg" => "image/jpg"
        case "jpeg" => "image/jpg"
        case "ico" => "image/x-icon"
        case "mp4" => "video/mp4"
        case _ => "application/octet-stream"
      }
    }
  }

  def getFileExtension(fileName: String): String = {
    val fileNameSplit = fileName.split('.')
    if (fileNameSplit.length > 1)
      fileNameSplit(1)
    else
      fileNameSplit(0)
  }

}
