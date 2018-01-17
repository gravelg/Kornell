package kornell.server.content

import javax.ws.rs.Path
import javax.ws.rs.core.Response
import javax.ws.rs.PathParam
import kornell.core.util.StringUtils
import java.nio.file.Files
import javax.ws.rs.core.Context
import javax.servlet.http.HttpServletResponse
import javax.servlet.http.HttpServletRequest
import javax.ws.rs.OPTIONS
import javax.ws.rs.PUT
import java.nio.file.Paths
import kornell.server.jdbc.repository.ContentRepositoriesRepo
import java.nio.file.StandardCopyOption
import java.io.File
import java.io.InputStream
import java.util.zip.ZipInputStream
import java.io.ByteArrayOutputStream
import java.io.ByteArrayInputStream

@Path("/")
class UploaderResource {

  @Path("{path: .+}")
  @OPTIONS
  def options(): Response = Response.status(200).build()

  @Path("{path: .+}")
  @PUT
  def put(@Context resp: HttpServletResponse, @Context req: HttpServletRequest, @PathParam("path") path: String): Response = {
    val repoData = StringUtils.parseRepositoryData(path.replace("repository/", ""))
    val repositoryUUID = repoData.getRepositoryUUID()
    val cm = ContentManagers.forRepository(repositoryUUID);
    val repo = ContentRepositoriesRepo.getByRepositoryUUID(repositoryUUID).get
    val destinationPath = Paths.get(StringUtils.mkurl(repo.getPath, path))
    //create dirs if they do not exist
    destinationPath.getParent.toFile().mkdirs()

    val fileContent = req.getInputStream
    val contentType = req.getHeader("Content-Type")
    try {
      if (contentType == "application/zip") {
        unzip(destinationPath.getParent.toFile, fileContent)
      } else {
        Files.copy(fileContent, destinationPath, StandardCopyOption.REPLACE_EXISTING)
      }
      Response.status(200).build()
      //    } catch {
      //      case e: Exception => Response.status(500).entity(s"Key [${path}] not loaded").build()
    } finally {
      fileContent.close()
    }
  }

  def unzip(uploadDirectory: File, zipContent: InputStream) = {
    val zis = new ZipInputStream(zipContent)
    var entry = zis.getNextEntry()
    val buffer = new Array[Byte](1024)

    while (entry != null) {
      val fileName = entry.getName()
      val outputStream = new ByteArrayOutputStream()
      var len = zis.read(buffer)
      while (len > 0) {
        outputStream.write(buffer, 0, len)
        len = zis.read(buffer)
      }
      val is = new ByteArrayInputStream(outputStream.toByteArray())
      val path = Paths.get(uploadDirectory.getAbsolutePath, fileName)
      // sometimes zips contain directory entries, don't create because we create on file copy
      if (path.getFileName.toString.indexOf(".") != -1) {
        Files.createDirectories(path.getParent)
        Files.copy(is, path, StandardCopyOption.REPLACE_EXISTING)
      }

      is.close();
      outputStream.close();
      entry = zis.getNextEntry();
    }
    zis.closeEntry();
    zis.close();
  }

}
