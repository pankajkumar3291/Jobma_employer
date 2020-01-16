package com.jobma.employer.model.get_candidate_detail;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class JobName implements Serializable {

    @SerializedName("jobma_job_title")
    @Expose
    private String jobmaJobTitle;

    public String getJobmaJobTitle() {
        return jobmaJobTitle;
    }

    public void setJobmaJobTitle(String jobmaJobTitle) {
        this.jobmaJobTitle = jobmaJobTitle;
    }

}