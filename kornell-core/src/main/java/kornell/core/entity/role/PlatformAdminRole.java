package kornell.core.entity.role;

public interface PlatformAdminRole extends Role {
    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);
}
