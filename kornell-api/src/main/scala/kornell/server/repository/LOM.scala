package kornell.server.repository

import kornell.core.lom.LOMFactory
import com.google.web.bindery.autobean.vm.AutoBeanFactorySource
import kornell.core.lom.ExternalPage
import kornell.core.lom.Topic
import kornell.core.lom.Content
import kornell.core.lom.ContentFormat
import java.util.ArrayList
import kornell.core.util.StringUtils._
import scala.collection.JavaConverters._
import scala.util.Try

object LOM {
  val factory = AutoBeanFactorySource.create(classOf[LOMFactory])

  def newTopic(name: String = "") = {
    val topic = factory.newTopic.as
    topic.setName(name)
    topic.setChildren(new ArrayList)
    topic
  }

  def newContent(topic: Topic) = {
    val content = factory.newContent.as
    content.setFormat(ContentFormat.Topic);
    content.setTopic(topic)
    content
  }

  def newExternalPage(
    prefix: String = "",
    fileName: String = "",
    title: String = "",
    actomKey: String = "",
    index: Int) = {

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

  def newContent(page: ExternalPage) = {
    val content = factory.newContent.as
    content.setFormat(ContentFormat.ExternalPage);
    content.setExternalPage(page)
    content
  }

  def newContents(children: List[Content] = List()) = {
    val contents = factory.newContents.as
    contents.setChildren(children asJava)
    contents
  }

}
