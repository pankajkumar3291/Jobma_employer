package com.jobma.employer.model.companyProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOCompanyData implements Serializable {

    @SerializedName("company")
    @Expose
    private EOCompany company;
    @SerializedName("industry")
    @Expose
    private List<EOIndustryArea> industry = null;
    @SerializedName("functional-area")
    @Expose
    private List<EOIndustryArea> functionalArea = null;
    @SerializedName("message")
    @Expose
    private String message;

    public EOCompany getCompany() {
        return company;
    }

    public void setCompany(EOCompany company) {
        this.company = company;
    }

    public List<EOIndustryArea> getIndustry() {
        return industry;
    }

    public void setIndustry(List<EOIndustryArea> industry) {
        this.industry = industry;
    }

    public List<EOIndustryArea> getFunctionalArea() {
        return functionalArea;
    }

    public void setFunctionalArea(List<EOIndustryArea> functionalArea) {
        this.functionalArea = functionalArea;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
