package com.jobma.employer.model.chat_history;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("jobma_catcher_id")
    @Expose
    private Integer jobmaCatcherId;
    @SerializedName("jobma_catcher_fname")
    @Expose
    private String jobmaCatcherFname;

    public Integer getJobmaCatcherId() {
        return jobmaCatcherId;
    }

    public void setJobmaCatcherId(Integer jobmaCatcherId) {
        this.jobmaCatcherId = jobmaCatcherId;
    }

    public String getJobmaCatcherFname() {
        return jobmaCatcherFname;
    }

    public void setJobmaCatcherFname(String jobmaCatcherFname) {
        this.jobmaCatcherFname = jobmaCatcherFname;
    }
}