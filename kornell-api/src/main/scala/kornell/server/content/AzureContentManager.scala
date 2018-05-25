package kornell.server.content

import java.io.InputStream
import java.util.logging.Logger

import com.amazonaws.services.s3.model.{DeleteObjectsRequest, ObjectMetadata}
import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.blob.CloudBlobClient
import kornell.core.entity.ContentRepository
import kornell.core.util.StringUtils._

import scala.collection.JavaConverters._
import scala.io.Source
import scala.util.Try

class AzureContentManager(repo: ContentRepository)
    extends SyncContentManager {

  val logger: Logger = Logger.getLogger(classOf[AzureContentManager].getName)

  lazy val blobClient: CloudBlobClient = if (isSome(repo.getAccountName)) {
    val storageConnectionString =
      "DefaultEndpointsProtocol=http;"
    + "AccountName=" + repo.getAccountName + ";"
    + "AccountKey=" + repo.getAccountKey
    CloudStorageAccount.parse(storageConnectionString).createCloudBlobClient()
  } else {
    throw new Exception("invalidConfiguration")
  }

  def source(keys: String*) =
    inputStream(keys: _*).map { Source.fromInputStream(_, "UTF-8") }

  def inputStream(keys: String*): Try[InputStream] = Try {
    val fqkn = url(keys: _*)
    logger.finest(s"loading key [ $fqkn ]")
    try {
      val container = blobClient.getContainerReference(repo.getContainer)
      container.getBlobReferenceFromServer(fqkn).openInputStream
    } catch {
      case e: Throwable => {
        val cmd = s"aws s3api get-object --bucket ${repo.getBucketName} --key $fqkn --region ${repo.getRegion} file.out"
        logger.warning("Could not load object. Try [ " + cmd + " ]")
        throw e
      }
    }
  }

  def put(value: InputStream, contentLength: Int, contentType: String, contentDisposition: String, metadataMap: Map[String, String], keys: String*): Unit = {
    val container = blobClient.getContainerReference(repo.getContainer)
    val blob = container.getBlockBlobReference(url(keys: _*))
    blob.getProperties.setContentDisposition(contentDisposition)
    blob.getProperties.setContentType(contentType)
    blob.upload(value, contentLength)
  }

  def delete(keys: String*): Unit = {
    // keys we support delete for already have repo prefix appended
    logger.info("Trying to delete object [ " + mkurl("", keys: _*) + " ]")
    val container = blobClient.getContainerReference(repo.getContainer)
    val blob = container.getBlockBlobReference(url(keys: _*))
    blob.delete()
  }

  def deleteFolder(keys: String*): Unit = {
    throw new Exception("notImplemented")
  }

  def getPrefix: String = repo.getPrefix

}
