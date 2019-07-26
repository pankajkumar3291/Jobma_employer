package com.jobma.employer.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOPitcherResume implements Serializable {

    @SerializedName("jobma_pitcher_id")
    @Expose
    private Integer jobmaPitcherId;
    @SerializedName("jobma_pitcher_pdf_resume")
    @Expose
    private String jobmaPitcherPdfResume;

    public Integer getJobmaPitcherId() {
        return jobmaPitcherId;
    }

    public void setJobmaPitcherId(Integer jobmaPitcherId) {
        this.jobmaPitcherId = jobmaPitcherId;
    }

    public String getJobmaPitcherPdfResume() {
        return jobmaPitcherPdfResume;
    }

    public void setJobmaPitcherPdfResume(String jobmaPitcherPdfResume) {
        this.jobmaPitcherPdfResume = jobmaPitcherPdfResume;
    }

}
