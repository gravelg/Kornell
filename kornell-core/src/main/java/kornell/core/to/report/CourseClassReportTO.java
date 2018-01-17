package kornell.core.to.report;

import java.math.BigDecimal;
import java.util.Date;

public class CourseClassReportTO {
    private String fullName;
    private String username;
    private String email;
    private String cpf;
    private String state;
    private String progressState;
    private Integer progress;
    private BigDecimal assessmentScore;
    private BigDecimal preAssessmentScore;
    private BigDecimal postAssessmentScore;
    private Date certifiedAt;
    private Date enrolledAt;
    private String courseName;
    private String courseVersionName;
    private String courseClassName;
    private EnrollmentsBreakdownTO enrollmentsBreakdownTO;
    private String company;
    private String title;
    private String sex;
    private String birthDate;
    private String telephone;
    private String country;
    private String stateProvince;
    private String city;
    private String addressLine1;
    private String addressLine2;
    private String postalCode;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProgressState() {
        return progressState;
    }

    public void setProgressState(String progressState) {
        this.progressState = progressState;
    }

    public Integer getProgress() {
        return progress;
    }

    public void setProgress(Integer progress) {
        this.progress = progress;
    }

    public EnrollmentsBreakdownTO getEnrollmentsBreakdownTO() {
        return enrollmentsBreakdownTO;
    }

    public void setEnrollmentsBreakdownTO(
            EnrollmentsBreakdownTO enrollmentsBreakdownTO) {
        this.enrollmentsBreakdownTO = enrollmentsBreakdownTO;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getAssessmentScore() {
        return assessmentScore;
    }

    public void setAssessmentScore(BigDecimal assessmentScore) {
        this.assessmentScore = assessmentScore;
    }

    public BigDecimal getPreAssessmentScore() {
        return preAssessmentScore;
    }

    public void setPreAssessmentScore(BigDecimal preAssessmentScore) {
        this.preAssessmentScore = preAssessmentScore;
    }

    public BigDecimal getPostAssessmentScore() {
        return postAssessmentScore;
    }

    public void setPostAssessmentScore(BigDecimal postAssessmentScore) {
        this.postAssessmentScore = postAssessmentScore;
    }

    public Date getCertifiedAt() {
        return certifiedAt;
    }

    public void setCertifiedAt(Date certifiedAt) {
        this.certifiedAt = certifiedAt;
    }

    public Date getEnrolledAt() {
        return enrolledAt;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseVersionName() {
        return courseVersionName;
    }

    public void setCourseVersionName(String courseVersionName) {
        this.courseVersionName = courseVersionName;
    }

    public String getCourseClassName() {
        return courseClassName;
    }

    public void setCourseClassName(String courseClassName) {
        this.courseClassName = courseClassName;
    }

    public void setEnrolledAt(Date enrolledAt) {
        this.enrolledAt = enrolledAt;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getStateProvince() {
        return stateProvince;
    }

    public void setStateProvince(String stateProvince) {
        this.stateProvince = stateProvince;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

}
