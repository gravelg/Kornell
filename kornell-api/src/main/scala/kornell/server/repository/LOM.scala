package kornell.server.repository

import java.util

import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.lom._
import kornell.core.util.StringUtils._

import scala.collection.JavaConverters._
import scala.util.Try

object LOM {
  val factory: LOMFactory = AutoBeanFactorySource.create(classOf[LOMFactory])

  def newTopic(name: String = ""): Topic = {
    val topic = factory.newTopic.as
    topic.setName(name)
    topic.setChildren(new util.ArrayList)
    topic
  }

  def newContent(topic: Topic): Content = {
    val content = factory.newContent.as
    content.setFormat(ContentFormat.Topic)
    content.setTopic(topic)
    content
  }

  def newExternalPage(
    prefix: String = "",
    fileName: String = "",
    title: String = "",
    actomKey: String = "",
    index: Int): ExternalPage = {

    val baseURL: String = ""
    val page = factory.newExternalPage.as
    page.setTitle(title)

    val idx = Try {
      fileName.split("\\.")(0).toInt
    }.getOrElse(index)
    page.setIndex(idx)
    page.setKey(actomKey)
    val pageURL = mkurl(baseURL, prefix, fileName)
    page.setURL(pageURL)
    page
  }

  def newContent(page: ExternalPage): Content = {
    val content = factory.newContent.as
    content.setFormat(ContentFormat.ExternalPage)
    content.setExternalPage(page)
    content
  }

  def newContents(children: List[Content] = List()): Contents = {
    val contents = factory.newContents.as
    contents.setChildren(children asJava)
    contents
  }
}
