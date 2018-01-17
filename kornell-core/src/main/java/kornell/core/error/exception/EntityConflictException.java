package kornell.core.error.exception;

@SuppressWarnings("serial")
public class EntityConflictException extends KornellBaseException {

    public EntityConflictException(String messageKey) {
        super(messageKey);
    }
}
