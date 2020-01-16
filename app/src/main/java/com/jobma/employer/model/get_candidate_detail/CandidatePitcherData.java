package com.jobma.employer.model.get_candidate_detail;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
public class CandidatePitcherData {
    @SerializedName("jobma_pitcher_id")
    @Expose
    private Integer jobmaPitcherId;
    @SerializedName("jobma_pitcher_url")
    @Expose
    private String jobmaPitcherUrl;
    @SerializedName("jobma_pitcher_fname")
    @Expose
    private String jobmaPitcherFname;
    @SerializedName("jobma_pitcher_lname")
    @Expose
    private String jobmaPitcherLname;
    @SerializedName("jobma_pitcher_email")
    @Expose
    private String jobmaPitcherEmail;
    @SerializedName("jobma_pitcher_phone")
    @Expose
    private String jobmaPitcherPhone;
    @SerializedName("jobma_pitcher_photo")
    @Expose
    private String jobmaPitcherPhoto;
    @SerializedName("jobma_current_deg")
    @Expose
    private String jobmaCurrentDeg;

    public Integer getJobmaPitcherId() {
        return jobmaPitcherId;
    }

    public void setJobmaPitcherId(Integer jobmaPitcherId) {
        this.jobmaPitcherId = jobmaPitcherId;
    }

    public String getJobmaPitcherUrl() {
        return jobmaPitcherUrl;
    }

    public void setJobmaPitcherUrl(String jobmaPitcherUrl) {
        this.jobmaPitcherUrl = jobmaPitcherUrl;
    }

    public String getJobmaPitcherFname() {
        return jobmaPitcherFname;
    }

    public void setJobmaPitcherFname(String jobmaPitcherFname) {
        this.jobmaPitcherFname = jobmaPitcherFname;
    }

    public String getJobmaPitcherLname() {
        return jobmaPitcherLname;
    }

    public void setJobmaPitcherLname(String jobmaPitcherLname) {
        this.jobmaPitcherLname = jobmaPitcherLname;
    }

    public String getJobmaPitcherEmail() {
        return jobmaPitcherEmail;
    }

    public void setJobmaPitcherEmail(String jobmaPitcherEmail) {
        this.jobmaPitcherEmail = jobmaPitcherEmail;
    }

    public String getJobmaPitcherPhone() {
        return jobmaPitcherPhone;
    }

    public void setJobmaPitcherPhone(String jobmaPitcherPhone) {
        this.jobmaPitcherPhone = jobmaPitcherPhone;
    }

    public String getJobmaPitcherPhoto() {
        return jobmaPitcherPhoto;
    }

    public void setJobmaPitcherPhoto(String jobmaPitcherPhoto) {
        this.jobmaPitcherPhoto = jobmaPitcherPhoto;
    }

    public String getJobmaCurrentDeg() {
        return jobmaCurrentDeg;
    }

    public void setJobmaCurrentDeg(String jobmaCurrentDeg) {
        this.jobmaCurrentDeg = jobmaCurrentDeg;
    }



}
