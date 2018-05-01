package kornell.server.api

import java.util.Date

import javax.ws.rs._
import javax.ws.rs.core.{Context, Response, SecurityContext}
import kornell.core.entity.{ChatThread, ChatThreadType}
import kornell.core.error.exception.UnauthorizedAccessException
import kornell.core.to.{ChatThreadMessagesTO, UnreadChatThreadsTO}
import kornell.core.util.StringUtils
import kornell.server.jdbc.repository.{AuthRepo, ChatThreadsRepo}

@Path("chatThreads")
@Produces(Array(ChatThread.TYPE))
class ChatThreadsResource {

  @POST
  @Path("courseClass/{courseClassUUID}/support")
  @Produces(Array("text/plain"))
  def postMessageToCourseClassSupportThread(implicit @Context sc: SecurityContext,
    @PathParam("courseClassUUID") courseClassUUID: String,
    message: String): String = AuthRepo().withPerson { person =>
    ChatThreadsRepo.postMessageToCourseClassThread(person.getUUID, courseClassUUID, message, ChatThreadType.SUPPORT)
    ChatThreadsRepo.getCourseClassChatThreadUUID(person.getUUID, courseClassUUID: String, ChatThreadType.SUPPORT).get
  }

  @POST
  @Path("courseClass/{courseClassUUID}/tutoring")
  @Produces(Array("text/plain"))
  def postMessageToCourseClassTutoringThread(implicit @Context sc: SecurityContext,
    @PathParam("courseClassUUID") courseClassUUID: String,
    message: String): String = AuthRepo().withPerson { person =>
    ChatThreadsRepo.postMessageToCourseClassThread(person.getUUID, courseClassUUID, message, ChatThreadType.TUTORING)
    ChatThreadsRepo.getCourseClassChatThreadUUID(person.getUUID, courseClassUUID: String, ChatThreadType.TUTORING).get
  }

  @POST
  @Path("institutionSupport")
  @Produces(Array("text/plain"))
  def postMessageToInstitutionSupportThread(implicit @Context sc: SecurityContext,
    message: String): String = AuthRepo().withPerson { person =>
    ChatThreadsRepo.postMessageToInstitutionThread(person.getUUID, person.getInstitutionUUID, message, ChatThreadType.INSTITUTION_SUPPORT)
    ChatThreadsRepo.getInstitutionChatThreadUUID(person.getUUID, person.getInstitutionUUID: String, ChatThreadType.INSTITUTION_SUPPORT).get
  }

  @POST
  @Path("platformSupport")
  @Produces(Array("text/plain"))
  def postMessageToPlatformSupportThread(implicit @Context sc: SecurityContext,
    message: String): String = AuthRepo().withPerson { person =>
    ChatThreadsRepo.postMessageToInstitutionThread(person.getUUID, person.getInstitutionUUID, message, ChatThreadType.PLATFORM_SUPPORT)
    ChatThreadsRepo.getInstitutionChatThreadUUID(person.getUUID, person.getInstitutionUUID: String, ChatThreadType.PLATFORM_SUPPORT).get
  }

  @POST
  @Path("{chatThreadUUID}/message")
  @Produces(Array(ChatThreadMessagesTO.TYPE))
  def postMessageToChatThread(implicit @Context sc: SecurityContext,
    @PathParam("chatThreadUUID") chatThreadUUID: String,
    message: String,
    @QueryParam("since") since: String): ChatThreadMessagesTO = AuthRepo().withPerson { person =>
    if (ChatThreadsRepo.isParticipant(chatThreadUUID, person.getUUID)) {
      ChatThreadsRepo.createChatThreadMessage(chatThreadUUID, person.getUUID, message)
      if (StringUtils.isSome(since))
        ChatThreadsRepo.getChatThreadMessagesSince(chatThreadUUID, new Date(since.toLong))
      else
        ChatThreadsRepo.getChatThreadMessagesBefore(chatThreadUUID, new Date)
    } else {
      throw new UnauthorizedAccessException("mustBeParticipant")
    }
  }

  @POST
  @Path("direct/{personUUID}")
  def postMessageToDirectThread(@PathParam("personUUID") targetPersonUUID: String, message: String): Response = {
    ChatThreadsRepo.postMessageToDirectThread(getAuthenticatedPersonUUID, targetPersonUUID, message)
    Response.noContent.build
  }

  @Path("unreadCount")
  @Produces(Array("text/plain"))
  @GET
  def getTotalUnreadCountByPerson(implicit @Context sc: SecurityContext): String = AuthRepo().withPerson { person =>
    ChatThreadsRepo.getTotalUnreadCountByPerson(person.getUUID, person.getInstitutionUUID)
  }

  @Path("unreadCountPerThread")
  @Produces(Array(UnreadChatThreadsTO.TYPE))
  @GET
  def getTotalUnreadCountsByPersonPerThread(implicit @Context sc: SecurityContext): UnreadChatThreadsTO = AuthRepo().withPerson { person =>
    ChatThreadsRepo.getTotalUnreadCountsByPersonPerThread(person.getUUID, person.getInstitutionUUID)
  }

  @Path("{chatThreadUUID}/messages")
  @Produces(Array(ChatThreadMessagesTO.TYPE))
  @GET
  def getChatThreadMessages(implicit @Context sc: SecurityContext,
    @PathParam("chatThreadUUID") chatThreadUUID: String,
    @QueryParam("since") since: String,
    @QueryParam("before") before: String): ChatThreadMessagesTO = AuthRepo().withPerson { person =>
    {
      if (ChatThreadsRepo.isParticipant(chatThreadUUID, person.getUUID)) {
        ChatThreadsRepo.markAsRead(chatThreadUUID, person.getUUID)
        if (StringUtils.isSome(since))
          ChatThreadsRepo.getChatThreadMessagesSince(chatThreadUUID, new Date(since.toLong))
        else if (StringUtils.isSome(before))
          ChatThreadsRepo.getChatThreadMessagesBefore(chatThreadUUID, new Date(before.toLong))
        else
          ChatThreadsRepo.getChatThreadMessagesBefore(chatThreadUUID, new Date)
      } else {
        throw new UnauthorizedAccessException("mustBeParticipant")
      }
    }
  }
}
