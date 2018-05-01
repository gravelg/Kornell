package kornell.server.jdbc.repository

import java.util.concurrent.TimeUnit.MINUTES

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import kornell.core.entity.AuthClientType
import kornell.core.to.TokenTO
import kornell.server.jdbc.SQL._
import kornell.server.repository.TOs
import org.joda.time.DateTime

object TokenRepo {

  val TOKEN_CACHE_SIZE = 300

  val cacheBuilder: CacheBuilder[AnyRef, AnyRef] = CacheBuilder
    .newBuilder()
    .expireAfterAccess(15, MINUTES)
    .maximumSize(TOKEN_CACHE_SIZE)

  def apply() = new TokenRepo(newTokenCache)

  def newTokenCache: LoadingCache[TokenID, Option[TokenTO]] = cacheBuilder.build(tokenLoader)

  type TokenID = String
  type TokenCache = LoadingCache[TokenID, Option[TokenTO]]

  val tokenLoader: CacheLoader[TokenID, Option[TokenTO]] = new CacheLoader[TokenID, Option[TokenTO]]() {
    override def load(token: TokenID): Option[TokenTO] =
      lookupToken(token)
  }

  def clearCache(): Unit = newTokenCache.invalidateAll()

  def lookupToken(token: TokenID): Option[TokenTO] = {
    sql"""select * from Token where token = ${token}""".first[TokenTO]
  }

  def getToken(personUUID: String): Option[TokenTO] = {
    sql"""select * from Token where personUUID = ${personUUID}""".first[TokenTO]
  }

  def createToken(token: String, personUUID: String, authClientType: AuthClientType): TokenTO = {
    val expiry = {
      if (authClientType == AuthClientType.web) {
        new DateTime().plusDays(7).toDate
      } else {
        null
      }
    }
    sql"""insert into Token (token, personUUID, expiry, clientType) values
      	(${token}, ${personUUID}, ${expiry}, ${authClientType.toString})""".executeUpdate

    TOs.newTokenTO(token, expiry, personUUID, authClientType)
  }
}

class TokenRepo(tokenCache: TokenRepo.TokenCache) {
  def checkToken(tokenId: String): Option[TokenTO] = {
    if (tokenId != null && tokenId.length() > 0)
      tokenCache.get(tokenId)
    else
      None
  }

  def deleteToken(token: String): Unit = {
    sql"""delete from Token where token = ${token}""".executeUpdate
    tokenCache.invalidate(token)
  }
}
