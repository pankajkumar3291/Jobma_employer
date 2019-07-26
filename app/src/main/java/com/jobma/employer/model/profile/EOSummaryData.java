package com.jobma.employer.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOSummaryData implements Serializable {

    @SerializedName("pitcher_id")
    @Expose
    private Integer pitcherId;
    @SerializedName("exp_year")
    @Expose
    private Integer expYear;
    @SerializedName("exp_month")
    @Expose
    private Integer expMonth;
    @SerializedName("pay_rate")
    @Expose
    private String payRate;
    @SerializedName("current_company")
    @Expose
    private String currentCompany;
    @SerializedName("current_designation")
    @Expose
    private String currentDesignation;
    @SerializedName("current_location")
    @Expose
    private String currentLocation;
    @SerializedName("current_salary")
    @Expose
    private String currentSalary;
    @SerializedName("currency")
    @Expose
    private Object currency;
    @SerializedName("expected_salary")
    @Expose
    private String expectedSalary;
    @SerializedName("notice_period")
    @Expose
    private String noticePeriod;
    @SerializedName("availability")
    @Expose
    private String availability;
    @SerializedName("key_skills")
    @Expose
    private String keySkills;
    @SerializedName("objective")
    @Expose
    private String objective;
    @SerializedName("facebook")
    @Expose
    private String facebook;
    @SerializedName("linkedin")
    @Expose
    private String linkedin;
    @SerializedName("twitter")
    @Expose
    private String twitter;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("skype")
    @Expose
    private String skype;
    @SerializedName("visa_status")
    @Expose
    private String visaStatus;
    @SerializedName("relocate")
    @Expose
    private String relocate;
    @SerializedName("desire_location")
    @Expose
    private String desireLocation;
    @SerializedName("desire_jobtype")
    @Expose
    private String desireJobtype;
    @SerializedName("open_for_contract")
    @Expose
    private String openForContract;
    @SerializedName("negotiable")
    @Expose
    private String negotiable;
    @SerializedName("industry")
    @Expose
    private List<EOIndustryFunctional> industry = null;
    @SerializedName("functional")
    @Expose
    private List<EOIndustryFunctional> functional = null;
    @SerializedName("currency_type")
    @Expose
    private String currencyType;

    public Integer getPitcherId() {
        return pitcherId;
    }

    public void setPitcherId(Integer pitcherId) {
        this.pitcherId = pitcherId;
    }

    public Integer getExpYear() {
        return expYear;
    }

    public void setExpYear(Integer expYear) {
        this.expYear = expYear;
    }

    public Integer getExpMonth() {
        return expMonth;
    }

    public void setExpMonth(Integer expMonth) {
        this.expMonth = expMonth;
    }

    public String getPayRate() {
        return payRate;
    }

    public void setPayRate(String payRate) {
        this.payRate = payRate;
    }

    public String getCurrentCompany() {
        return currentCompany;
    }

    public void setCurrentCompany(String currentCompany) {
        this.currentCompany = currentCompany;
    }

    public String getCurrentDesignation() {
        return currentDesignation;
    }

    public void setCurrentDesignation(String currentDesignation) {
        this.currentDesignation = currentDesignation;
    }

    public String getCurrentLocation() {
        return currentLocation;
    }

    public void setCurrentLocation(String currentLocation) {
        this.currentLocation = currentLocation;
    }

    public String getCurrentSalary() {
        return currentSalary;
    }

    public void setCurrentSalary(String currentSalary) {
        this.currentSalary = currentSalary;
    }

    public Object getCurrency() {
        return currency;
    }

    public void setCurrency(Object currency) {
        this.currency = currency;
    }

    public String getExpectedSalary() {
        return expectedSalary;
    }

    public void setExpectedSalary(String expectedSalary) {
        this.expectedSalary = expectedSalary;
    }

    public String getNoticePeriod() {
        return noticePeriod;
    }

    public void setNoticePeriod(String noticePeriod) {
        this.noticePeriod = noticePeriod;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public String getKeySkills() {
        return keySkills;
    }

    public void setKeySkills(String keySkills) {
        this.keySkills = keySkills;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getFacebook() {
        return facebook;
    }

    public void setFacebook(String facebook) {
        this.facebook = facebook;
    }

    public String getLinkedin() {
        return linkedin;
    }

    public void setLinkedin(String linkedin) {
        this.linkedin = linkedin;
    }

    public String getTwitter() {
        return twitter;
    }

    public void setTwitter(String twitter) {
        this.twitter = twitter;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getSkype() {
        return skype;
    }

    public void setSkype(String skype) {
        this.skype = skype;
    }

    public String getVisaStatus() {
        return visaStatus;
    }

    public void setVisaStatus(String visaStatus) {
        this.visaStatus = visaStatus;
    }

    public String getRelocate() {
        return relocate;
    }

    public void setRelocate(String relocate) {
        this.relocate = relocate;
    }

    public String getDesireLocation() {
        return desireLocation;
    }

    public void setDesireLocation(String desireLocation) {
        this.desireLocation = desireLocation;
    }

    public String getDesireJobtype() {
        return desireJobtype;
    }

    public void setDesireJobtype(String desireJobtype) {
        this.desireJobtype = desireJobtype;
    }

    public String getOpenForContract() {
        return openForContract;
    }

    public void setOpenForContract(String openForContract) {
        this.openForContract = openForContract;
    }

    public String getNegotiable() {
        return negotiable;
    }

    public void setNegotiable(String negotiable) {
        this.negotiable = negotiable;
    }

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

    public String getCurrencyType() {
        return currencyType;
    }

    public void setCurrencyType(String currencyType) {
        this.currencyType = currencyType;
    }

}
