package com.jobma.employer.model.reportIssue;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOReportList implements Serializable {

    @SerializedName("jobma_contact_id")
    @Expose
    private Integer jobmaContactId;
    @SerializedName("jobma_ticket_id")
    @Expose
    private String jobmaTicketId;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("issue_status")
    @Expose
    private String issueStatus;
    @SerializedName("created_at")
    @Expose
    private String createdAt;

    public Integer getJobmaContactId() {
        return jobmaContactId;
    }

    public void setJobmaContactId(Integer jobmaContactId) {
        this.jobmaContactId = jobmaContactId;
    }

    public String getJobmaTicketId() {
        return jobmaTicketId;
    }

    public void setJobmaTicketId(String jobmaTicketId) {
        this.jobmaTicketId = jobmaTicketId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

}