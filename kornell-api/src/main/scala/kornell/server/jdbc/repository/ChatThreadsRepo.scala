package kornell.server.jdbc.repository

import scala.collection.JavaConverters.setAsJavaSetConverter
import scala.collection.JavaConverters.asScalaBufferConverter
import scala.collection.JavaConverters.bufferAsJavaListConverter
import kornell.server.jdbc.SQL.SQLHelper
import kornell.core.entity.ChatThread
import kornell.core.util.UUID
import java.util.Date
import kornell.server.repository.Entities
import java.sql.ResultSet
import kornell.core.to.UnreadChatThreadsTO
import kornell.server.repository.TOs
import kornell.core.to.UnreadChatThreadTO
import kornell.core.to.ChatThreadMessageTO
import kornell.core.entity.CourseClass
import java.util.concurrent.TimeUnit._
import kornell.core.entity.Person
import kornell.core.to.ChatThreadMessagesTO
import kornell.core.entity.role.RoleCategory
import kornell.core.entity.ChatThreadType
import kornell.core.to.EnrollmentTO
import kornell.core.entity.ChatThreadParticipant
import java.text.SimpleDateFormat
import kornell.core.entity.Enrollment
import kornell.server.util.RequirementNotMet
import kornell.core.entity.role.RoleType
import kornell.core.to.RolesTO
import kornell.core.to.RoleTO
import scala.collection.immutable.List
import kornell.server.util.EmailService
import kornell.server.api.InstitutionResource
import kornell.core.entity.EntityState

object ChatThreadsRepo {

  def postMessageToCourseClassThread(personUUID: String, courseClassUUID: String, message: String, threadType: ChatThreadType) = {
    val courseClass = CourseClassRepo(courseClassUUID).get
    val chatThreadUUID = getCourseClassChatThreadUUID(personUUID, courseClass.getUUID, threadType)
    if (!chatThreadUUID.isDefined) {
      val chatThread = createChatThread(courseClass.getInstitutionUUID, courseClass.getUUID, personUUID, threadType)
      updateChatThreadParticipants(chatThread.getUUID, personUUID, courseClass, threadType, courseClass.getInstitutionUUID)
      createChatThreadMessage(chatThread.getUUID, personUUID, message)
      sendEmailForThreadCreation(courseClass, chatThread, threadType, message, courseClass.getInstitutionUUID)
    } else {
      createChatThreadMessage(chatThreadUUID.get, personUUID, message)
    }
  }

  def postMessageToInstitutionThread(personUUID: String, institutionUUID: String, message: String, threadType: ChatThreadType) = {
    val chatThreadUUID = getInstitutionChatThreadUUID(personUUID, institutionUUID, threadType)
    if (!chatThreadUUID.isDefined) {
      val chatThread = createChatThread(institutionUUID, null, personUUID, threadType)
      updateChatThreadParticipants(chatThread.getUUID, personUUID, null, threadType, institutionUUID)
      createChatThreadMessage(chatThread.getUUID, personUUID, message)
      sendEmailForThreadCreation(null, chatThread, threadType, message, institutionUUID)
    } else {
      createChatThreadMessage(chatThreadUUID.get, personUUID, message)
    }
  }

  def postMessageToDirectThread(fromPersonUUID: String, toPersonUUID: String, message: String) = {
    val fromPerson = new PersonRepo(fromPersonUUID).get
    val toPerson = new PersonRepo(toPersonUUID).get
    if (!fromPerson.getInstitutionUUID.equals(toPerson.getInstitutionUUID)) {
      throw new IllegalStateException("Cannot send message to person in another institution")
    }
    //the message may be coming from any of the participants, got to check both sides
    val chatThreadUUID = {
      val tempThreadUUID = getDirectChatThreadUUID(fromPersonUUID, toPersonUUID)
      if (tempThreadUUID.isDefined) {
        tempThreadUUID
      } else {
        null
      }
    }
    if (chatThreadUUID == null) {
      val chatThread = createChatThread(fromPerson.getInstitutionUUID, null, fromPersonUUID, ChatThreadType.DIRECT)
      createChatThreadParticipant(chatThread.getUUID, fromPersonUUID)
      createChatThreadParticipant(chatThread.getUUID, toPersonUUID)
      createChatThreadMessage(chatThread.getUUID, fromPersonUUID, message)
    } else {
      createChatThreadMessage(chatThreadUUID.get, fromPersonUUID, message)
    }
  }

