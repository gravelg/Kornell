package kornell.server.dev.util

import scala.collection.mutable.ListBuffer
import kornell.core.lom.Topic
import kornell.core.lom.Content
import kornell.server.repository.Entities
import kornell.core.lom.Contents
import scala.io.Source
import scala.io.BufferedSource
import kornell.core.util.StringUtils._
import kornell.server.repository.LOM
import kornell.core.to.LibraryFileTO
import kornell.server.repository.TOs
import kornell.core.to.LibraryFilesTO
import kornell.core.util.StringUtils

object LibraryFilesParser {

  def parse(filesURL: String, source: String): LibraryFilesTO =
    parseLines(filesURL, source.lines)

  def parseLine(lineArray: Array[String], filesURL: String) = {
    val libraryFileTO = TOs.newLibraryFileTO
    //#fullFileName#fileType#fileNameDisplay#fileDescription#fileSize#publishingDate
    libraryFileTO.setURL(StringUtils.composeURL(filesURL, lineArray(0)))
    libraryFileTO.setFileType(lineArray(1))
    libraryFileTO.setFileName(lineArray(2))
    libraryFileTO.setFileDescription(lineArray(3))
    libraryFileTO.setFileSize(lineArray(4))
    libraryFileTO.setPublishingDate(lineArray(5))
    libraryFileTO
  }

  def parseLines(filesURL: String, lines: Iterator[String]) = {
    val result = ListBuffer[LibraryFileTO]()
    lines foreach { line =>
      val lineArray = line.split(";")
      if (lineArray.length > 1)
        result += parseLine(lineArray, filesURL)
    }
    val libraryFiles = result.toList
    TOs.newLibraryFilesTO(libraryFiles)
  }
}
