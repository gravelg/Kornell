package kornell.core.error.exception;

@SuppressWarnings("serial")
public class AuthenticationException extends KornellBaseException {

    public AuthenticationException(String messageKey) {
        super(messageKey);
    }
}
