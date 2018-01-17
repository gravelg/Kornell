package kornell.core.error;

import kornell.core.to.TOFactory;

public interface KornellErrorTO {
    public static final String TYPE = TOFactory.PREFIX + "kornell.error+json";

    String getMessageKey();
    void setMessageKey(String messageKey);

    String getException();
    void setException(String exception);
}