  def sendEmailForThreadCreation(courseClass: CourseClass, chatThread: ChatThread, threadType: ChatThreadType, message: String, institutionUUID: String) = {
    val institution = new InstitutionRepo(chatThread.getInstitutionUUID).get
    threadType match {
      case ChatThreadType.SUPPORT => RolesRepo.getCourseClassSupportThreadParticipants(courseClass.getUUID, courseClass.getInstitutionUUID, RoleCategory.BIND_WITH_PERSON)
        .getRoleTOs.asScala.filter(role => !chatThread.getPersonUUID.equals(role.getPerson.getUUID)).map(role => EmailService.sendEmailNewChatThread(role.getPerson, institution, courseClass, chatThread, message))
      case ChatThreadType.TUTORING => RolesRepo.getUsersForCourseClassByRole(courseClass.getUUID, RoleType.tutor, RoleCategory.BIND_WITH_PERSON)
        .getRoleTOs.asScala.filter(role => !chatThread.getPersonUUID.equals(role.getPerson.getUUID)).map(role => EmailService.sendEmailNewChatThread(role.getPerson, institution, courseClass, chatThread, message))
      case ChatThreadType.INSTITUTION_SUPPORT => RolesRepo.getPlatformSupportThreadParticipants(institutionUUID, RoleCategory.BIND_WITH_PERSON)
        .getRoleTOs.asScala.filter(role => !chatThread.getPersonUUID.equals(role.getPerson.getUUID)).map(role => EmailService.sendEmailNewChatThread(role.getPerson, institution, courseClass, chatThread, message))
      case ChatThreadType.PLATFORM_SUPPORT => RolesRepo.getPlatformSupportThreadParticipants(institutionUUID, RoleCategory.BIND_WITH_PERSON)
        .getRoleTOs.asScala.filter(role => !chatThread.getPersonUUID.equals(role.getPerson.getUUID)).map(role => EmailService.sendEmailNewChatThread(role.getPerson, institution, courseClass, chatThread, message))
      case ChatThreadType.COURSE_CLASS | ChatThreadType.DIRECT => throw new IllegalStateException("not-supported-for-this-type")
    }
  }

  def updateChatThreadParticipants(chatThreadUUID: String, threadCreatorUUID: String, courseClass: CourseClass, threadType: ChatThreadType, institutionUUID: String) = {
    val participantsThatShouldExist = threadType match {
      case ChatThreadType.SUPPORT => RolesRepo.getCourseClassSupportThreadParticipants(courseClass.getUUID, courseClass.getInstitutionUUID, null)
        .getRoleTOs.asScala.map(_.getRole.getPersonUUID).+:(threadCreatorUUID).toList.distinct
      case ChatThreadType.TUTORING => RolesRepo.getUsersForCourseClassByRole(courseClass.getUUID, RoleType.tutor, RoleCategory.BIND_WITH_PERSON)
        .getRoleTOs.asScala.map(_.getRole.getPersonUUID).+:(threadCreatorUUID).toList.distinct
      case ChatThreadType.INSTITUTION_SUPPORT => RolesRepo.getPlatformSupportThreadParticipants(institutionUUID, null)
        .getRoleTOs.asScala.map(_.getRole.getPersonUUID).+:(threadCreatorUUID).toList.distinct
      case ChatThreadType.PLATFORM_SUPPORT => RolesRepo.getPlatformSupportThreadParticipants(institutionUUID, null)
        .getRoleTOs.asScala.map(_.getRole.getPersonUUID).+:(threadCreatorUUID).toList.distinct
      case ChatThreadType.COURSE_CLASS | ChatThreadType.DIRECT => throw new IllegalStateException("not-supported-for-this-type")
    }

    val participants = getChatTreadParticipantsUUIDs(chatThreadUUID).distinct
    val createThese = participantsThatShouldExist.filterNot(participants.contains)
    val removeThese = participants.filterNot(participantsThatShouldExist.contains)
    createThese.foreach(createChatThreadParticipant(chatThreadUUID, _))
    removeThese.foreach(removeChatThreadParticipant(chatThreadUUID, _))
  }

