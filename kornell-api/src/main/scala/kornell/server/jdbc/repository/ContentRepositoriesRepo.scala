package kornell.server.jdbc.repository

import kornell.server.jdbc.SQL._
import kornell.server.repository.Entities
import kornell.server.repository.ContentRepository
import kornell.core.entity.ContentRepository
import java.sql.ResultSet
import kornell.core.util.UUID
import com.google.common.cache.CacheLoader
import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit.MINUTES

object ContentRepositoriesRepo {

  def createRepo(repo: ContentRepository): ContentRepository = {
    if (repo.getUUID == null) {
      repo.setUUID(UUID.random)
    }
    sql"""insert into ContentRepository (uuid,repositoryType,prefix,institutionUUID,accessKeyId,secretAccessKey,bucketName,prefix,region,path,accountName,accountKey,container) values (
      ${repo.getUUID},
      ${repo.getRepositoryType.toString},
      ${repo.getPrefix},
      ${repo.getInstitutionUUID},
      ${repo.getAccessKeyId},
      ${repo.getSecretAccessKey},
      ${repo.getBucketName},
      ${repo.getPrefix},
      ${repo.getRegion},
      ${repo.getPath},
      ${repo.getAccountName},
      ${repo.getAccountKey},
      ${repo.getContainer})""".executeUpdate
    repo
  }

  def firstRepository(repoUUID: String) = sql"""
    select * from ContentRepository where uuid=$repoUUID
  """.first[ContentRepository]

  def firstRepositoryByInstitution(institutionUUID: String) = sql"""
    select * from ContentRepository where institutionUUID=${institutionUUID}
  """.first[ContentRepository]

  val cacheBuilder = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, MINUTES)
    .maximumSize(20)

  val contentRepositoryLoader = new CacheLoader[String, Option[ContentRepository]]() {
    override def load(repositoryUUID: String): Option[ContentRepository] = firstRepository(repositoryUUID)
  }
  val contentRepositoryCache = cacheBuilder.build(contentRepositoryLoader)

  def getByRepositoryUUID(repositoryUUID: String) = contentRepositoryCache.get(repositoryUUID)

  def updateCache(repo: ContentRepository) = {
    val optionRepo = Some(repo)
    contentRepositoryCache.put(repo.getUUID, optionRepo)
  }

  def clearCache() = contentRepositoryCache.invalidateAll()
}
