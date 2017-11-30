package kornell.server.jdbc.repository

import scala.collection.JavaConverters.asScalaBufferConverter
import kornell.core.entity.CourseClass
import kornell.core.error.exception.EntityConflictException
import kornell.server.jdbc.SQL.SQLHelper
import kornell.server.jdbc.SQL.rsToString
import kornell.core.entity.AuditedEntityType
import kornell.core.entity.ChatThreadType
import kornell.server.authentication.ThreadLocalAuthenticator
import kornell.core.util.UUID
import kornell.server.service.AssetService
import kornell.core.util.StringUtils
import kornell.core.entity.CourseDetailsEntityType
import kornell.core.entity.EntityState
import kornell.core.entity.RoleType
import kornell.core.entity.RoleCategory
import java.util.Date

class CourseClassRepo(uuid: String) {

  val finder = sql"""select * from CourseClass where uuid = $uuid and state <> ${EntityState.deleted.toString}"""

  def get = finder.get[CourseClass]
  def first = finder.first[CourseClass]

  def version = CourseVersionRepo(get.getCourseVersionUUID)

  def institution = InstitutionRepo(get.getInstitutionUUID)

  def update(courseClass: CourseClass): CourseClass = {
    //get previous version
    val oldCourseClass = CourseClassRepo(courseClass.getUUID).first.get

    val courseClassExists = sql"""
      select count(*) from CourseClass
      where courseVersionUUID = ${courseClass.getCourseVersionUUID}
      and name = ${courseClass.getName}
      and uuid <> ${courseClass.getUUID}
      and state <> ${EntityState.deleted.toString}
    """.first[String].get
    if (courseClassExists == "0") {
      sql"""
        update CourseClass cc set
          cc.name = ${courseClass.getName},
          cc.institutionUUID = ${courseClass.getInstitutionUUID},
          cc.requiredScore = ${courseClass.getRequiredScore},
          cc.publicClass = ${courseClass.isPublicClass},
          cc.overrideEnrollments = ${courseClass.isOverrideEnrollments},
          cc.invisible = ${courseClass.isInvisible},
          cc.maxEnrollments = ${courseClass.getMaxEnrollments},
          cc.registrationType = ${courseClass.getRegistrationType.toString},
          cc.institutionRegistrationPrefixUUID = ${courseClass.getInstitutionRegistrationPrefixUUID},
          cc.courseClassChatEnabled = ${courseClass.isCourseClassChatEnabled},
          cc.chatDockEnabled = ${courseClass.isChatDockEnabled},
          cc.allowBatchCancellation = ${courseClass.isAllowBatchCancellation},
          cc.tutorChatEnabled = ${courseClass.isTutorChatEnabled},
          cc.approveEnrollmentsAutomatically = ${courseClass.isApproveEnrollmentsAutomatically},
          cc.thumbUrl = ${courseClass.getThumbUrl}
        where cc.uuid = ${courseClass.getUUID}""".executeUpdate

      //update course class threads active states per threadType and add participants to the global class chat, if applicable
      ChatThreadsRepo.updateCourseClassChatThreadStatusByThreadType(courseClass.getUUID, ChatThreadType.COURSE_CLASS, courseClass.isCourseClassChatEnabled)
      ChatThreadsRepo.updateCourseClassChatThreadStatusByThreadType(courseClass.getUUID, ChatThreadType.TUTORING, courseClass.isTutorChatEnabled)
      ChatThreadsRepo.addParticipantsToCourseClassThread(courseClass)

      //log entity change
      EventsRepo.logEntityChange(courseClass.getInstitutionUUID, AuditedEntityType.courseClass, courseClass.getUUID, oldCourseClass, courseClass)

      courseClass
    } else {
      throw new EntityConflictException("courseClassAlreadyExists")
    }
  }

  def delete = {
    val courseClass = get
    if (EnrollmentsRepo.countByCourseClass(uuid) == 0) {
      sql"""
        update CourseClass
        set state = ${EntityState.deleted.toString}
        where uuid = ${uuid}
      """.executeUpdate
    }
    courseClass
  }

  def copy = {
    val courseClass = CourseClassRepo(uuid).first.get
    val sourceCourseClassUUID = courseClass.getUUID
    val targetCourseClassUUID = UUID.random
    val ecommerceIdentifier = UUID.random.replace("-", "").substring(0, 20)

    //copy courseClass
    courseClass.setUUID(targetCourseClassUUID)
    courseClass.setName(courseClass.getName + " (2)")
    courseClass.setCreatedBy(ThreadLocalAuthenticator.getAuthenticatedPersonUUID.get)
    courseClass.setCreatedAt(new Date())
    courseClass.setEcommerceIdentifier(ecommerceIdentifier)
    if (StringUtils.isSome(courseClass.getThumbUrl)) {
      courseClass.setThumbUrl(courseClass.getThumbUrl.replace(sourceCourseClassUUID + "/thumb.jpg", targetCourseClassUUID + "/thumb.jpg"))
    }
    CourseClassesRepo.create(courseClass)

    //copy roles
    val roles = RolesRepo.getAllUsersWithRoleForCourseClass(sourceCourseClassUUID)
    roles.getRoleTOs.asScala.foreach(roleTO => {
      val role = roleTO.getRole
      role.setUUID(UUID.random)
      RoleCategory.setCourseClassUUID(role, targetCourseClassUUID)
      RolesRepo.create(role)
    })

    AssetService.copyAssets(courseClass.getInstitutionUUID, CourseDetailsEntityType.COURSE_CLASS, sourceCourseClassUUID, targetCourseClassUUID, courseClass.getThumbUrl)

    courseClass
  }

  def actomsVisitedBy(personUUID: String): List[String] = sql"""
    select actomKey from ActomEntered ae
    join Enrollment e on ae.enrollmentUUID=e.uuid
    where e.courseClassUUID = ${uuid}
    and personUUID = ${personUUID}
    order by eventFiredAt
    """.map[String]({ rs => rs.getString("actomKey") })
}

object CourseClassRepo extends App {
  def apply(uuid: String) = new CourseClassRepo(uuid)
}
