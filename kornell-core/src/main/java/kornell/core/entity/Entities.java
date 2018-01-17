package kornell.core.entity;

/*
 * Factory methods for entities
 */
public abstract class Entities {

    public EnrollmentProgress newEnrollmentProgress() {
        return getEntityFactory().newEnrollmentProgress().as();
    }

    protected abstract EntityFactory getEntityFactory();
}
