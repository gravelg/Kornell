package kornell.core.to.report;

import java.util.Date;

import kornell.core.entity.RepositoryType;

public class CertificateInformationTO {
    private String personFullName;
    private String personCPF;
    private String courseTitle;
    private String courseClassName;
    private String assetsRepositoryUUID;
    private String distributionPrefix;
    private String courseVersionUUID;
    private String courseClassUUID;
    private String courseUUID;
    private Date courseClassFinishedDate;
    private String baseURL;
    private String institutionName;
    private RepositoryType repositoryType;
    private String courseCode;

    public String getPersonFullName() {
        return personFullName;
    }
    public void setPersonFullName(String personFullName) {
        this.personFullName = personFullName;
    }
    public String getPersonCPF() {
        return personCPF;
    }
    public void setPersonCPF(String personCPF) {
        this.personCPF = personCPF;
    }
    public String getCourseTitle() {
        return courseTitle;
    }
    public void setCourseTitle(String courseTitle) {
        this.courseTitle = courseTitle;
    }
    public String getCourseClassName() {
        return courseClassName;
    }
    public void setCourseClassName(String courseClassName) {
        this.courseClassName = courseClassName;
    }
    public String getAssetsRepositoryUUID() {
        return assetsRepositoryUUID;
    }
    public void setAssetsRepositoryUUID(String assetsRepositoryUUID) {
        this.assetsRepositoryUUID = assetsRepositoryUUID;
    }
    public String getDistributionPrefix() {
        return distributionPrefix;
    }
    public void setDistributionPrefix(String distributionPrefix) {
        this.distributionPrefix = distributionPrefix;
    }
    public Date getCourseClassFinishedDate() {
        return courseClassFinishedDate;
    }
    public void setCourseClassFinishedDate(Date courseClassFinishedDate) {
        this.courseClassFinishedDate = courseClassFinishedDate;
    }
    public String getCourseVersionUUID() {
        return courseVersionUUID;
    }
    public void setCourseVersionUUID(String courseVersionUUID) {
        this.courseVersionUUID = courseVersionUUID;
    }
    public String getCourseClassUUID() {
        return courseClassUUID;
    }
    public void setCourseClassUUID(String courseClassUUID) {
        this.courseClassUUID = courseClassUUID;
    }
    public String getCourseUUID() {
        return courseUUID;
    }
    public void setCourseUUID(String courseUUID) {
        this.courseUUID = courseUUID;
    }
    public String getBaseURL() {
        return baseURL;
    }
    public void setBaseURL(String baseURL) {
        this.baseURL = baseURL;
    }
    public String getInstitutionName() {
        return institutionName;
    }
    public void setInstitutionName(String institutionName) {
        this.institutionName = institutionName;
    }
    public RepositoryType getRepositoryType() {
        return repositoryType;
    }
    public void setRepositoryType(RepositoryType repositoryType) {
        this.repositoryType = repositoryType;
    }
    public String getCourseCode() {
        return courseCode;
    }
    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }
}
