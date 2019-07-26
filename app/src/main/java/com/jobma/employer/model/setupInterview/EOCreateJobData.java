package com.jobma.employer.model.setupInterview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOCreateJobData implements Serializable {

    @SerializedName("job_id")
    @Expose
    private Integer jobId;

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

}
