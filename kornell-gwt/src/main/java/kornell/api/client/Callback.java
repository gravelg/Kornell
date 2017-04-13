package kornell.api.client;

import static com.google.gwt.http.client.Response.SC_CONFLICT;
import static com.google.gwt.http.client.Response.SC_FORBIDDEN;
import static com.google.gwt.http.client.Response.SC_INTERNAL_SERVER_ERROR;
import static com.google.gwt.http.client.Response.SC_NOT_FOUND;
import static com.google.gwt.http.client.Response.SC_NO_CONTENT;
import static com.google.gwt.http.client.Response.SC_OK;
import static com.google.gwt.http.client.Response.SC_SERVICE_UNAVAILABLE;
import static com.google.gwt.http.client.Response.SC_UNAUTHORIZED;
import static com.google.gwt.http.client.Response.SC_BAD_GATEWAY;

import java.util.logging.Logger;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.Response;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import kornell.core.entity.EntityFactory;
import kornell.core.error.KornellErrorTO;
import kornell.core.event.EventFactory;
import kornell.core.lom.LOMFactory;
import kornell.core.to.TOFactory;
import kornell.gui.client.GenericClientFactoryImpl;
import kornell.gui.client.KornellConstantsHelper;
import kornell.gui.client.event.LogoutEvent;

@SuppressWarnings("unchecked")
public abstract class Callback<T> implements RequestCallback {

	Logger logger = Logger.getLogger(Callback.class.getName());

	@Override
	public void onResponseReceived(Request request, Response response) {
		int statusCode = response.getStatusCode();
		switch (statusCode) {
		case SC_OK:
			ok(response);
			break;
		case SC_FORBIDDEN:
			forbidden(unwrapError(response));
			break;
		case SC_UNAUTHORIZED:
			unauthorized(unwrapError(response));
			break;
		case SC_CONFLICT:
			conflict(unwrapError(response));
			break;
		case SC_NOT_FOUND:
			notFound(unwrapError(response));
			break;
		case SC_NO_CONTENT:
			ok((T) null);
			break;
		case SC_INTERNAL_SERVER_ERROR:
			internalServerError(unwrapError(response));
			break;
		case SC_SERVICE_UNAVAILABLE:
			serviceUnavailable();
			break;
		case SC_BAD_GATEWAY:
			badGateway();
			break;
		case 0:
			failed();
			break;
		default:
			logger.fine("Got a response, but don't know what to do about it");
			break;
		}
	}

	protected void ok(Response response) {
		dispatchByMimeType(response);
	}

	private void dispatchByMimeType(Response response) {
		String contentTypeHeader = response.getHeader("Content-Type");
		String contentType = contentTypeHeader.toLowerCase();
		String responseText = response.getText();

		if (contentType.equals("application/boolean")) {
			T bool = bool(responseText);
			ok(bool);
		} else if (contentType.contains("json")) {
			if (MediaTypes.get().containsType(contentType)) {
				Class<T> clazz = (Class<T>) MediaTypes.get().classOf(contentType);

				AutoBean<T> bean = null;
				AutoBeanFactory factory = factoryFor(contentType);
				if ("null".equals(responseText))
					throw new NullPointerException(
							"The remote service returned 'null', this is probably a bug.");
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

		} else if (contentType.contains("application/octet-stream")) {
			T txt = (T) responseText;
			ok(txt);
		} else
			ok((T) null); // TODO: Consider throwing exception
							// "unknow response type" instead, but map "text/*"
							// and "application/*" first
	}

	private KornellErrorTO unwrapError(Response response) {
		String responseText = response.getText();
		if (responseText == null || responseText.trim().equals("")) {
			throw new RuntimeException("Response text was blank");
		}
		String contentType = response.getHeader("Content-Type").toLowerCase();
		AutoBean<T> bean = null;
		AutoBeanFactory factory = factoryFor(contentType);
		if(factory == null){
			KornellErrorTO errorTO = GenericClientFactoryImpl.TO_FACTORY.newKornellErrorTO().as();
			errorTO.setMessageKey("genericUnhandledError");
			return errorTO;
		}
		Class<T> clazz = (Class<T>) MediaTypes.get().classOf(contentType);
		bean = AutoBeanCodex.decode(factory, clazz, responseText);
		T unwrapped = bean.as();
		if (unwrapped instanceof KornellErrorTO) {
			return (KornellErrorTO) unwrapped;
		}
		throw new RuntimeException("Supposed to get a KornellErrorTO, got "
				+ unwrapped.getClass());
	}

	protected void notFound(KornellErrorTO kornellErrorTO) {
		logger.fine(KornellConstantsHelper.getErrorMessage(kornellErrorTO));
	}

	protected void internalServerError(KornellErrorTO kornellErrorTO) {
		logger.severe(KornellConstantsHelper
				.getErrorMessage(kornellErrorTO));
		logger.severe("Cause: " + kornellErrorTO.getException());
	}

	protected void serviceUnavailable() {
	}

	protected void badGateway() {
	}

	private T bool(String responseText) {
		return (T) Boolean.valueOf(responseText);
	}

	private AutoBeanFactory factoryFor(String contentType) {
		if (contentType == null)
			throw new NullPointerException(
					"Can't create factory without content type");
		if (contentType.startsWith(TOFactory.PREFIX))
			return GenericClientFactoryImpl.TO_FACTORY;
		else if (contentType.startsWith(LOMFactory.PREFIX))
			return GenericClientFactoryImpl.LOM_FACTORY;
		else if (contentType.startsWith(EventFactory.PREFIX))
			return GenericClientFactoryImpl.EVENT_FACTORY;
		else if (contentType.startsWith(EntityFactory.PREFIX))
			return GenericClientFactoryImpl.ENTITY_FACTORY;
		else
			return null;

	}

	public abstract void ok(T to);

	protected void ok(JSONValue json) {
	}

	private static JSONValue parseJson(String jsonStr) {
		return JSONParser.parseStrict(jsonStr);
	}

	protected void failed() {
		logger.severe("Your request failed. Please check that the API is running and responding cross-origin requests.");
	}

	protected void unauthorized(KornellErrorTO kornellErrorTO) {
		GenericClientFactoryImpl.EVENT_BUS.fireEvent(new LogoutEvent());
		logger.fine(KornellConstantsHelper
				.getErrorMessage(kornellErrorTO));
	}

	protected void conflict(KornellErrorTO kornellErrorTO) {
		logger.info(KornellConstantsHelper.getErrorMessage(kornellErrorTO));
	}

	protected void forbidden(KornellErrorTO kornellErrorTO) {
		if (kornellErrorTO != null)
			logger.fine(KornellConstantsHelper
					.getErrorMessage(kornellErrorTO));
	}

	@Override
	public void onError(Request request, Throwable exception) {
		error(request, exception);

	}

	private void error(Request request, Throwable exception) {
		logger.severe("Error: " + exception.getMessage());
	}

}
