package kornell.server.content

import kornell.server.jdbc.SQL._
import com.amazonaws.services.s3.AmazonS3Client
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import kornell.server.util.Settings
import kornell.server.repository.Entities
import kornell.core.entity.ContentRepository
import kornell.server.util.Settings._
import kornell.core.util.StringUtils.mkurl
import kornell.core.entity.RepositoryType

object ContentManagers {
  
  lazy val s3 = new AmazonS3Client

  def forRepository(repoUUID: String): SyncContentManager = 
    ContentRepositoriesRepo
    	.getByRepositoryUUID(repoUUID)
    	.map { _ match {
    	  case x if x.getRepositoryType == RepositoryType.S3 => new S3ContentManager(x)
    	  case x if x.getRepositoryType == RepositoryType.FS => new FSContentManager(x)
    	  case _ => throw new IllegalStateException("Unknow repository type")
    	}
  	}.getOrElse(throw new IllegalArgumentException(s"Could not find repository [$repoUUID]"))
  
  
  lazy val USER_CONTENT_URL = mkurl("https://s3.amazonaws.com", USER_CONTENT_BUCKET, "") 
  
  def certsRepo(institutionUUID:String) = 
    Entities.newContentRepository(null, RepositoryType.S3, USER_CONTENT_ACCESS_KEY.getOpt.getOrElse(null), USER_CONTENT_SECRET_ACCESS_KEY.getOpt.getOrElse(null), USER_CONTENT_BUCKET, "usercontent/certificates", USER_CONTENT_REGION, institutionUUID, null)
  
  def forCertificates(institutionUUID:String): SyncContentManager = new S3ContentManager(certsRepo(institutionUUID))
  
}