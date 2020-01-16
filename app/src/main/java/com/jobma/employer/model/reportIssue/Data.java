package com.jobma.employer.model.reportIssue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {

    @SerializedName("total_count")
    @Expose
    private Integer totalCount;

    @SerializedName("information")
    @Expose
    private String information;

    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    @SerializedName("remaining")
    @Expose
    private Integer remaining;
    @SerializedName("getUserList")
    @Expose
    private List<GetUserList> getUserList = null;

    @SerializedName("report_list")
    @Expose
    private List<EOReportList> reportList = null;

    public List<EOReportList> getReportList() {
        return reportList;
    }

    public void setReportList(List<EOReportList> reportList) {
        this.reportList = reportList;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public Integer getRemaining() {
        return remaining;
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    public List<GetUserList> getGetUserList() {
        return getUserList;
    }

    public void setGetUserList(List<GetUserList> getUserList) {
        this.getUserList = getUserList;
    }

}