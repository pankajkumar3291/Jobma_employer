package com.jobma.employer.model.chat_history;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Title {

    @SerializedName("jobma_ticket_id")
    @Expose
    private String jobmaTicketId;
    @SerializedName("subject")
    @Expose
    private String subject;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("issue_status")
    @Expose
    private String issueStatus;

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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIssueStatus() {
        return issueStatus;
    }

    public void setIssueStatus(String issueStatus) {
        this.issueStatus = issueStatus;
    }

}
