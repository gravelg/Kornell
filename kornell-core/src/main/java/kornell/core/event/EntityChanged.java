package kornell.core.event;

import kornell.core.entity.AuditedEntityType;

public interface EntityChanged extends Event {
    public static final String TYPE = EventFactory.PREFIX+"EntityChanged+json";

    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);

    String getFromPersonUUID();
    void setFromPersonUUID(String fromPersonUUID);

    AuditedEntityType getEntityType();
    void setEntityType(AuditedEntityType entityType);

    String getEntityUUID();
    void setEntityUUID(String entityUUID);

    String getFromValue();
    void setFromValue(String fromValue);

    String getToValue();
    void setToValue(String toValue);

    String getEntityName();
    void setEntityName(String entityName);

    String getFromPersonName();
    void setFromPersonName(String fromPersonName);

    String getFromUsername();
    void setFromUsername(String fromUsername);
}
