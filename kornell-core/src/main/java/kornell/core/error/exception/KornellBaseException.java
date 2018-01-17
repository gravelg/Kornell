package kornell.core.error.exception;

import java.util.logging.Level;
import java.util.logging.Logger;

@SuppressWarnings("serial")
public class KornellBaseException extends Exception {

    private static final Logger logger = Logger.getLogger(KornellBaseException.class.getName());

    private String messageKey;

    public KornellBaseException(String messageKey) {
        this.messageKey = messageKey;
        logger.severe(messageKey);
    }

    public KornellBaseException(String messageKey, Exception cause) {
        super(cause);
        this.messageKey = messageKey;
        logger.log(Level.SEVERE, messageKey, cause);
    }

    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
