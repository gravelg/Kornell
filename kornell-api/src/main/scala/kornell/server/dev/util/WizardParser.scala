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

object WizardParser {

  val topicPattern = """#\s?(.*)""".r
  val buildNum = BUILD_NUM.getOpt.orElse("development_build").get

  def parse(classroomJson: String, visited: List[String]): Contents = {
    val json = JSON.parseFull(classroomJson)
    val map: Map[String, Any] = json.get.asInstanceOf[Map[String, Any]]
    parseLines(map, visited)
  }

  def parseLines(jsonMap: Map[String, Any], visited: List[String]) = {
    val result = ListBuffer[Content]()
    var topic: Topic = null
    var index = 1 //should start with 1, in case of the ordering fallback
    val time = (new Date).getTime

    val topics: List[Any] = jsonMap.get("modules").get.asInstanceOf[List[Any]]
    topics foreach { topicObj =>
      {
        val topicObjMap = topicObj.asInstanceOf[Map[String, Any]]
        topic = LOM.newTopic(topicObjMap.get("title").get.asInstanceOf[String])
        result += LOM.newContent(topic)

        val slides: List[Any] = topicObjMap.get("lectures").get.asInstanceOf[List[Any]]
        slides foreach { slideObj =>
          {
            val slideObjMap = slideObj.asInstanceOf[Map[String, Any]]

            val fileName = ""
            val title = slideObjMap.get("title").get.asInstanceOf[String]
            val uuid = slideObjMap.get("uuid").get.asInstanceOf[String]
            //TODO tcfaria KORNELL.PROPERTIES
            val path = "/angular/knlClassroom/index.html?cache-buster=" + buildNum + "#!/lecture?uuid=" + uuid

            val page = LOM.newExternalPage(path, fileName, title, uuid, index)
            page.setVisited(visited.contains(page.getKey()))
            val content = LOM.newContent(page)
            if (topic != null)
              topic.getChildren().add(content)
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

  def parseLines(prefix: String, lines: Iterator[String], visited: List[String]) = {
    val result = ListBuffer[Content]()
    var topic: Topic = null
    var index = 1 //should start with 1, in case of the ordering fallback
    lines foreach { line =>
      line match {
        case topicPattern(topicName) => {
          topic = LOM.newTopic(topicName)
          result += LOM.newContent(topic)
        }

        case _ => {
          val tokens = line.split(";")
          val fileName = Try { tokens(0) }.getOrElse("")
          val title = Try { tokens(1) }.getOrElse("")
          val actomKey = Try { tokens(2) }.getOrElse(fileName)

          val page = LOM.newExternalPage(prefix, fileName, title, actomKey, index)
          page.setVisited(visited.contains(page.getKey()))
          val content = LOM.newContent(page)
          if (topic != null)
            topic.getChildren().add(content)
          else
            result += content
          index += 1
        }

      }
    }
    val contents = result.toList
    LOM.newContents(contents)
  }
}