  def getChatTreadParticipantsUUIDs(chatThreadUUID: String) = sql"""
        | select p.uuid
          | from ChatThreadParticipant ctp
          | join Person p on p.uuid = ctp.personUUID
          | where ctp.chatThreadUUID = ${chatThreadUUID}
        """.map[String]

  def getChatTreadParticipant(personUUID: String, chatThreadUUID: String) = sql"""
            | select *
            | from ChatThreadParticipant ctp
            | where ctp.chatThreadUUID = ${chatThreadUUID}
        | and ctp.personUUID = ${personUUID}
            """.first[ChatThreadParticipant](toChatThreadParticipant)

  def updateChatThreadParticipantActivation(chatThreadParticipantUUID: String, active: Boolean) = {
    if (active) {
      sql"""update ChatThreadParticipant set active = ${active}, lastJoinDate = now() where uuid = ${chatThreadParticipantUUID}""".executeUpdate
    } else {
      sql"""update ChatThreadParticipant set active = ${active} where uuid = ${chatThreadParticipantUUID}""".executeUpdate
    }

  }

  def createChatThreadParticipant(chatThreadUUID: String, personUUID: String) = {
    sql"""
    insert into ChatThreadParticipant (uuid, chatThreadUUID, personUUID, lastReadAt, active, lastJoinDate)
    values (${UUID.random}, ${chatThreadUUID} , ${personUUID}, now(), 1, now())""".executeUpdate
  }

  def removeChatThreadParticipant(chatThreadUUID: String, personUUID: String) = {
    sql"""
      delete from ChatThreadParticipant
      where chatThreadUUID = ${chatThreadUUID}
      and personUUID = ${personUUID}""".executeUpdate
  }

  def createChatThread(institutionUUID: String, courseClassUUID: String, personUUID: String, threadType: ChatThreadType): ChatThread = {
    createChatThread(Entities.newChatThread(UUID.random, new Date(), institutionUUID, courseClassUUID, personUUID, threadType.toString))
  }

  def createChatThread(chatThread: ChatThread): ChatThread = {
    if (chatThread.getUUID == null)
      chatThread.setUUID(UUID.random)
    sql"""
    insert into ChatThread (uuid, createdAt, institutionUUID, courseClassUUID, personUUID, threadType, active)
    values (${chatThread.getUUID}, ${chatThread.getCreatedAt}, ${chatThread.getInstitutionUUID}, ${chatThread.getCourseClassUUID}, ${chatThread.getPersonUUID}, ${chatThread.getThreadType}, 1)
    """.executeUpdate
    chatThread
  }

  def isParticipant(chatThreadUUID: String, personUUID: String) =
    sql"""select count(*) as participantExists from ChatThreadParticipant where chatThreadUUID = ${chatThreadUUID} and personUUID = ${personUUID}"""
      .first[Integer] { rs => rs.getInt("participantExists") }.get >= 1

