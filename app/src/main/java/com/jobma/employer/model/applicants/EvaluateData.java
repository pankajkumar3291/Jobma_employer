package com.jobma.employer.model.applicants;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EvaluateData {

    @SerializedName("total_count")
    @Expose
    private Integer totalCount;
    @SerializedName("remaining")
    @Expose
    private Integer remaining;
    @SerializedName("job_count")
    @Expose
    private Integer jobCount;
    @SerializedName("job_data")
    @Expose
    private List<JobDatum> jobData = null;

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

    public Integer getJobCount() {
        return jobCount;
    }

    public void setJobCount(Integer jobCount) {
        this.jobCount = jobCount;
    }

    public List<JobDatum> getJobData() {
        return jobData;
    }

    public void setJobData(List<JobDatum> jobData) {
        this.jobData = jobData;
    }

}