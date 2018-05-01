package kornell.server.jdbc.repository

import java.util.concurrent.TimeUnit.MINUTES
import java.util.logging.{Level, Logger}

import com.google.common.cache.{CacheBuilder, CacheLoader, LoadingCache}
import kornell.core.entity.{EmailTemplate, EmailTemplateType}
import kornell.core.error.exception.EntityNotFoundException
import kornell.server.jdbc.SQL._
import kornell.server.util.Settings

object EmailTemplatesRepo {

  val logger: Logger = Logger.getLogger("kornell.server.jdbc.repository.EmailTemplatesRepo")

  val cacheBuilder: CacheBuilder[AnyRef, AnyRef] = CacheBuilder
    .newBuilder()
    .expireAfterAccess(5, MINUTES)
    .maximumSize(1000)

  type EmailTemplateLocale = String
  type EmailTemplateCacheEntry = (EmailTemplateType, EmailTemplateLocale)

  val templateLoader: CacheLoader[EmailTemplateCacheEntry, Option[EmailTemplate]] = new CacheLoader[EmailTemplateCacheEntry, Option[EmailTemplate]]() {
    override def load(cacheEntry: EmailTemplateCacheEntry): Option[EmailTemplate] = lookupByTemplateTypeAndLocale(cacheEntry)
  }

  val templateCache: LoadingCache[(EmailTemplateType, EmailTemplateLocale), Option[EmailTemplate]] = cacheBuilder.build(templateLoader)

  def getTemplate(templateType: EmailTemplateType, locale: String): Option[EmailTemplate] = {
    val template = templateCache.get((templateType, locale))
    if (template.isEmpty) {
      val fallbackTemplate = templateCache.get((templateType, Settings.DEFAULT_LOCALE))
      if (fallbackTemplate.isEmpty) {
        logger.log(Level.SEVERE, "Cannot find template " + templateType.toString + " for locale " + locale + " or default locale")
        throw new EntityNotFoundException("missingTemplate")
      } else {
        fallbackTemplate
      }
    } else {
      template
    }
  }

  def lookupByTemplateTypeAndLocale(cacheEntry: EmailTemplateCacheEntry): Option[EmailTemplate] =
    sql"select * from EmailTemplate where templateType = ${cacheEntry._1.toString} and locale = ${cacheEntry._2}".first[EmailTemplate]
}