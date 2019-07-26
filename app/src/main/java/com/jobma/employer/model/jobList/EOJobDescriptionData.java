package com.jobma.employer.model.jobList;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOJobDescriptionData implements Serializable {

    @SerializedName("industry")
    @Expose
    private List<EOIndustryFunctional> industry = null;
    @SerializedName("functional")
    @Expose
    private List<EOIndustryFunctional> functional = null;
    @SerializedName("job_id")
    @Expose
    private Integer jobId;
    @SerializedName("job_title")
    @Expose
    private String jobTitle;
    @SerializedName("company_name")
    @Expose
    private String companyName;
    @SerializedName("locations")
    @Expose
    private String locations;
    @SerializedName("min_exp")
    @Expose
    private String minExp;
    @SerializedName("max_exp")
    @Expose
    private String maxExp;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("job_type")
    @Expose
    private String jobType;
    @SerializedName("salary_type")
    @Expose
    private String salaryType;
    @SerializedName("min_salary")
    @Expose
    private String minSalary;
    @SerializedName("max_salary")
    @Expose
    private String maxSalary;
    @SerializedName("qualification")
    @Expose
    private String qualification;
    @SerializedName("industries")
    @Expose
    private String industries;
    @SerializedName("functional_areas")
    @Expose
    private String functionalAreas;
    @SerializedName("keywords")
    @Expose
    private String keywords;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("company_profile")
    @Expose
    private String companyProfile;
    @SerializedName("recruiter_name")
    @Expose
    private String recruiterName;
    @SerializedName("recruiter_email")
    @Expose
    private String recruiterEmail;
    @SerializedName("recruiter_phone")
    @Expose
    private String recruiterPhone;
    @SerializedName("recruiter_ext")
    @Expose
    private String recruiterExt;
    @SerializedName("approval")
    @Expose
    private String approval;
    @SerializedName("notice_period")
    @Expose
    private String noticePeriod;
    @SerializedName("company_web")
    @Expose
    private String companyWeb;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("jobma_job_currency")
    @Expose
    private String jobmaJobCurrency;

    public List<EOIndustryFunctional> getIndustry() {
        return industry;
    }

    public void setIndustry(List<EOIndustryFunctional> industry) {
        this.industry = industry;
    }

    public List<EOIndustryFunctional> getFunctional() {
        return functional;
    }

    public void setFunctional(List<EOIndustryFunctional> functional) {
        this.functional = functional;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocations() {
        return locations;
    }

    public void setLocations(String locations) {
        this.locations = locations;
    }

    public String getMinExp() {
        return minExp;
    }

    public void setMinExp(String minExp) {
        this.minExp = minExp;
    }

    public String getMaxExp() {
        return maxExp;
    }

    public void setMaxExp(String maxExp) {
        this.maxExp = maxExp;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getJobType() {
        return jobType;
    }

    public void setJobType(String jobType) {
        this.jobType = jobType;
    }

    public String getSalaryType() {
        return salaryType;
    }

    public void setSalaryType(String salaryType) {
        this.salaryType = salaryType;
    }

    public String getMinSalary() {
        return minSalary;
    }

    public void setMinSalary(String minSalary) {
        this.minSalary = minSalary;
    }

    public String getMaxSalary() {
        return maxSalary;
    }

    public void setMaxSalary(String maxSalary) {
        this.maxSalary = maxSalary;
    }

    public String getQualification() {
        return qualification;
    }

    public void setQualification(String qualification) {
        this.qualification = qualification;
    }

    public String getIndustries() {
        return industries;
    }

    public void setIndustries(String industries) {
        this.industries = industries;
    }

    public String getFunctionalAreas() {
        return functionalAreas;
    }

    public void setFunctionalAreas(String functionalAreas) {
        this.functionalAreas = functionalAreas;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCompanyProfile() {
        return companyProfile;
    }

    public void setCompanyProfile(String companyProfile) {
        this.companyProfile = companyProfile;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public String getRecruiterPhone() {
        return recruiterPhone;
    }

    public void setRecruiterPhone(String recruiterPhone) {
        this.recruiterPhone = recruiterPhone;
    }

    public String getRecruiterExt() {
        return recruiterExt;
    }

    public void setRecruiterExt(String recruiterExt) {
        this.recruiterExt = recruiterExt;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getNoticePeriod() {
        return noticePeriod;
    }

    public void setNoticePeriod(String noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    public String getCompanyWeb() {
        return companyWeb;
    }

    public void setCompanyWeb(String companyWeb) {
        this.companyWeb = companyWeb;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getJobmaJobCurrency() {
        return jobmaJobCurrency;
    }

    public void setJobmaJobCurrency(String jobmaJobCurrency) {
        this.jobmaJobCurrency = jobmaJobCurrency;
    }
}
