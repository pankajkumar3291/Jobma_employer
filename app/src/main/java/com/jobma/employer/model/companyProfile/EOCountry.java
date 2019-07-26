package com.jobma.employer.model.companyProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOCountry implements Serializable {

    @SerializedName("data")
    @Expose
    private List<EOCountryData> data = null;
    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("message")
    @Expose
    private String message;

    public List<EOCountryData> getData() {
        return data;
    }

    public void setData(List<EOCountryData> data) {
        this.data = data;
    }

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
