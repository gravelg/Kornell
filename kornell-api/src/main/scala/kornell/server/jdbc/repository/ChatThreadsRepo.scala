package kornell.server.jdbc.repository

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

class ChatThreadsRepo {
}

object ChatThreadsRepo {
  
  def getSupportChatThreadName(courseClassName: String) = "Ajuda para turma: " + courseClassName
  def getSupportChatThreadNameAdminPerspective(userFullName: String, courseClassName: String) = userFullName + " (turma: " + courseClassName + ")"

  def postMessageToCourseClassSupportThread(personUUID: String, courseClassUUID: String, message: String) = {
      val courseClass = CourseClassRepo(courseClassUUID).get
      val chatThreadUUID = getCourseClassSupportChatThreadUUID(personUUID, courseClass.getUUID)
      if(!chatThreadUUID.isDefined){
        val chatThread = createChatThread(courseClass.getInstitutionUUID)
        updateChatThreadParticipants(personUUID, chatThread.getUUID, courseClass)
        createCourseClassSupportChatThread(chatThread.getUUID, courseClass.getUUID, personUUID)
        createChatThreadMessage(chatThread.getUUID, personUUID, message)
      } else {
      	createChatThreadMessage(chatThreadUUID.get, personUUID, message)
        updateChatThreadParticipants(personUUID, chatThreadUUID.get, courseClass)
      }
      ""
  }

  def updateChatThreadParticipants(threadCreatorUUID: String, chatThreadUUID: String, courseClass: CourseClass) = {
    val participantsThatShouldExist = RolesRepo.getCourseClassThreadSupportParticipants(courseClass.getUUID, courseClass.getInstitutionUUID, null)
    		.getRoleTOs.asScala.map(_.getRole.getPersonUUID).union(List(threadCreatorUUID))
    val participants = getChatTreadParticipantsUUIDs(chatThreadUUID)
    val createThese = participantsThatShouldExist.filterNot(participants.contains)
    val removeThese = participants.filterNot(participantsThatShouldExist.contains)
    createThese.foreach(createChatThreadParticipant(chatThreadUUID, _, courseClass.getName, threadCreatorUUID))
    removeThese.foreach(removeChatThreadParticipant(chatThreadUUID, _))
  }

  def createChatThreadParticipant(chatThreadUUID: String, personUUID: String, courseClassName: String, threadCreatorUUID: String) = {
    val chatThreadName = {
      if(personUUID.equals(threadCreatorUUID))
      	getSupportChatThreadName(courseClassName)
      else
        getSupportChatThreadNameAdminPerspective(PeopleRepo.uuidLoader.load(threadCreatorUUID).get.getFullName(), courseClassName)
    }
    sql"""
		insert into ChatThreadParticipant (uuid, chatThreadUUID, personUUID, lastReadAt, chatThreadName)
		values (${UUID.random}, ${chatThreadUUID} , ${personUUID}, null, ${chatThreadName})""".executeUpdate
  }

  def removeChatThreadParticipant(chatThreadUUID: String, personUUID: String) = {
    sql"""
      delete from ChatThreadParticipant 
      where chatThreadUUID = ${chatThreadUUID}
    	and personUUID = ${personUUID}""".executeUpdate
  }

  def createChatThread(institutionUUID: String): ChatThread = {
      createChatThread(Entities.newChatThread(UUID.random, new Date(), institutionUUID))
  }

  def createChatThread(chatThread: ChatThread): ChatThread = {
    if (chatThread.getUUID == null)
      chatThread.setUUID(UUID.random)
    sql"""
		insert into ChatThread (uuid, createdAt, institutionUUID)
		values (${chatThread.getUUID}, ${chatThread.getCreatedAt}, ${chatThread.getInstitutionUUID})
	  """.executeUpdate
	  chatThread
  }
 
  def createCourseClassSupportChatThread(chatThreadUUID: String, courseClassUUID: String, personUUID: String) = {
    sql"""
		insert into CourseClassSupportChatThread (uuid, chatThreadUUID, courseClassUUID, personUUID)
		values (${UUID.random}, ${chatThreadUUID} , ${courseClassUUID}, ${personUUID})
	  """.executeUpdate
  }

  def createChatThreadMessage(chatThreadUUID: String, personUUID: String, message: String) = {
    sql"""
		insert into ChatThreadMessage (uuid, chatThreadUUID, sentAt, personUUID, message)
		values (${UUID.random}, ${chatThreadUUID} , ${new Date()}, ${personUUID}, ${message})
	  """.executeUpdate
  }
  
