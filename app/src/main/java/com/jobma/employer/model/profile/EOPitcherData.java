package com.jobma.employer.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOPitcherData implements Serializable {

    @SerializedName("pitcher_id")
    @Expose
    private Integer pitcherId;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("jobma_pitcher_photo")
    @Expose
    private String jobmaPitcherPhoto;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("city")
    @Expose
    private Integer city;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("country")
    @Expose
    private Integer country;
    @SerializedName("state")
    @Expose
    private Integer state;
    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("state_name")
    @Expose
    private String stateName;
    @SerializedName("city_name")
    @Expose
    private String cityName;

    public Integer getPitcherId() {
        return pitcherId;
    }

    public void setPitcherId(Integer pitcherId) {
        this.pitcherId = pitcherId;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getJobmaPitcherPhoto() {
        return jobmaPitcherPhoto;
    }

    public void setJobmaPitcherPhoto(String jobmaPitcherPhoto) {
        this.jobmaPitcherPhoto = jobmaPitcherPhoto;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Integer getCity() {
        return city;
    }

    public void setCity(Integer city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
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
