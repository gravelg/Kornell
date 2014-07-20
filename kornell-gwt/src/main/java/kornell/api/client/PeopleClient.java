package kornell.api.client;

import kornell.core.entity.People;

import com.google.gwt.http.client.URL;

public class PeopleClient extends KornellClient {

	public void findBySearchTerm(String search, String institutionUUID,
			Callback<People> callback) {
		GET("/people/?search="
				+ URL.encodePathSegment(search)
				+ "&institutionUUID="
				+ institutionUUID)
				.sendRequest("", callback);
	}

	public void isCPFRegistered(String cpf, Callback<Boolean> cb) {
		GET("/people/isRegistered?cpf=" + cpf)
				.sendRequest("", cb);
	}

	public void isEmailRegistered(String email, Callback<Boolean> cb) {
		GET("/people/isRegistered?email=" + email)
				.sendRequest("", cb);
	}

}