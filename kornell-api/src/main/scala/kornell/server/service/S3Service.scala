package kornell.server.service

import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.amazonaws.HttpMethod
import org.joda.time.DateTime
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.auth.BasicAWSCredentials
import kornell.core.util.StringUtils._
import kornell.server.jdbc.repository.CourseVersionRepo
import kornell.server.jdbc.repository.CourseClassRepo
import kornell.server.jdbc.repository.CourseRepo
import kornell.server.jdbc.repository.InstitutionRepo
import kornell.server.content.ContentManagers
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import java.util.Date
import kornell.server.jdbc.repository.PersonRepo
import kornell.server.jdbc.repository.CoursesRepo

object S3Service {
  
  def PREFIX = "knl"
  def INSTITUTION = "institution"
  def COURSES = "courses"
  def COURSE_VERSIONS = "courseVersions"
  def COURSE_CLASSES = "courseClasses"
  def CERTIFICATES = "certificates"
  def CLASSROOM = "classroom"
  
  def getCourseVersionContentUploadUrl(courseVersionUUID: String) = {
    val courseVersion = CourseVersionRepo(courseVersionUUID).get
    val course = CourseRepo(courseVersion.getCourseUUID).get
    val fullPath = mkurl(CLASSROOM, course.getCode, courseVersion.getDistributionPrefix, "upload" + new Date().getTime + ".zip");
    getUploadUrl(CourseRepo(courseVersion.getCourseUUID).get.getInstitutionUUID, fullPath, "application/zip")
  }
  
  def getContentType(fileName: String) = {
    fileName.split('.')(1) match {
      case "png"  => "image/png"
      case "jpg"  => "image/jpg"
      case "jpeg" => "image/jpg"
      case "ico"  => "image/x-icon"
      case _      => "application/octet-stream"
    }
  }
  
  def getCourseUploadUrl(courseUUID: String, fileName: String, path: String) = {
    val fullPath = mkurl(PREFIX, COURSES, courseUUID, path, fileName)
    getUploadUrl(CourseRepo(courseUUID).get.getInstitutionUUID, fullPath, getContentType(fileName))
  }
  
  def getCourseVersionUploadUrl(courseVersionUUID: String, fileName: String, path: String) = {
    val fullPath = mkurl(PREFIX, COURSE_VERSIONS, courseVersionUUID, path, fileName)
    getUploadUrl(CoursesRepo.byCourseVersionUUID(courseVersionUUID).get.getInstitutionUUID, fullPath, getContentType(fileName))
  }
  
  def getCourseClassUploadUrl(courseClassUUID: String, fileName: String, path: String) = {
    val fullPath = mkurl(PREFIX, COURSE_CLASSES, courseClassUUID, path, fileName)
    getUploadUrl(CourseClassRepo(courseClassUUID).get.getInstitutionUUID, fullPath, getContentType(fileName))
  }
  
  def getInstitutionUploadUrl(institutionUUID: String, fileName: String) = {
    val path = mkurl(PREFIX, INSTITUTION, fileName)
    getUploadUrl(institutionUUID, path, getContentType(fileName))
  }
  
  def getUploadUrl(institutionUUID: String, path: String, contentType: String) = {
    val institution = InstitutionRepo(institutionUUID).get
    val repo = ContentRepositoriesRepo.firstRepository(institution.getAssetsRepositoryUUID).get
    
    val s3 = if (isSome(repo.getAccessKeyId()))
      new AmazonS3Client(new BasicAWSCredentials(repo.getAccessKeyId(),repo.getSecretAccessKey()))
    else  
      new AmazonS3Client
      
    val fullPath = mkurl("repository", repo.getUUID, path);
    val presignedRequest = new GeneratePresignedUrlRequest(repo.getBucketName, fullPath)
    presignedRequest.setMethod(HttpMethod.PUT)
    presignedRequest.setExpiration(new DateTime().plusMinutes(1).toDate)
    presignedRequest.setContentType(contentType)
    s3.generatePresignedUrl(presignedRequest).toString
  }
}