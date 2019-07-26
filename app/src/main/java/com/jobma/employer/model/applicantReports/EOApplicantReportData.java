package com.jobma.employer.model.applicantReports;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class EOApplicantReportData implements Serializable {

    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;
    @SerializedName("applied_data")
    @Expose
    private List<ApplicantsData> appliedData = null;

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

    public List<ApplicantsData> getAppliedData() {
        return appliedData;
    }

    public void setAppliedData(List<ApplicantsData> appliedData) {
        this.appliedData = appliedData;
    }
}
