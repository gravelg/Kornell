package db.jdbcmigration

import java.io.File
import java.net.URL
import java.sql.Connection

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration
import kornell.core.util.StringUtils
import kornell.server.jdbc.ConnectionHandler
import kornell.server.jdbc.SQL._
import net.lingala.zip4j.core.ZipFile
import org.apache.commons.io.FileUtils

class V2017_03_07_23_11__AddRepoLocation extends JdbcMigration {
  override def migrate(conn: Connection): Unit = {
    migrateAddRepoLocation()
  }

  def migrateAddRepoLocation(): Unit = {
    val uuid = "hwrbf852-91d1-4373-b9f7-979ce0e907c3"
    val zipUrl = "https://github.com/Craftware/hello-world-uni/raw/master/hello-world-uni.zip"
    val repoPath = System.getenv("REPO_PATH")

    if (StringUtils.isSome(repoPath)) {
      val tempZipFile = File.createTempFile("repo", "zip")
      FileUtils.copyURLToFile(new URL(zipUrl), tempZipFile)

      val zipFile = new ZipFile(tempZipFile)
      zipFile.extractAll(repoPath)

      sql"""
  		  update ContentRepository set path = ${repoPath + "/hello-world"} where uuid = ${uuid}
  		""".executeUpdate
      ConnectionHandler.commit()
    }
  }

}