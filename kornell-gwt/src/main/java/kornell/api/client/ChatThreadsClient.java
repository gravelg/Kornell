package kornell.api.client;

import java.util.Date;

import kornell.core.to.ChatThreadMessagesTO;
import kornell.core.to.UnreadChatThreadsTO;

public class ChatThreadsClient extends RESTClient {

	public void postMessageToSupportCourseClassThread(String message, String courseClassUUID, Callback<String> callback) {
		POST("/chatThreads/courseClass/" + courseClassUUID + "/support").sendRequest(message, callback);
	}

	public void postMessageToTutoringCourseClassThread(String message, String courseClassUUID, Callback<String> callback) {
		POST("/chatThreads/courseClass/" + courseClassUUID + "/tutoring").sendRequest(message, callback);
	}

	public void postMessageToSupportInstitutionThread(String message, Callback<String> callback) {
		POST("/chatThreads/institutionSupport").sendRequest(message, callback);
	}

	public void postMessageToSupportPlatformThread(String message, Callback<String> callback) {
		POST("/chatThreads/platformSupport").sendRequest(message, callback);
	}
	
	public void postMessageToDirectThread(String message, String personUUID, Callback<Void> callback) {
		POST("/chatThreads/direct/" + personUUID).sendRequest(message, callback);
	}
	
	public void postMessageToChatThread(String message, String chatThreadUUID, Callback<ChatThreadMessagesTO> callback) {
		postMessageToChatThread(message, chatThreadUUID, null, callback);
	}
	
	public void postMessageToChatThread(String message, String chatThreadUUID, Date since, Callback<ChatThreadMessagesTO> callback) {
		POST("/chatThreads/"+chatThreadUUID+"/message/?since="+((since != null) ? since.getTime() : "")).sendRequest(message, callback);
	}
	
	public void getTotalUnreadCount(Callback<String> callback) {
		GET("/chatThreads/unreadCount").sendRequest(null, callback);
	}
	
	public void getTotalUnreadCountsPerThread(Callback<UnreadChatThreadsTO> callback) {
		GET("/chatThreads/unreadCountPerThread").sendRequest(null, callback);
	}
	
	public void getChatThreadMessages(String chatThreadUUID, Callback<ChatThreadMessagesTO> callback) {
		getChatThreadMessages(chatThreadUUID, null, null, callback);
	}
	
	public void getChatThreadMessages(String chatThreadUUID, Date since, Date before, Callback<ChatThreadMessagesTO> callback) {
		GET("/chatThreads/"+chatThreadUUID+"/messages/?since="+((since != null) ? since.getTime() : "")+"&before="+((before != null) ? before.getTime() : "")).sendRequest(null, callback);
	}

}