  def getCourseClassSupportChatThreadUUID(personUUID: String, courseClassUUID: String) = {
    sql"""
		    | select st.chatThreadUUID
	      	| from CourseClassSupportChatThread st
    			| where st.personUUID = ${personUUID}
    			| and st.courseClassUUID = ${courseClassUUID}
		    """.first[String]
  }
  
  def updateParticipantsInCourseClassSupportThreads(courseClassUUID: String) = {
    ???
  }
  
  def updateCourseClassSupportThreadsNames(courseClassUUID: String) = {
    ???
  }
  
  implicit def toString(rs: ResultSet): String = rs.getString(1)
      
  def getChatTreadParticipantsUUIDs(chatThreadUUID: String) = {
    sql"""
		    | select ctp.uuid
	      	| from ChatThreadParticipant ctp
    			| where ctp.chatThreadUUID = ${chatThreadUUID}
		    """.map[String]
  }
  
  def getTotalUnreadCountByPerson(personUUID: String, institutionUUID: String) = {
    sql"""
		    | select count(tm.uuid) as unreadMessages
					| from ChatThreadMessage tm
					| join (
						| select t.uuid as chatThreadUUID,
						| tp.lastReadAt as threadLastReadAt
						| from ChatThread t
						| join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID
						| left join CourseClassSupportChatThread ccs on ccs.chatThreadUUID = t.uuid
						| where tp.PersonUUID = ${personUUID}
						| and t.institutionUUID = ${institutionUUID}
					| ) countByCC on tm.chatThreadUUID = countByCC.chatThreadUUID
					| where (countByCC.threadLastReadAt < tm.sentAt or countByCC.threadLastReadAt is null)
					| and tm.personUUID <> ${personUUID}
		    """.first[String].get
  }
  
  def getTotalUnreadCountsByPersonPerThread(personUUID: String, institutionUUID: String) = {
    TOs.newUnreadChatThreadsTO(sql"""
				| select count(unreadMessages) as unreadMessages, chatThreadUUID, chatThreadName, courseClassUUID
				| from (
					| select tm.uuid as unreadMessages,
						| countByCC.chatThreadUUID,
						| countByCC.chatThreadName,
						| countByCC.courseClassUUID,
						| countByCC.threadLastReadAt,
						| tm.personUUID,
						| tm.sentAt
					| from ChatThread t
					| join (
						| select distinct t.uuid as chatThreadUUID,
						| tp.lastReadAt as threadLastReadAt,
						| ccs.courseClassUUID,
						| tp.chatThreadName as chatThreadName
						| from ChatThread t
						| join ChatThreadParticipant tp on t.uuid = tp.chatThreadUUID
						| left join CourseClassSupportChatThread ccs on ccs.chatThreadUUID = t.uuid
						| where tp.PersonUUID = ${personUUID}
						| and t.institutionUUID = ${institutionUUID}
					| ) countByCC on t.uuid = countByCC.chatThreadUUID
					| left join ChatThreadMessage tm on tm.chatThreadUUID = t.uuid and tm.personUUID <> ${personUUID}
    				| and (threadLastReadAt < sentAt or threadLastReadAt is null)
				| ) as threadMessages
				| group by chatThreadUUID
				| order by max(sentAt) desc
		    """.map[UnreadChatThreadTO](toUnreadChatThreadTO))
  }

  def getChatThreadMessages(chatThreadUUID: String) = {
    TOs.newChatThreadMessagesTO(sql"""
				| select p.fullName as senderFullName, tm.sentAt, tm.message from
				| 	ChatThreadMessage tm 
				| 	join Person p on p.uuid = tm.personUUID
				| 	where tm.chatThreadUUID = ${chatThreadUUID}
				| 	order by tm.sentAt
		    """.map[ChatThreadMessageTO](toChatThreadMessageTO))
  } 

  def getChatThreadMessagesSince(chatThreadUUID: String, lastFetchedMessageSentAt: String) = {
    TOs.newChatThreadMessagesTO(sql"""
				| select p.fullName as senderFullName, tm.sentAt, tm.message from
				| 	ChatThreadMessage tm 
				| 	join Person p on p.uuid = tm.personUUID
				| 	where tm.chatThreadUUID = ${chatThreadUUID}
    	  |   and tm.sentAt > ${lastFetchedMessageSentAt}
				| 	order by tm.sentAt
		    """.map[ChatThreadMessageTO](toChatThreadMessageTO))
  } 

  def markAsRead(chatThreadUUID: String, personUUID: String) = {
    sql"""
    	| update ChatThreadParticipant p set
			| 	lastReadAt = ${new Date()}
      | where p.chatThreadUUID = ${chatThreadUUID} 
      | and p.personUUID = ${personUUID}""".executeUpdate
  }
}