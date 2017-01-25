package kornell.gui.client.util;

import java.util.Date;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.storage.client.Storage;
import com.google.gwt.user.client.Cookies;

import kornell.core.util.StringUtils;

//TODO: if this is user specific, move to UserSession
public class ClientProperties {
	private static Storage localStorage = Storage.getLocalStorageIfSupported();

	public static final String SEPARATOR = ".";
	public static final String PREFIX = "Kornell.v1.";
	public static final String X_KNL_TOKEN = "X-KNL-TOKEN";
	public static final String SELECTED_COURSE_CLASS = "SELECTED_COURSE_CLASS";
	public static final String CURRENT_SESSION = "CURRENT_SESSION";
	public static final String CURRENT_ENROLLMENT = "CURRENT_ENROLLMENT";
	
	private static DateTimeFormat format = DateTimeFormat.getFormat("dd/MM/yyyy");
	
	public static String get(String propertyName){
		if(localStorage != null){
			return localStorage.getItem(propertyName);
		} else if (Cookies.isCookieEnabled()) {
			return Cookies.getCookie(propertyName);
		}
		return null;
	}
	
	public static void set(String propertyName, String propertyValue){
		if(localStorage != null){
			localStorage.setItem(propertyName, propertyValue);
		} else if (Cookies.isCookieEnabled()) {
			Cookies.setCookie(propertyName, propertyValue);
		}
	}
	
	public static String getLocaleCookie(){
		final String cookieName = LocaleInfo.getLocaleCookieName();
		return Cookies.getCookie( cookieName );
	}
	
	public static void setLocaleCookie(String locale){
	    String cookieName = LocaleInfo.getLocaleCookieName();
	    String localeCookie = getLocaleCookie();
	    if (cookieName != null){
	        Cookies.setCookie( cookieName, locale, format.parse("01/01/2030"));
	    }
		if(localeCookie != null && !localeCookie.equals(locale))
			com.google.gwt.user.client.Window.Location.reload();
	}
	
	public static void setCookie(String name, String value, Date expires){
	    if (StringUtils.isSome(name) && value != null){
	        Cookies.setCookie( name, value, expires != null ? expires : format.parse("01/01/2030"));
	    }
	}
	
	public static String getCookie(String name){
	    return Cookies.getCookie(name);
	}
	
	
	public static void removeCookie(String name){
		Cookies.removeCookie(name);
	}
	
	public static void remove(String propertyName){
		if(localStorage != null){
			localStorage.removeItem(propertyName);
		} else if (Cookies.isCookieEnabled()) {
			removeCookie(propertyName);
		}
	}
	
	public static String getDecoded(String propertyName){
		String value = get(propertyName);
		return value == null ? null : base64Decode(value);
	}
	
	public static void setEncoded(String propertyName, String propertyValue){
		set(propertyName, base64Encode(propertyValue));
	}
	

	public static String base64Encode(String plain) {
		return Base64Utils.toBase64(plain.getBytes());
	};

	public static String base64Decode(String base64) {
		return new String(Base64Utils.fromBase64(base64));
	}


}
