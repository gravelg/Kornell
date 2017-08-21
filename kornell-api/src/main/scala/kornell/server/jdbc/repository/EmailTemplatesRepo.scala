package kornell.server.jdbc.repository

import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit.MINUTES
import com.google.common.cache.CacheLoader
import kornell.core.entity.EmailTemplate
import kornell.server.jdbc.SQL._
import kornell.core.entity.EmailTemplateType

object EmailTemplatesRepo {

  val cacheBuilder = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, MINUTES)
    .maximumSize(1000)

  type EmailTemplateLocale = String
  type EmailTemplateCacheEntry = (EmailTemplateType, EmailTemplateLocale)

  val templateLoader = new CacheLoader[EmailTemplateCacheEntry, Option[EmailTemplate]]() {
    override def load(cacheEntry: EmailTemplateCacheEntry): Option[EmailTemplate] = lookupByTemplateTypeAndLocale(cacheEntry)
  }
  val templateCache = cacheBuilder.build(templateLoader)

  def getTemplate(templateType: EmailTemplateType, locale: String) = templateCache.get((templateType, locale))

  def lookupByTemplateTypeAndLocale(cacheEntry: EmailTemplateCacheEntry) = 
	  sql"select * from EmailTemplate where templateType = ${cacheEntry._1.toString} and locale = ${cacheEntry._2}".first[EmailTemplate]
}