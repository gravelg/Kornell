package kornell.api.client;

import java.util.logging.Logger;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import kornell.core.event.EventFactory;

public class ExceptionalRequestBuilder extends RequestBuilder {
    static Logger logger = Logger.getLogger(ExceptionalRequestBuilder.class.getName());

    protected static final RequestCallback NOOP = new RequestCallback() {
        @Override
        public void onResponseReceived(Request request, Response response) {
            logger.fine("NOOP ResponseReceived");
        }

        @Override
        public void onError(Request request, Throwable exception) {
            logger.fine("NOOP onError");
        }
    };

    public ExceptionalRequestBuilder(Method httpMethod, String url) {
        super(httpMethod, url);
    }

    @Override
    public Request sendRequest(String requestData, RequestCallback callback) {
        try {
            if (requestData == null) {
                super.setCallback(callback);
                return super.send();
            } else {
                return super.sendRequest(requestData, callback);
            }
        } catch (RequestException e) {
            return handle(e);
        }
    }

    private Request handle(RequestException e) {
        logger.fine(e.getMessage());
        throw new RuntimeException(e);
    }

    public ExceptionalRequestBuilder addHeader(String header, String value) {
        setHeader(header, value);
        return this;
    }

    public void go(Callback<?> callback) {
        if (callback != null) {
            setCallback(callback);
        }
        go();
    }

    public void go() {
        try {
            if (getCallback() == null) {
                setCallback(NOOP);
            }
            send();
        } catch (RequestException e) {
            handle(e);
        }
    }

    // TODO: Move All creation to clientfactory
    EventFactory eventFactory = GWT.create(EventFactory.class);

    private <T> ExceptionalRequestBuilder withBody(T object) {
        AutoBean<T> autoBean = AutoBeanUtils.getAutoBean(object);
        String reqData = AutoBeanCodex.encode(autoBean).getPayload();
        setRequestData(reqData);
        return this;
    }

    public <T> ExceptionalRequestBuilder withEntityBody(T object) {
        return withBody(object);
    }

    public String contentTypeOf(Object o) {
        // TODO: Fix and automate
        return MediaTypes.get().typeOf(o.getClass());
    }

    public ExceptionalRequestBuilder withContentType(String contentType) {
        if (contentType != null)
            setHeader("Content-Type", contentType);
        else
            throw new NullPointerException("Header value ");
        return this;
    }

}
