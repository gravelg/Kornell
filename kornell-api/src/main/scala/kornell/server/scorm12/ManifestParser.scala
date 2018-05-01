package kornell.server.scorm12

import java.io.InputStream

import kornell.core.lom.{Content, Contents}
import kornell.server.repository.LOM

import scala.collection.mutable.ListBuffer
import scala.xml.XML

object ManifestParser {

  def parse(prefix: String, source: InputStream, visited: List[String]): Contents = {
    val result = ListBuffer[Content]()
    var index = 1
    val xmlContents = XML.load(source)

    //build resource map
    val resourceMap = (xmlContents \\ "resource").map(x => x.attribute("identifier").get.text -> x.attribute("href").get.text).toMap

    //topics don't have a 'identifierref' attribute
    val topicsNodes = xmlContents \\ "organization" \ "item"
    val topLevelRefs = topicsNodes.map(x => (x \ "@identifierref").text).filter(_.nonEmpty)
    if (topLevelRefs.nonEmpty) {
      //one level
      val topic = LOM.newTopic((xmlContents \\ "organization" \ "title").text)
      result += LOM.newContent(topic)
      val titles = topicsNodes.map(x => x.attribute("identifierref").get.text -> (x \ "title").text)
      titles foreach { x =>
        {
          val identifier = x._1
          val title = x._2
          val fileName = resourceMap(identifier)
          val page = LOM.newExternalPage(prefix, fileName, title, fileName, index)
          page.setVisited(visited.contains(page.getKey))
          val content = LOM.newContent(page)
          topic.getChildren.add(content)
          index += 1
        }
      }
    } else {
      //multi-level
      topicsNodes foreach {
        x =>
          {
            val topicNode = x \ "item"
            val topic = LOM.newTopic((x \ "title").text)
            result += LOM.newContent(topic)

            topicNode foreach {
              x =>
                {
                  val identRef = x.attribute("identifierref").get.text
                  val title = (x \ "title").text
                  val fileName = resourceMap(identRef)
                  val page = LOM.newExternalPage(prefix, fileName, title, fileName, index)
                  page.setVisited(visited.contains(page.getKey))
                  val content = LOM.newContent(page)
                  topic.getChildren.add(content)
                  index += 1
                }
            }
          }
      }
    }

    val contents = result.toList
    LOM.newContents(contents)
  }
}