  def createChatThreadMessage(chatThreadUUID: String, personUUID: String, message: String) = {
    sql"""
    insert into ChatThreadMessage (uuid, chatThreadUUID, sentAt, personUUID, message)
    values (${UUID.random}, ${chatThreadUUID} , now(), ${personUUID}, ${message})
    """.executeUpdate

    sql"""
      update ChatThread set lastSentAt = now() where uuid = ${chatThreadUUID}
    """.executeUpdate

    sql"""
      | update ChatThreadParticipant set
      | unreadCount = unreadCount+1
      | where chatThreadUUID = ${chatThreadUUID}
      | and personUUID <> ${personUUID}
    """.executeUpdate
  }

  def getCourseClassChatThreadUUID(personUUID: String, courseClassUUID: String, threadType: ChatThreadType) = {
    sql"""
        | select uuid
          | from ChatThread
          | where ( personUUID = ${personUUID} or ${personUUID} is null )
          | and ( courseClassUUID = ${courseClassUUID} or ${courseClassUUID} is null )
          | and threadType = ${threadType.toString}
        """.first[String]
  }

  def getInstitutionChatThreadUUID(personUUID: String, institutionUUID: String, threadType: ChatThreadType) = {
    sql"""
        | select uuid
          | from ChatThread
          | where ( personUUID = ${personUUID} or ${personUUID} is null )
          | and ( institutionUUID = ${institutionUUID} or ${institutionUUID} is null )
          | and threadType = ${threadType.toString}
        """.first[String]
  }

  def getDirectChatThreadUUID(fromPersonUUID: String, toPersonUUID: String) = {
    sql"""
        | select t.uuid
        | from ChatThread t
        | where ( select count(uuid) from ChatThreadParticipant ctp where personUUID = ${fromPersonUUID}
          | and t.uuid = ctp.chatThreadUUID) = 1
        | and ( select count(uuid) from ChatThreadParticipant ctp where personUUID = ${toPersonUUID}
          | and t.uuid = ctp.chatThreadUUID) = 1
        | and t.threadType = ${ChatThreadType.DIRECT.toString}
        """.first[String]
  }

  def updateParticipantsInCourseClassSupportThreadsForInstitution(institutionUUID: String, threadType: ChatThreadType) = {
    sql"""select uuid from CourseClass where institutionUUID = ${institutionUUID} and state <> ${EntityState.deleted.toString()}""".map[String](toString)
      .foreach(cc => updateParticipantsInThreads(cc, institutionUUID, threadType))
  }

  def updateParticipantsInThreads(courseClassUUID: String, institutionUUID: String, threadType: ChatThreadType) = {
    type CourseClassSupportThreadData = Tuple2[String, String]
    implicit def courseClassSupportThreadConvertion(rs: ResultSet): CourseClassSupportThreadData = (rs.getString(1), rs.getString(2))

    if (!ChatThreadType.PLATFORM_SUPPORT.equals(threadType)) {
      val courseClass = CourseClassRepo(courseClassUUID).get
      sql"""
      select uuid, personUUID from ChatThread
        where courseClassUUID = ${courseClassUUID}
          and threadType = ${threadType.toString}
        """.map[CourseClassSupportThreadData](courseClassSupportThreadConvertion)
        .foreach(ct => updateChatThreadParticipants(ct._1, ct._2, courseClass, threadType, courseClass.getInstitutionUUID))
    } else {
      sql"""
      select uuid, personUUID from ChatThread
        where institutionUUID = ${institutionUUID}
          and threadType = ${threadType.toString}
        """.map[CourseClassSupportThreadData](courseClassSupportThreadConvertion)
        .foreach(ct => updateChatThreadParticipants(ct._1, ct._2, null, threadType, institutionUUID))
    }
  }

  def getTotalUnreadCountByPerson(personUUID: String, institutionUUID: String) = {
    sql"""
        select sum(unreadCount) as unreadMessages from ChatThreadParticipant tp where tp.personUUID = ${personUUID}
        """.first[String].get
  }

