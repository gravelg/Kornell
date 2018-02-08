package kornell.gui.client.event;

import com.google.gwt.event.shared.GwtEvent;

public class ActomEnteredEvent extends GwtEvent<ActomEnteredEventHandler> {
    public static final Type<ActomEnteredEventHandler> TYPE = new Type<ActomEnteredEventHandler>();

    private String actomKey;
    private String enrollmentUUID;

    public ActomEnteredEvent(String enrollmentUUID, String actomKey) {
        this.enrollmentUUID = enrollmentUUID;
        this.actomKey = actomKey;
    }

    @Override
    protected void dispatch(ActomEnteredEventHandler handler) {
        handler.onActomEntered(this);
    }

    @Override
    public Type<ActomEnteredEventHandler> getAssociatedType() {
        return TYPE;
    }

    public String getActomKey() {
        return actomKey;
    }

    public String getEnrollmentUUID() {
        return enrollmentUUID;
    }

    public void setActomKey(String actomKey) {
        this.actomKey = actomKey;
    }

    public void setEnrollmentUUID(String enrollmentUUID) {
        this.enrollmentUUID = enrollmentUUID;
    }
}