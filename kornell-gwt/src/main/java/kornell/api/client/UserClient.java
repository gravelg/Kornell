package kornell.api.client;

import com.google.gwt.http.client.URL;

import kornell.core.to.RegistrationRequestTO;
import kornell.core.to.UserHelloTO;
import kornell.core.to.UserInfoTO;
import kornell.core.util.StringUtils;

public class UserClient extends RESTClient {

	// TODO: Is this safe?
	public void getUser(String userUUID, Callback<UserInfoTO> cb) {
		GET("/user/get/" + userUUID).sendRequest(null, cb);
	}

	public void getUserHello(String name, String hostName, Callback<UserHelloTO> cb) {
		//TODO: AUTHDEBUG Move this to server?
		if(StringUtils.isNone(name)){
			String[] dots = hostName.split("\\.");
			if(dots.length > 0){
				String id = dots[0];
				String[] slashes = id.split("-");
				if (slashes.length > 0) //@TODO and is followed by only numbers
					name = slashes[0];
				}
		}
		String path = "/user/login?" + (StringUtils.isSome(name) ? "name="+name : "hostName="+hostName);
		GET(path).sendRequest(null, cb);
	}

	public void checkUser(String institutionUUID, String email, Callback<UserInfoTO> cb) {
		GET("/user/check",institutionUUID,email).sendRequest(null, cb);
	}

	public void requestRegistration(RegistrationRequestTO registrationRequestTO, Callback<UserInfoTO> cb) {
		PUT("/user/registrationRequest").withContentType(RegistrationRequestTO.TYPE).withEntityBody(registrationRequestTO).go(cb);
	}

	public void requestPasswordChange(String email, String institutionName, Callback<Void> cb) {
		GET("/user/requestPasswordChange/" + URL.encodePathSegment(email) + "/" + institutionName).sendRequest(null, cb);
	}

	public void changePassword(String password, String passwordChangeUUID, Callback<UserInfoTO> cb) {
		PUT("/user/resetPassword/" + passwordChangeUUID).sendRequest(password, cb);
	}

	public void changeTargetPassword(String targetPersonUUID, String password, Callback<Void> cb) {
		PUT("/user/changePassword/" + targetPersonUUID).sendRequest(password, cb);
	}
	
	public void forcedPasswordChange(String username, String password, Callback<UserInfoTO> cb) {
		PUT("/user/updatePassword/" + username).sendRequest(password, cb);
	}

	public void hasPowerOver(String targetPersonUUID, Callback<Boolean> cb) {
		GET("/user/hasPowerOver/" + targetPersonUUID).sendRequest(null, cb);
	}

	public void updateUser(UserInfoTO userInfo, Callback<UserInfoTO> cb) {
		PUT("/user/" + userInfo.getPerson().getUUID()).withContentType(UserInfoTO.TYPE).withEntityBody(userInfo).go(cb);
	}
	
	public void acceptTerms(Callback<UserInfoTO> cb) {
		PUT("/user/acceptTerms").sendRequest(null, cb);
	}
	
}
