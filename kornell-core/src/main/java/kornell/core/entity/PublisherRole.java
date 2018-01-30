package kornell.core.entity;

public interface PublisherRole extends Role {
    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);
}
