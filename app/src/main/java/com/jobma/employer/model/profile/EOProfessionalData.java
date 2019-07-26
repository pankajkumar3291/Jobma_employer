package com.jobma.employer.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOProfessionalData implements Serializable {

    @SerializedName("pro_id")
    @Expose
    private Integer proId;
    @SerializedName("pitcher_id")
    @Expose
    private String pitcherId;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("company")
    @Expose
    private String company;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("city")
    @Expose
    private Integer city;
    @SerializedName("state")
    @Expose
    private Integer state;
    @SerializedName("country")
    @Expose
    private Integer country;
    @SerializedName("detail")
    @Expose
    private String detail;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("state_name")
    @Expose
    private String stateName;
    @SerializedName("city_name")
    @Expose
    private String cityName;

    public Integer getProId() {
        return proId;
    }

    public void setProId(Integer proId) {
        this.proId = proId;
    }

    public String getPitcherId() {
        return pitcherId;
    }

    public void setPitcherId(String pitcherId) {
        this.pitcherId = pitcherId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
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

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
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
}
