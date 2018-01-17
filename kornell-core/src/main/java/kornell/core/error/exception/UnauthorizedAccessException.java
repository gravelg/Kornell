package kornell.core.error.exception;

@SuppressWarnings("serial")
public class UnauthorizedAccessException extends KornellBaseException {

    public UnauthorizedAccessException(String messageKey) {
        super(messageKey);
    }
}
