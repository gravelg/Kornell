package kornell.core.entity;



public interface InstitutionRegistrationPrefix extends Named {
    public static String TYPE = EntityFactory.PREFIX + "institutionRegistrationPrefix+json";

    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);

    boolean isShowEmailOnProfile();
    void setShowEmailOnProfile(boolean showEmailOnProfile);

    boolean isShowCPFOnProfile();
    void setShowCPFOnProfile(boolean showCPFOnProfile);

    boolean isShowContactInformationOnProfile();
    void setShowContactInformationOnProfile(boolean showContactInformationOnProfile);
}
