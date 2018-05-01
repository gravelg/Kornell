package kornell.server.jdbc.repository

import java.util.concurrent.TimeUnit.MINUTES

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import kornell.core.entity.ContentRepository
import kornell.core.util.UUID
import kornell.server.jdbc.SQL._

object ContentRepositoriesRepo {

  def createRepo(repo: ContentRepository): ContentRepository = {
    if (repo.getUUID == null) {
      repo.setUUID(UUID.random)
    }
    sql"""insert into ContentRepository (uuid,repositoryType,prefix,institutionUUID,accessKeyId,secretAccessKey,bucketName,prefix,region,path) values (
      ${repo.getUUID},
      ${repo.getRepositoryType.toString},
      ${repo.getPrefix},
      ${repo.getInstitutionUUID},
      ${repo.getAccessKeyId},
      ${repo.getSecretAccessKey},
      ${repo.getBucketName},
      ${repo.getPrefix},
      ${repo.getRegion},
      ${repo.getPath})""".executeUpdate
    repo
  }

  def updateRepo(repo: ContentRepository): ContentRepository = {
    sql"""
        | update ContentRepository set
        | repositoryType = ${repo.getRepositoryType.toString},
        | accessKeyId = ${repo.getAccessKeyId},
        | secretAccessKey = ${repo.getSecretAccessKey},
        | bucketName = ${repo.getBucketName},
        | prefix = ${repo.getPrefix},
        | region = ${repo.getRegion},
        | path = ${repo.getPath}
        | where uuid = ${repo.getUUID}""".executeUpdate
    repo
  }

  def firstRepository(repoUUID: String): Option[ContentRepository] = sql"""
    select * from ContentRepository where uuid=$repoUUID
  """.first[ContentRepository]

  def firstRepositoryByInstitution(institutionUUID: String): Option[ContentRepository] = sql"""
    select * from ContentRepository where institutionUUID=${institutionUUID}
  """.first[ContentRepository]

  val cacheBuilder: CacheBuilder[AnyRef, AnyRef] = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, MINUTES)
    .maximumSize(20)

  val contentRepositoryLoader : CacheLoader[String, Option[ContentRepository]]= new CacheLoader[String, Option[ContentRepository]]() {
    override def load(repositoryUUID: String): Option[ContentRepository] = firstRepository(repositoryUUID)
  }
  val contentRepositoryCache: LoadingCache[String, Option[ContentRepository]] = cacheBuilder.build(contentRepositoryLoader)

  def getByRepositoryUUID(repositoryUUID: String): Option[ContentRepository] = contentRepositoryCache.get(repositoryUUID)

  def updateCache(repo: ContentRepository): Unit = {
    val optionRepo = Some(repo)
    contentRepositoryCache.put(repo.getUUID, optionRepo)
  }

  def clearCache(): Unit = contentRepositoryCache.invalidateAll()
}
