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

object UploadService {
  
  val PREFIX = "knl-institution"
  
  def getCourseVersionContentUploadUrl(courseVersionUUID: String) = {
    val courseVersion = CourseVersionRepo(courseVersionUUID).get
    val path = courseVersion.getDistributionPrefix + "upload" + new Date().getTime + ".zip";
    getUploadUrl(CourseRepo(courseVersion.getCourseUUID).get.getInstitutionUUID, path, "application/zip")
  }
  
  def getContentType(fileName: String) = {
    fileName.split('.')(1) match {
      case "png" => "image/png"
      case "jpg" => "image/jpg"
      case "ico" => "image/x-icon"
    }
  }
  
  def getCourseUploadUrl(courseUUID: String, fileName: String) = {
    val destinationPath = mkurl(PREFIX, "courses", courseUUID, fileName)
    getUploadUrl(CourseRepo(courseUUID).get.getInstitutionUUID, destinationPath, getContentType(fileName))
  }
  
  def getCourseVersionUploadUrl(courseVersionUUID: String, fileName: String) = {
    val destinationPath = mkurl(PREFIX, "courseVersions", courseVersionUUID, fileName)
    getUploadUrl(CoursesRepo.byCourseVersionUUID(courseVersionUUID).get.getInstitutionUUID, destinationPath, getContentType(fileName))
  }
  
  def getCourseClassUploadUrl(courseClassUUID: String, fileName: String) = {
    val destinationPath = mkurl(PREFIX, "courseClasses", courseClassUUID, fileName)
    getUploadUrl(CourseClassRepo(courseClassUUID).get.getInstitutionUUID, destinationPath, getContentType(fileName))
  }
  
  def getInstitutionUploadUrl(institutionUUID: String, fileName: String) = {
    getUploadUrl(institutionUUID, fileName, getContentType(fileName))
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