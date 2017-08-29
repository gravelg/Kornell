package kornell.server.jdbc.repository

import com.google.common.cache.CacheBuilder
import java.util.concurrent.TimeUnit.MINUTES
import com.google.common.cache.CacheLoader
import kornell.core.entity.EmailTemplate
import kornell.server.jdbc.SQL._
import kornell.core.entity.EmailTemplateType
import kornell.server.util.Settings
import kornell.core.error.exception.EntityNotFoundException
import java.util.logging.Logger
import java.util.logging.Level

object EmailTemplatesRepo {

  val logger = Logger.getLogger("kornell.server.jdbc.repository.EmailTemplatesRepo")

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

  def getTemplate(templateType: EmailTemplateType, locale: String) = {
    val template = templateCache.get((templateType, locale))
    if (template.isEmpty) {
      val fallbackTemplate = templateCache.get((templateType, Settings.DEFAULT_LOCALE))
      if (fallbackTemplate.isEmpty) {
        logger.log(Level.SEVERE, "Cannot find template " + templateType.toString() + " for locale " + locale + " or default locale")
        throw new EntityNotFoundException("missingTemplate")
      }
      fallbackTemplate
    }
    template
  }

  def lookupByTemplateTypeAndLocale(cacheEntry: EmailTemplateCacheEntry) = 
	  sql"select * from EmailTemplate where templateType = ${cacheEntry._1.toString} and locale = ${cacheEntry._2}".first[EmailTemplate]
}