  def getTotalUnreadCountsByPersonPerThread(personUUID: String, institutionUUID: String) = {
    TOs.newUnreadChatThreadsTO(sql"""
       | select pt.unreadCount as unreadMessages, pt.chatThreadUUID, t.threadType, p.fullName as creatorName, t.lastSentAt,
       |   (case t.threadType when ${ChatThreadType.DIRECT.toString} then
       |     (select pin.fullName
       |       from ChatThreadParticipant ctpin
       |         join Person pin on ctpin.personUUID = pin.uuid
       |       where ctpin.personUUID <> ${personUUID}
       |         and t.uuid = ctpin.chatThreadUUID
       |     ) else cc.name end) as entityName,
       |    (case t.threadType when ${ChatThreadType.DIRECT.toString} then
       |     (select cpin2.personUUID
       |       from ChatThreadParticipant cpin2
       |       where cpin2.personUUID <> ${personUUID}
       |         and t.uuid = chatThreadUUID
       |     ) else  cc.uuid end) as entityUUID
       | from ChatThreadParticipant pt
       | join ChatThread t on t.uuid = pt.chatThreadUUID
       | join Person p on pt.personUUID = p.uuid
       | left join CourseClass cc on t.courseClassUUID = cc.uuid
       | where pt.personUUID = ${personUUID}
       | order by t.lastSentAt desc
        """.map[UnreadChatThreadTO](toUnreadChatThreadTO))
  }

  def getChatThreadMessagesSince(chatThreadUUID: String, lastFetchedMessageSentAt: Date) = {
    TOs.newChatThreadMessagesTO(sql"""
      select
        p.fullName as senderFullName,
        (select role
        from Role r
        where r.personUUID = p.uuid and
        (r.institutionUUID = t.institutionUUID or r.courseClassUUID = t.courseClassUUID) and
        (r.role <> ${RoleType.tutor.toString} or t.threadType = ${ChatThreadType.TUTORING.toString})
        order by
          case
            when (r.role = ${RoleType.tutor.toString} and
              (t.threadType = ${ChatThreadType.TUTORING.toString} or
               t.threadType = ${ChatThreadType.COURSE_CLASS.toString})) then 1
            when r.role = ${RoleType.platformAdmin.toString} then 2
            when r.role = ${RoleType.institutionAdmin.toString}  then 3
            when r.role = ${RoleType.courseClassAdmin.toString}  then 4
            else 5
            end
        limit 1
        ) as senderRole,
        tm.sentAt,
          tm.message
      from ChatThreadMessage tm
      join ChatThread t on t.uuid = tm.chatThreadUUID
      join Person p on p.uuid = tm.personUUID
        where tm.chatThreadUUID = ${chatThreadUUID}
          and tm.sentAt > ${lastFetchedMessageSentAt}
        order by tm.sentAt
      """.map[ChatThreadMessageTO](toChatThreadMessageTO))
  }

  def getChatThreadMessagesBefore(chatThreadUUID: String, firstFetchedMessageSentAt: Date) = {
    val date = if ("none".equals(firstFetchedMessageSentAt)) {
      new Date
    } else {
      firstFetchedMessageSentAt
    }
    TOs.newChatThreadMessagesTO(sql"""
      select
        p.fullName as senderFullName,
        (select role
        from Role r
        where r.personUUID = p.uuid and
        (r.institutionUUID = t.institutionUUID or r.courseClassUUID = t.courseClassUUID) and
        (r.role <> ${RoleType.tutor.toString} or t.threadType = ${ChatThreadType.TUTORING.toString})
        order by
          case
            when (r.role = ${RoleType.tutor.toString} and t.threadType = ${ChatThreadType.TUTORING.toString}) then 1
            when r.role = ${RoleType.platformAdmin.toString} then 2
            when r.role = ${RoleType.institutionAdmin.toString}  then 3
            when r.role = ${RoleType.courseClassAdmin.toString}  then 4
            else 5
            end
        limit 1
        ) as senderRole,
        tm.sentAt,
          tm.message
      from ChatThreadMessage tm
      join ChatThread t on t.uuid = tm.chatThreadUUID
      join Person p on p.uuid = tm.personUUID
       where tm.chatThreadUUID = ${chatThreadUUID}
          and tm.sentAt < ${date}
       order by tm.sentAt desc limit 20
      """.map[ChatThreadMessageTO](toChatThreadMessageTO))
  }

