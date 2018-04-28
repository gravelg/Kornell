package kornell.server.jdbc.repository

import java.sql.ResultSet
import kornell.server.repository.Entities
import kornell.core.entity.CourseVersion
import kornell.server.jdbc.SQL._
import kornell.core.to.CourseVersionTO
import kornell.core.to.CourseClassesTO
import kornell.server.repository.TOs
import kornell.core.entity.AuditedEntityType
import kornell.core.util.UUID
import kornell.server.service.AssetService
import kornell.core.util.StringUtils
import kornell.core.entity.CourseDetailsEntityType
import kornell.core.entity.EntityState
import kornell.core.error.exception.EntityConflictException
import java.util.Date
import kornell.server.content.ContentManagers
import kornell.server.service.S3Service
import kornell.server.service.SandboxService

class CourseVersionRepo(uuid: String) {

  val finder = sql"select * from CourseVersion where uuid = $uuid and state <> ${EntityState.deleted.toString}"

  def get = finder.get[CourseVersion]

  def first = finder.first[CourseVersion]

  def update(courseVersion: CourseVersion, skipAudit: Boolean, institutionUUID: String): CourseVersion = {
    //get previous version
    val oldCourseVersion = CourseVersionRepo(courseVersion.getUUID).first.get

    val courseVersionExists = sql"""
      select count(*) from CourseVersion
      where courseUUID = ${courseVersion.getCourseUUID}
      and (name = ${courseVersion.getName} or
        distributionPrefix = ${courseVersion.getDistributionPrefix})
      and uuid <> ${courseVersion.getUUID}
      and state <> ${EntityState.deleted.toString}
    """.first[String].get
    if (courseVersionExists == "0") {
      sql"""
      | update CourseVersion c
      | set c.name = ${courseVersion.getName},
      | c.courseUUID = ${courseVersion.getCourseUUID},
      | c.versionCreatedAt = ${courseVersion.getVersionCreatedAt},
      | c.distributionPrefix = ${courseVersion.getDistributionPrefix},
      | c.state = ${courseVersion.getState.toString},
      | c.disabled = ${courseVersion.isDisabled},
      | c.parentVersionUUID = ${courseVersion.getParentVersionUUID},
      | c.instanceCount = ${courseVersion.getInstanceCount},
      | c.classroomJson = ${courseVersion.getClassroomJson},
      | c.classroomJsonPublished = ${courseVersion.getClassroomJsonPublished},
      | c.label = ${courseVersion.getLabel},
      | c.thumbUrl = ${courseVersion.getThumbUrl}
      | where c.uuid = ${courseVersion.getUUID}""".executeUpdate

      //log entity change
      if (!skipAudit) {
        EventsRepo.logEntityChange(institutionUUID, AuditedEntityType.courseVersion, courseVersion.getUUID, oldCourseVersion, courseVersion)
      }
      courseVersion
    } else {
      throw new EntityConflictException("courseVersionAlreadyExists")
    }
  }

  def delete = {
    val courseVersion = get
    if (CourseClassesRepo.countByCourseVersion(uuid) == 0) {
      sql"""
        update CourseVersion
        set state = ${EntityState.deleted.toString},
        name = concat(name, " - ", uuid),
        distributionPrefix = concat(distributionPrefix, " - ", uuid)
        where uuid = ${uuid}
      """.executeUpdate

      sql"""
        update CourseClass
        set state = ${EntityState.deleted.toString}
        where sandbox = 1 and courseVersionUUID = ${uuid}
      """.executeUpdate

      val course = CourseRepo(courseVersion.getCourseUUID).get
      val repo = ContentRepositoriesRepo.firstRepositoryByInstitution(course.getInstitutionUUID).get
      val cm = ContentManagers.forRepository(repo.getUUID)

      cm.deleteFolder(S3Service.CLASSROOMS, course.getCode, courseVersion.getDistributionPrefix)

      courseVersion
    }
  }

  def copy = {
    val courseVersion = CourseVersionRepo(uuid).first.get
    val institutionUUID = CoursesRepo.byCourseVersionUUID(courseVersion.getUUID).get.getInstitutionUUID
    val sourceCourseVersionUUID = courseVersion.getUUID
    val targetCourseVersionUUID = UUID.random

    println(courseVersion.getThumbUrl)
    //copy courseVersion
    courseVersion.setUUID(targetCourseVersionUUID)
    courseVersion.setDistributionPrefix(targetCourseVersionUUID)
    courseVersion.setName(courseVersion.getName + " (2)")
    courseVersion.setVersionCreatedAt(new Date());
    if (StringUtils.isSome(courseVersion.getThumbUrl)) {
      courseVersion.setThumbUrl(courseVersion.getThumbUrl.replace(sourceCourseVersionUUID + "/thumb.jpg", targetCourseVersionUUID + "/thumb.jpg"))
    }
    println(courseVersion.getThumbUrl)
    CourseVersionsRepo.create(courseVersion, institutionUUID)

    AssetService.copyAssets(institutionUUID, CourseDetailsEntityType.COURSE_VERSION, sourceCourseVersionUUID, targetCourseVersionUUID, courseVersion.getThumbUrl)

    courseVersion
  }

  def getChildren(): List[CourseVersion] = {
    sql"""
      select * from CourseVersion where parentVersionUUID = ${uuid}"""
      .map[CourseVersion](toCourseVersion)
  }

}

object CourseVersionRepo {
  def apply(uuid: String) = new CourseVersionRepo(uuid: String)
}
