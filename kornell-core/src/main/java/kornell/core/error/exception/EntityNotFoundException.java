package kornell.core.error.exception;

@SuppressWarnings("serial")
public class EntityNotFoundException extends KornellBaseException {

    public EntityNotFoundException(String messageKey) {
        super(messageKey);
    }
}
