package kornell.server.scorm12

import scala.xml.XML
import scala.collection.mutable.ListBuffer
import java.io.InputStream
import kornell.core.lom.Contents
import kornell.server.repository.LOM
import kornell.core.lom.Content

object ManifestParser {

  def parse(prefix: String, source: InputStream, visited:List[String]): Contents = {
    val result = ListBuffer[Content]()
    var index = 1
    val xmlContents = XML.load(source)
    val nodes = (xmlContents \\ "resource")
    nodes foreach { x => {
      val fileName = x.attribute("href").get.toString
      val page = LOM.newExternalPage(prefix, fileName, "" , fileName, index)
      page.setVisited(visited.contains(page.getKey()))
      val content = LOM.newContent(page)
      result += content
      index += 1
      }
    }
    val contents = result.toList
    LOM.newContents(contents)
  }
}