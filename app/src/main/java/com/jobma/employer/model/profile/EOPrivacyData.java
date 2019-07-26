package com.jobma.employer.model.profile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOPrivacyData implements Serializable {

    @SerializedName("pitcher_id")
    @Expose
    private Integer pitcherId;
    @SerializedName("email_privacy")
    @Expose
    private String emailPrivacy;
    @SerializedName("alternate_mail_privacy")
    @Expose
    private String alternateMailPrivacy;
    @SerializedName("phone_privacy")
    @Expose
    private String phonePrivacy;
    @SerializedName("second_phone_privacy")
    @Expose
    private String secondPhonePrivacy;
    @SerializedName("facebook_privacy")
    @Expose
    private String facebookPrivacy;
    @SerializedName("linkedin_privacy")
    @Expose
    private String linkedinPrivacy;
    @SerializedName("twitter_privacy")
    @Expose
    private String twitterPrivacy;
    @SerializedName("website_privacy")
    @Expose
    private String websitePrivacy;
    @SerializedName("quora_privacy")
    @Expose
    private String quoraPrivacy;
    @SerializedName("social_web_privacy")
    @Expose
    private String socialWebPrivacy;
    @SerializedName("pdf_resume_privacy")
    @Expose
    private String pdfResumePrivacy;
    @SerializedName("video_resume1_privacy")
    @Expose
    private String videoResume1Privacy;
    @SerializedName("audio_resume_privacy")
    @Expose
    private String audioResumePrivacy;
    @SerializedName("sms_privacy")
    @Expose
    private String smsPrivacy;
    @SerializedName("email_alert_privacy")
    @Expose
    private String emailAlertPrivacy;

    public Integer getPitcherId() {
        return pitcherId;
    }

    public void setPitcherId(Integer pitcherId) {
        this.pitcherId = pitcherId;
    }

    public String getEmailPrivacy() {
        return emailPrivacy;
    }

    public void setEmailPrivacy(String emailPrivacy) {
        this.emailPrivacy = emailPrivacy;
    }

    public String getAlternateMailPrivacy() {
        return alternateMailPrivacy;
    }

    public void setAlternateMailPrivacy(String alternateMailPrivacy) {
        this.alternateMailPrivacy = alternateMailPrivacy;
    }

    public String getPhonePrivacy() {
        return phonePrivacy;
    }

    public void setPhonePrivacy(String phonePrivacy) {
        this.phonePrivacy = phonePrivacy;
    }

    public String getSecondPhonePrivacy() {
        return secondPhonePrivacy;
    }

    public void setSecondPhonePrivacy(String secondPhonePrivacy) {
        this.secondPhonePrivacy = secondPhonePrivacy;
    }

    public String getFacebookPrivacy() {
        return facebookPrivacy;
    }

    public void setFacebookPrivacy(String facebookPrivacy) {
        this.facebookPrivacy = facebookPrivacy;
    }

    public String getLinkedinPrivacy() {
        return linkedinPrivacy;
    }

    public void setLinkedinPrivacy(String linkedinPrivacy) {
        this.linkedinPrivacy = linkedinPrivacy;
    }

    public String getTwitterPrivacy() {
        return twitterPrivacy;
    }

    public void setTwitterPrivacy(String twitterPrivacy) {
        this.twitterPrivacy = twitterPrivacy;
    }

    public String getWebsitePrivacy() {
        return websitePrivacy;
    }

    public void setWebsitePrivacy(String websitePrivacy) {
        this.websitePrivacy = websitePrivacy;
    }

    public String getQuoraPrivacy() {
        return quoraPrivacy;
    }

    public void setQuoraPrivacy(String quoraPrivacy) {
        this.quoraPrivacy = quoraPrivacy;
    }

    public String getSocialWebPrivacy() {
        return socialWebPrivacy;
    }

    public void setSocialWebPrivacy(String socialWebPrivacy) {
        this.socialWebPrivacy = socialWebPrivacy;
    }

    public String getPdfResumePrivacy() {
        return pdfResumePrivacy;
    }

    public void setPdfResumePrivacy(String pdfResumePrivacy) {
        this.pdfResumePrivacy = pdfResumePrivacy;
    }

    public String getVideoResume1Privacy() {
        return videoResume1Privacy;
    }

    public void setVideoResume1Privacy(String videoResume1Privacy) {
        this.videoResume1Privacy = videoResume1Privacy;
    }

    public String getAudioResumePrivacy() {
        return audioResumePrivacy;
    }

    public void setAudioResumePrivacy(String audioResumePrivacy) {
        this.audioResumePrivacy = audioResumePrivacy;
    }

    public String getSmsPrivacy() {
        return smsPrivacy;
    }

    public void setSmsPrivacy(String smsPrivacy) {
        this.smsPrivacy = smsPrivacy;
    }

    public String getEmailAlertPrivacy() {
        return emailAlertPrivacy;
    }

    public void setEmailAlertPrivacy(String emailAlertPrivacy) {
        this.emailAlertPrivacy = emailAlertPrivacy;
    }

}
