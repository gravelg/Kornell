package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.jdbc.SQL._
import scala.collection.JavaConverters._
import kornell.core.entity.CourseDetailsLibrary
import kornell.server.content.ContentManagers
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.core.util.StringUtils

class CourseDetailsLibraryRepo(uuid: String) {

  val finder = sql"select * from CourseDetailsLibrary where uuid=$uuid"

  def get = finder.get[CourseDetailsLibrary]
  def first = finder.first[CourseDetailsLibrary]

  def update(courseDetailsLibrary: CourseDetailsLibrary): CourseDetailsLibrary = {
    sql"""
    | update CourseDetailsLibrary l
    | set l.title = ${courseDetailsLibrary.getTitle},
    | l.index = ${courseDetailsLibrary.getIndex},
    | l.description = ${courseDetailsLibrary.getDescription},
    | l.size = ${courseDetailsLibrary.getSize},
    | l.path = ${courseDetailsLibrary.getPath},
    | l.uploadDate = ${courseDetailsLibrary.getUploadDate},
    | l.fontAwesomeClassName = ${courseDetailsLibrary.getFontAwesomeClassName}
    | where l.uuid = ${courseDetailsLibrary.getUUID}""".executeUpdate

    courseDetailsLibrary
  }

  def delete = {
    val courseDetailsLibrary = get
    sql"""
      delete from CourseDetailsLibrary
      where uuid = ${uuid}""".executeUpdate

    val courseDetailsLibraries = CourseDetailsLibrariesRepo.getForEntity(courseDetailsLibrary.getEntityUUID, courseDetailsLibrary.getEntityType).getCourseDetailsLibraries
    val indexed = courseDetailsLibraries.asScala.zipWithIndex
    for (i <- indexed) {
      val courseDetailsLibrary = i._1
      val index = i._2
      courseDetailsLibrary.setIndex(index)
      CourseDetailsLibraryRepo(courseDetailsLibrary.getUUID).update(courseDetailsLibrary)
    }

    val person = PersonRepo(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get).get
    val repo = ContentRepositoriesRepo.firstRepositoryByInstitution(person.getInstitutionUUID).get
    val cm = ContentManagers.forRepository(repo.getUUID)

    val url = StringUtils.mkurl(courseDetailsLibrary.getPath, courseDetailsLibrary.getTitle)
    cm.delete(url)

    courseDetailsLibrary
  }

}

object CourseDetailsLibraryRepo {
  def apply(uuid: String) = new CourseDetailsLibraryRepo(uuid)
}
