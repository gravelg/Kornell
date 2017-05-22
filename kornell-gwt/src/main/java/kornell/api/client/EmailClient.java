package kornell.api.client;


public class EmailClient extends RESTClient {

	public void sendWelcomeEmail(String userUUID, Callback<String> cb) {
		GET("/email/welcome/" + userUUID).sendRequest(null, cb);
	}
	
}
