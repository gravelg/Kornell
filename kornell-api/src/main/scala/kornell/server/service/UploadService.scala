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

object UploadService {
  
  def getCourseVersionUploadUrl(courseVersionUUID: String) = {
    val courseVersion = CourseVersionRepo(courseVersionUUID).get
    val path = courseVersion.getDistributionPrefix + "upload" + new Date().getTime + ".zip";
    getUploadUrl(CourseRepo(courseVersion.getCourseUUID).get.getInstitutionUUID, path, "application/zip")
  }
  
  def getInstitutionUploadUrl(institutionUUID: String, filename: String) = {
    val contentType = filename.split('.')(1) match {
      case "png" => "image/png"
      case "jpg" => "image/jpg"
      case "ico" => "image/x-icon"
    }
    getUploadUrl(institutionUUID, filename, contentType)
  }
  
  def getUploadUrl(institutionUUID: String, path: String, contentType: String) = {
    val institution = InstitutionRepo(institutionUUID).get
    val repo = ContentRepositoriesRepo.firstRepository(institution.getAssetsRepositoryUUID).get
    
    val s3 = if (isSome(repo.getAccessKeyId()))
      new AmazonS3Client(new BasicAWSCredentials(repo.getAccessKeyId(),repo.getSecretAccessKey()))
    else  
      new AmazonS3Client
      
    val fullPath = "repository/" + repo.getUUID + "/" + path;
    val presignedRequest = new GeneratePresignedUrlRequest(repo.getBucketName, fullPath)
    presignedRequest.setMethod(HttpMethod.PUT)
    presignedRequest.setExpiration(new DateTime().plusMinutes(1).toDate)
    presignedRequest.setContentType(contentType)
    s3.generatePresignedUrl(presignedRequest).toString
  }
}