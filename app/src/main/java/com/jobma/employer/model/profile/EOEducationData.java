package com.jobma.employer.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOEducationData implements Serializable {

    @SerializedName("ed_id")
    @Expose
    private Integer edId;
    @SerializedName("pitcher_id")
    @Expose
    private Integer pitcherId;
    @SerializedName("degree")
    @Expose
    private String degree;
    @SerializedName("edu_level")
    @Expose
    private String eduLevel;
    @SerializedName("start_date")
    @Expose
    private String startDate;
    @SerializedName("end_date")
    @Expose
    private String endDate;
    @SerializedName("edu_institution")
    @Expose
    private String eduInstitution;
    @SerializedName("city")
    @Expose
    private Integer city;
    @SerializedName("state")
    @Expose
    private Integer state;
    @SerializedName("country")
    @Expose
    private Integer country;
    @SerializedName("edu_summary")
    @Expose
    private String eduSummary;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("state_name")
    @Expose
    private String stateName;
    @SerializedName("city_name")
    @Expose
    private String cityName;

    public Integer getEdId() {
        return edId;
    }

    public void setEdId(Integer edId) {
        this.edId = edId;
    }

    public Integer getPitcherId() {
        return pitcherId;
    }

    public void setPitcherId(Integer pitcherId) {
        this.pitcherId = pitcherId;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getEduLevel() {
        return eduLevel;
    }

    public void setEduLevel(String eduLevel) {
        this.eduLevel = eduLevel;
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

    public String getEduInstitution() {
        return eduInstitution;
    }

    public void setEduInstitution(String eduInstitution) {
        this.eduInstitution = eduInstitution;
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

    public String getEduSummary() {
        return eduSummary;
    }

    public void setEduSummary(String eduSummary) {
        this.eduSummary = eduSummary;
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
