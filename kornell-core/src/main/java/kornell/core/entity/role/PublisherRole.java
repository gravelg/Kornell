package kornell.core.entity.role;

public interface PublisherRole extends Role {
    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);
}
