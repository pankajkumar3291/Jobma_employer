package com.jobma.employer.model.companyProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOCompany implements Serializable {

    @SerializedName("jobma_catcher_id")
    @Expose
    private Integer jobmaCatcherId;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("employer_email")
    @Expose
    private String employerEmail;
    @SerializedName("company_email")
    @Expose
    private String companyEmail;
    @SerializedName("organisation")
    @Expose
    private String organisation;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("address2")
    @Expose
    private String address2;
    @SerializedName("city")
    @Expose
    private Integer city;
    @SerializedName("state")
    @Expose
    private Integer state;
    @SerializedName("country")
    @Expose
    private Integer country;
    @SerializedName("zip")
    @Expose
    private String zip;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("fax")
    @Expose
    private String fax;
    @SerializedName("ext")
    @Expose
    private String ext;
    @SerializedName("about_company")
    @Expose
    private String aboutCompany;
    @SerializedName("functional_area")
    @Expose
    private String functionalArea;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("state_name")
    @Expose
    private String stateName;
    @SerializedName("city_name")
    @Expose
    private String cityName;
    @SerializedName("jobma_catcher_otype")
    @Expose
    private String jobmaCatcherOtype;

    public Integer getJobmaCatcherId() {
        return jobmaCatcherId;
    }

    public void setJobmaCatcherId(Integer jobmaCatcherId) {
        this.jobmaCatcherId = jobmaCatcherId;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public void setEmployerEmail(String employerEmail) {
        this.employerEmail = employerEmail;
    }

    public String getCompanyEmail() {
        return companyEmail;
    }

    public void setCompanyEmail(String companyEmail) {
        this.companyEmail = companyEmail;
    }

    public String getOrganisation() {
        return organisation;
    }

    public void setOrganisation(String organisation) {
        this.organisation = organisation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getFax() {
        return fax;
    }

    public void setFax(String fax) {
        this.fax = fax;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getAboutCompany() {
        return aboutCompany;
    }

    public void setAboutCompany(String aboutCompany) {
        this.aboutCompany = aboutCompany;
    }

    public String getFunctionalArea() {
        return functionalArea;
    }

    public void setFunctionalArea(String functionalArea) {
        this.functionalArea = functionalArea;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getJobmaCatcherOtype() {
        return jobmaCatcherOtype;
    }

    public void setJobmaCatcherOtype(String jobmaCatcherOtype) {
        this.jobmaCatcherOtype = jobmaCatcherOtype;
    }
}
