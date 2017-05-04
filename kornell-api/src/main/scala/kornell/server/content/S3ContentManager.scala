package kornell.server.content

import kornell.core.entity.ContentRepository
import com.amazonaws.services.s3.AmazonS3Client
import scala.util.Try
import java.util.logging.Logger
import kornell.core.util.StringUtils._
import scala.io.Source
import java.io.InputStream
import com.amazonaws.services.s3.model.ObjectMetadata
import scala.collection.JavaConverters._
import com.amazonaws.auth.BasicAWSCredentials

class S3ContentManager(repo: ContentRepository)
  extends SyncContentManager {
  
  val logger = Logger.getLogger(classOf[S3ContentManager].getName)

  lazy val s3 = if (isSome(repo.getAccessKeyId()))
    new AmazonS3Client(new BasicAWSCredentials(repo.getAccessKeyId(),repo.getSecretAccessKey()))
  else  
    new AmazonS3Client
  
  def source(keys: String*) =
    inputStream(keys:_*).map { Source.fromInputStream(_, "UTF-8") }
  
  def inputStream(keys: String*): Try[InputStream] = Try {
    val fqkn = url(keys:_*)
    logger.finest(s"loading key [ ${fqkn} ]")
    try {
      s3.getObject(repo.getBucketName, fqkn).getObjectContent
    } catch {
      case e: Throwable => {
        val cmd = s"aws s3api get-object --bucket ${repo.getBucketName} --key ${fqkn} --region ${repo.getRegion} file.out"
        logger.warning("Could not load object. Try [ " + cmd + " ]")
        throw e
      }
    }
  }
  
  def put(value: InputStream, contentType: String, contentDisposition: String, metadataMap: Map[String, String],keys: String*) = {
    val metadata = new ObjectMetadata()
    metadata.setUserMetadata(metadataMap asJava)
    Option(contentType).foreach { metadata.setContentType(_) }
    Option(contentDisposition).foreach { metadata.setContentDisposition(_) }
    s3.putObject(repo.getBucketName(), url(keys:_*), value, metadata)
  }
  
  def delete(keys: String*) = {
    logger.info("Trying to delete object [ " + url(keys:_*) + " ]")
    s3.deleteObject(repo.getBucketName(), url(keys:_*))
  }
  
  def getPrefix = repo.getPrefix

}