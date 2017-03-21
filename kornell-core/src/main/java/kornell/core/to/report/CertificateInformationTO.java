package kornell.core.to.report;

import java.util.Date;

import kornell.core.entity.RepositoryType;

public class CertificateInformationTO {
	private String personFullName;
	private String personCPF;
	private String courseTitle;
	private String courseClassName;
	private String assetsURL;
	private String distributionPrefix;
	private String courseVersionUUID;
	private Date courseClassFinishedDate;
	private String baseURL;
	private String institutionName;
	private RepositoryType repositoryType;
	
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
	public String getAssetsURL() {
		return assetsURL;
	}
	public void setAssetsURL(String assetsURL) {
		this.assetsURL = assetsURL;
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
}
