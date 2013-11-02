package kornell.api.client;

import static com.google.gwt.http.client.Response.SC_FORBIDDEN;
import static com.google.gwt.http.client.Response.SC_NOT_FOUND;
import static com.google.gwt.http.client.Response.SC_OK;
import static com.google.gwt.http.client.Response.SC_UNAUTHORIZED;
import kornell.core.entity.EntityFactory;
import kornell.core.event.EventFactory;
import kornell.core.lom.LOMFactory;
import kornell.core.to.TOFactory;
import kornell.gui.client.GenericClientFactoryImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;


public class Callback<T> implements RequestCallback {
	private static final MediaTypes mimeTypes = new MediaTypes();

	@Override
	public void onResponseReceived(Request request, Response response) {
		if (!isTrusted(response))
			throw new RuntimeException("Won't touch untrusted response");
		int statusCode = response.getStatusCode();
		switch (statusCode) {
		case SC_OK:
			ok(response);
			break;
		case SC_FORBIDDEN:
			forbidden();
			break;
		case SC_UNAUTHORIZED:
			unauthorized();
			break;
		case SC_NOT_FOUND:
			notFound();
			break;
		case 0:
			failed();
			break;
		default:
			GWT.log("Got a response, but don't know what to do about it");
			break;
		}
	}

	protected void notFound() {

	}

	protected void ok(Response response) {
		dispatchByMimeType(response);
	}

	private void dispatchByMimeType(Response response) {
		String contentType = response.getHeader("Content-Type").toLowerCase();
		String responseText = response.getText();

		if (contentType.contains("json")) {
			if (mimeTypes.containsKey(contentType)) {
				@SuppressWarnings("unchecked")
				Class<T> clazz = (Class<T>) mimeTypes.get(contentType);

				AutoBean<T> bean = null;
				AutoBeanFactory factory = factoryFor(contentType);
				bean = AutoBeanCodex.decode(factory, clazz, responseText);
				T unwrapped = bean.as();
				try {
					ok(unwrapped);
				} catch (ClassCastException ex) {
					String message = "Could not dispatch object of type ["
							+ clazz.getName()
							+ "] to this callback. Please check that your callback type mapping matches the response ContentType and you are hitting the correct URL.";
					throw new RuntimeException(message, ex);
				}
			} else
				ok(Callback.parseJson(responseText));

		} else
			ok();
	}

	private AutoBeanFactory factoryFor(String contentType) {
		if(contentType == null) throw new NullPointerException("Can't create factory without content type");
		if(contentType.startsWith(TOFactory.PREFIX))
			return GenericClientFactoryImpl.toFactory;
		else if(contentType.startsWith(LOMFactory.PREFIX))
			return GenericClientFactoryImpl.lomFactory;
		else if(contentType.startsWith(EventFactory.PREFIX))
			return GenericClientFactoryImpl.eventFactory;
		else if (contentType.startsWith(EntityFactory.PREFIX))
			return GenericClientFactoryImpl.entityFactory;
		else throw new IllegalArgumentException("Unknown factory for content type ["+contentType+"]");
		
	}

	protected void ok(T to) {
	}

	protected void ok(JSONValue json) {
	}

	private static JSONValue parseJson(String jsonStr) {
		return JSONParser.parseStrict(jsonStr);
	}

	protected boolean isTrusted(Response response) {
		return true;
	}

	protected void failed() {
		GWT.log("Your request failed. Please check that the API is running and responding cross-origin requests.");
	}

	protected void unauthorized() {
	}

	protected void forbidden() {
	}

	protected void ok() {
	}

	@Override
	public void onError(Request request, Throwable exception) {
		error(request, exception);

	}

	private void error(Request request, Throwable exception) {
		GWT.log("Error!", exception);
	}

}
