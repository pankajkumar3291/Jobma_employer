package com.jobma.employer.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobma.employer.model.companyProfile.EOVideoPath;

import java.io.Serializable;
import java.util.List;

public class EOProfileData implements Serializable {

    @SerializedName("pitcherData")
    @Expose
    private EOPitcherData pitcherData;
    @SerializedName("privacy")
    @Expose
    private EOPrivacyData privacy;
    @SerializedName("summary")
    @Expose
    private EOSummaryData summary;
    @SerializedName("Professional")
    @Expose
    private List<EOProfessionalData> professional = null;
    @SerializedName("keyskills")
    @Expose
    private List<EOKeySkillData> keyskills = null;
    @SerializedName("Certification")
    @Expose
    private List<EOCertificationData> certification = null;
    @SerializedName("education")
    @Expose
    private List<EOEducationData> education = null;
    @SerializedName("video_path")
    @Expose
    private EOVideoPath videoPath;
    @SerializedName("pitcherresume")
    @Expose
    private EOPitcherResume pitcherresume;

    public EOPitcherData getPitcherData() {
        return pitcherData;
    }

    public void setPitcherData(EOPitcherData pitcherData) {
        this.pitcherData = pitcherData;
    }

    public EOPrivacyData getPrivacy() {
        return privacy;
    }

    public void setPrivacy(EOPrivacyData privacy) {
        this.privacy = privacy;
    }

    public EOSummaryData getSummary() {
        return summary;
    }

    public void setSummary(EOSummaryData summary) {
        this.summary = summary;
    }

    public List<EOProfessionalData> getProfessional() {
        return professional;
    }

    public void setProfessional(List<EOProfessionalData> professional) {
        this.professional = professional;
    }

    public List<EOKeySkillData> getKeyskills() {
        return keyskills;
    }

    public void setKeyskills(List<EOKeySkillData> keyskills) {
        this.keyskills = keyskills;
    }

    public List<EOCertificationData> getCertification() {
        return certification;
    }

    public void setCertification(List<EOCertificationData> certification) {
        this.certification = certification;
    }

    public List<EOEducationData> getEducation() {
        return education;
    }

    public void setEducation(List<EOEducationData> education) {
        this.education = education;
    }

    public EOVideoPath getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(EOVideoPath videoPath) {
        this.videoPath = videoPath;
    }

    public EOPitcherResume getPitcherresume() {
        return pitcherresume;
    }

    public void setPitcherresume(EOPitcherResume pitcherresume) {
        this.pitcherresume = pitcherresume;
    }

}
