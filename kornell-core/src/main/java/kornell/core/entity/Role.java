package kornell.core.entity;

public interface Role extends Entity {
    public static String TYPE = EntityFactory.PREFIX + "role+json";

    RoleType getRoleType();
    void setRoleType(RoleType roleType);

    String getPersonUUID();
    void setPersonUUID(String personUUID);

    UserRole getUserRole();
    void setUserRole(UserRole userRole);

    PlatformAdminRole getPlatformAdminRole();
    void setPlatformAdminRole(PlatformAdminRole platformRole);

    InstitutionAdminRole getInstitutionAdminRole();
    void setInstitutionAdminRole(InstitutionAdminRole institutionAdminRole);

    CourseClassAdminRole getCourseClassAdminRole();
    void setCourseClassAdminRole(CourseClassAdminRole courseClassAdminRole);

    TutorRole getTutorRole();
    void setTutorRole(TutorRole tutorRole);

    ObserverRole getObserverRole();
    void setObserverRole(ObserverRole observerRole);

    ControlPanelAdminRole getControlPanelAdminRole();
    void setControlPanelAdminRole(ControlPanelAdminRole controlPanelAdminRole);
}
