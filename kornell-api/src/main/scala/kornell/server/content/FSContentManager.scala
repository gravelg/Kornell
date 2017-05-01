package kornell.server.content

import kornell.core.entity.ContentRepository
import scala.util.Try
import scala.io.Source
import java.nio.file.Paths
import java.io.FileInputStream
import java.io.InputStream
import kornell.core.util.StringUtils
import java.nio.file.Files

class FSContentManager(fsRepo:ContentRepository) extends SyncContentManager {
	 def source(keys: String*): Try[Source] = Try {
	   val path = Paths.get(fsRepo.getPath,url(keys:_*))
	   Source.fromFile(path.toFile(), "UTF-8")
	 }
	 
	 def inputStream(keys: String*): Try[InputStream] = Try {
	   val path = Paths.get(fsRepo.getPath, url(keys:_*))
	   val file = path.toFile()
		 if(file.exists()) new FileInputStream(file) 
		 else throw new IllegalArgumentException(s"$path not found")
	 }

	 def put(input: InputStream, contentType: String, contentDisposition: String, metadataMap: Map[String, String], keys: String*) = {
	   val fullFilePath = (if (fsRepo.getPath.endsWith("/")) fsRepo.getPath else fsRepo.getPath + "/") + url(keys:_*)
	   val directory = Paths.get(fullFilePath).toFile.getParentFile
	   if (!directory.exists) directory.mkdirs
		 Files.copy(input, Paths.get(fullFilePath))
	 }
  
  def delete(keys: String*) = {
	   val fullFilePath = (if (fsRepo.getPath.endsWith("/")) fsRepo.getPath else fsRepo.getPath + "/") + url(keys:_*)
	   val file = Paths.get(fullFilePath).toFile
	   if(file.exists) Files.delete(Paths.get(fullFilePath))
  }
	 
	 def getPrefix = fsRepo.getPrefix
}