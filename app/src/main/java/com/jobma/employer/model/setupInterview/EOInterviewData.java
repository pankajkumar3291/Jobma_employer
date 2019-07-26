package com.jobma.employer.model.setupInterview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOInterviewData implements Serializable {

    @SerializedName("jobma_job_post_id")
    @Expose
    private Integer jobmaJobPostId;
    @SerializedName("jobma_job_title")
    @Expose
    private String jobmaJobTitle;

    public Integer getJobmaJobPostId() {
        return jobmaJobPostId;
    }

    public void setJobmaJobPostId(Integer jobmaJobPostId) {
        this.jobmaJobPostId = jobmaJobPostId;
    }

    public String getJobmaJobTitle() {
        return jobmaJobTitle;
    }

    public void setJobmaJobTitle(String jobmaJobTitle) {
        this.jobmaJobTitle = jobmaJobTitle;
    }

}
