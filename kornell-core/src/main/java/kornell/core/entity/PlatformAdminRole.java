package kornell.core.entity;

public interface PlatformAdminRole extends Role {
    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);
}