  def markAsRead(chatThreadUUID: String, personUUID: String) = {
    sql"""
      | update ChatThreadParticipant p set
      | lastReadAt = ${new Date()},
      | unreadCount = 0
      | where p.chatThreadUUID = ${chatThreadUUID}
      | and p.personUUID = ${personUUID}""".executeUpdate
  }

  def getCourseClassGlobalChatThread(courseClassUUID: String) = {
    sql"""select * from ChatThread where courseClassUUID = ${courseClassUUID} and threadType =  ${ChatThreadType.COURSE_CLASS.toString}""".first[ChatThread](toChatThread)
  }

  def createCourseClassGlobalChatThread(institutionUUID: String, courseClassUUID: String) = {
    sql"""insert into ChatThread (uuid, institutionUUID, courseClassUUID, threadType, createdAt, active) values (
    ${UUID.random}, ${institutionUUID}, ${courseClassUUID}, ${ChatThreadType.COURSE_CLASS.toString}, ${new Date}, 1)
    """.executeUpdate
  }

  def updateChatThreadParticipantStatus(chatThreadParticipantUUID: String, status: Boolean) = {
    sql"""update ChatThreadParticipant set active = ${status} where uuid = ${chatThreadParticipantUUID}""".executeUpdate
  }

  def updateCourseClassChatThreadStatusByThreadType(courseClassUUID: String, chatThreadType: ChatThreadType, status: Boolean) = {
    sql"""update ChatThread set active = ${status} where courseClassUUID = ${courseClassUUID} and threadType = ${chatThreadType.toString}""".executeUpdate
  }

  /**
   * Call this when enrolling a user
   */
  def addParticipantToCourseClassThread(enrollment: Enrollment) {
    val chatThread = getCourseClassGlobalChatThread(enrollment.getCourseClassUUID)
    if (chatThread.isDefined) {
      val participant = getChatTreadParticipant(enrollment.getPersonUUID, chatThread.get.getUUID)
      if (!participant.isDefined) {
        val courseClass = CourseClassRepo(enrollment.getCourseClassUUID).get
        createChatThreadParticipant(chatThread.get.getUUID, enrollment.getPersonUUID)
      } else {
        updateChatThreadParticipantStatus(participant.get.getUUID, true)
      }
    }
  }

  /**
   * Call this when un-enrolling a user
   */
  def disableParticipantFromCourseClassThread(enrollment: Enrollment) = {
    val chatThread = getCourseClassGlobalChatThread(enrollment.getCourseClassUUID)
    if (chatThread.isDefined) {
      val participant = getChatTreadParticipant(enrollment.getPersonUUID, chatThread.get.getUUID)
      if (participant.isDefined) {
        updateChatThreadParticipantStatus(participant.get.getUUID, false)
      }
    }
  }

  /**
   * Call this when updating the course class properties
   */
  def addParticipantsToCourseClassThread(courseClass: CourseClass) = {
    if (courseClass.isCourseClassChatEnabled) {
      val threadUUID = (getCourseClassGlobalChatThread(courseClass.getUUID).
        getOrElse(createChatThread(courseClass.getInstitutionUUID, courseClass.getUUID, null, ChatThreadType.COURSE_CLASS))).getUUID
      EnrollmentsRepo.byCourseClass(courseClass.getUUID).getEnrollmentTOs().asScala.foreach(enrollment => {
        val participant = getChatTreadParticipant(enrollment.getPersonUUID, threadUUID)
        if (!participant.isDefined) {
          createChatThreadParticipant(threadUUID, enrollment.getPersonUUID)
        }
      })
    }
  }

  implicit def toString(rs: ResultSet): String = rs.getString(1)

}
