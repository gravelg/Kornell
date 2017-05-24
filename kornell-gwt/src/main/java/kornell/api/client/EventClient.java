package kornell.api.client;

import kornell.core.event.Event;

public class EventClient extends RESTClient {

	private Event event;
	private String contentType;
	private String path;

	public EventClient(String path, String contentType, Event event) {
		this.event = event;
		this.path = path;
		this.contentType = contentType;
	}

	public void fire() {
		PUT(path).withContentType(contentType).withEntityBody(event).go();
	}

	public void fire(Callback<Void> cb) {
		PUT(path).withContentType(contentType).withEntityBody(event).go(cb);
	}

}
