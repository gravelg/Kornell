package kornell.core.entity;

import java.util.Date;


public interface Person {
    public static String TYPE = EntityFactory.PREFIX + "person+json";

    String getUUID();
    void setUUID(String uuid);

    String getFullName();
    void setFullName(String fullName);

    String getLastPlaceVisited();
    void setLastPlaceVisited(String lastPlaceVisited);

    String getEmail();
    void setEmail(String email);

    String getCPF();
    void setCPF(String cpf);

    String getCompany();
    void setCompany(String company);

    String getTitle();
    void setTitle(String title);

    String getSex();
    void setSex(String sex);

    Date getBirthDate();
    void setBirthDate(Date birthDate);

    String getConfirmation();
    void setConfirmation(String confirmation);

    String getTelephone();
    void setTelephone(String telephone);

    String getCountry();
    void setCountry(String country);

    String getState();
    void setState(String state);

    String getCity();
    void setCity(String city);

    String getAddressLine1();
    void setAddressLine1(String addressLine1);

    String getAddressLine2();
    void setAddressLine2(String addressLine2);

    String getPostalCode();
    void setPostalCode(String postalCode);

    String getInstitutionUUID();
    void setInstitutionUUID(String institutionUUID);

    Date getTermsAcceptedOn();
    void setTermsAcceptedOn(Date termsAcceptedOn);

    RegistrationType getRegistrationType();
    void setRegistrationType(RegistrationType registrationType);

    String getInstitutionRegistrationPrefixUUID();
    void setInstitutionRegistrationPrefixUUID(String institutionRegistrationPrefixUUID);

    boolean isReceiveEmailCommunication();
    void setReceiveEmailCommunication(boolean receiveEmailCommunication);

    boolean isForcePasswordUpdate();
    void setForcePasswordUpdate(boolean forcePasswordUpdate);
}
