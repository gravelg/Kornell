package kornell.core.entity.role;

public interface InstitutionAdminRole extends Role {
    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);
}
