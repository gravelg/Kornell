package kornell.server.dev.util

import java.util.Date

import scala.collection.mutable.ListBuffer
import scala.util.Try
import scala.util.parsing.json.JSON
import kornell.core.lom.Content
import kornell.core.lom.Contents
import kornell.core.lom.Topic
import kornell.server.repository.LOM
import kornell.server.util.Settings.BUILD_NUM
import kornell.server.util.Settings.toOption

import scala.util.matching.Regex

object WizardParser {

  val topicPattern: Regex = """#\s?(.*)""".r
  val buildNum: String = BUILD_NUM.getOpt.orElse("development_build").get

  def parse(classroomJson: String, visited: List[String]): Contents = {
    val json = JSON.parseFull(classroomJson)
    val map: Map[String, Any] = json.get.asInstanceOf[Map[String, Any]]
    parseLines(map, visited)
  }

  def parseLines(jsonMap: Map[String, Any], visited: List[String]): Contents = {
    val result = ListBuffer[Content]()
    var topic: Topic = null
    var index = 1 //should start with 1, in case of the ordering fallback

    val topics: List[Any] = jsonMap("modules").asInstanceOf[List[Any]]
    topics foreach { topicObj =>
      {
        val topicObjMap = topicObj.asInstanceOf[Map[String, Any]]
        topic = LOM.newTopic(topicObjMap("title").asInstanceOf[String])
        result += LOM.newContent(topic)

        val slides: List[Any] = topicObjMap("lectures").asInstanceOf[List[Any]]
        slides foreach { slideObj =>
          {
            val slideObjMap = slideObj.asInstanceOf[Map[String, Any]]

            val fileName = ""
            val title = slideObjMap("title").asInstanceOf[String]
            val uuid = slideObjMap("uuid").asInstanceOf[String]
            //TODO tcfaria KORNELL.PROPERTIES
            val path = "/angular/knlClassroom/index.html?cache-buster=" + buildNum + "#!/lecture?uuid=" + uuid

            val page = LOM.newExternalPage(path, fileName, title, uuid, index)
            page.setVisited(visited.contains(page.getKey))
            val content = LOM.newContent(page)
            if (topic != null)
              topic.getChildren.add(content)
            else
              result += content
            index += 1
          }
        }
      }
    }
    val contents = result.toList
    LOM.newContents(contents)
  }

  def parseLines(prefix: String, lines: Iterator[String], visited: List[String]): Contents = {
    val result = ListBuffer[Content]()
    var topic: Topic = null
    var index = 1 //should start with 1, in case of the ordering fallback
    lines foreach {
      case topicPattern(topicName) => {
        topic = LOM.newTopic(topicName)
        result += LOM.newContent(topic)
      }

      case line => {
        val tokens = line.split(";")
        val fileName = Try {
          tokens(0)
        }.getOrElse("")
        val title = Try {
          tokens(1)
        }.getOrElse("")
        val actomKey = Try {
          tokens(2)
        }.getOrElse(fileName)

        val page = LOM.newExternalPage(prefix, fileName, title, actomKey, index)
        page.setVisited(visited.contains(page.getKey))
        val content = LOM.newContent(page)
        if (topic != null)
          topic.getChildren.add(content)
        else
          result += content
        index += 1
      }

    }
    val contents = result.toList
    LOM.newContents(contents)
  }
}
