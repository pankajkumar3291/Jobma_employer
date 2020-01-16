package com.jobma.employer.model.applicants;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jobma.employer.model.applicants.Catcher;

public class JobDatum {

    @SerializedName("catcher")
    @Expose
    private Catcher catcher;
    @SerializedName("job_id")
    @Expose
    private Integer jobId;
    @SerializedName("job_title")
    @Expose
    private String jobTitle;
    @SerializedName("catcher_id")
    @Expose
    private Integer catcherId;
    @SerializedName("recruiter_name")
    @Expose
    private String recruiterName;
    @SerializedName("recruiter_email")
    @Expose
    private String recruiterEmail;
    @SerializedName("expiry_date")
    @Expose
    private String expiryDate;
    @SerializedName("apply_count")
    @Expose
    private Integer applyCount;
    @SerializedName("view_count")
    @Expose
    private Integer viewCount;
    @SerializedName("inactive_date")
    @Expose
    private String inactiveDate;
    @SerializedName("job_status")
    @Expose
    private String jobStatus;
    @SerializedName("approval")
    @Expose
    private String approval;
    @SerializedName("create")
    @Expose
    private String create;
    @SerializedName("update")
    @Expose
    private String update;
    @SerializedName("create_date")
    @Expose
    private String createDate;
    @SerializedName("applicants")
    @Expose
    private Integer applicants;
    @SerializedName("hold")
    @Expose
    private Integer hold;
    @SerializedName("selected")
    @Expose
    private Integer selected;
    @SerializedName("rejected")
    @Expose
    private Integer rejected;
    @SerializedName("invited")
    @Expose
    private Integer invited;
    @SerializedName("approval_status")
    @Expose
    private String approvalStatus;

    public Catcher getCatcher() {
        return catcher;
    }

    public void setCatcher(Catcher catcher) {
        this.catcher = catcher;
    }

    public Integer getJobId() {
        return jobId;
    }

    public void setJobId(Integer jobId) {
        this.jobId = jobId;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public Integer getCatcherId() {
        return catcherId;
    }

    public void setCatcherId(Integer catcherId) {
        this.catcherId = catcherId;
    }

    public String getRecruiterName() {
        return recruiterName;
    }

    public void setRecruiterName(String recruiterName) {
        this.recruiterName = recruiterName;
    }

    public String getRecruiterEmail() {
        return recruiterEmail;
    }

    public void setRecruiterEmail(String recruiterEmail) {
        this.recruiterEmail = recruiterEmail;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public Integer getApplyCount() {
        return applyCount;
    }

    public void setApplyCount(Integer applyCount) {
        this.applyCount = applyCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public String getInactiveDate() {
        return inactiveDate;
    }

    public void setInactiveDate(String inactiveDate) {
        this.inactiveDate = inactiveDate;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getApproval() {
        return approval;
    }

    public void setApproval(String approval) {
        this.approval = approval;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Integer getApplicants() {
        return applicants;
    }

    public void setApplicants(Integer applicants) {
        this.applicants = applicants;
    }

    public Integer getHold() {
        return hold;
    }

    public void setHold(Integer hold) {
        this.hold = hold;
    }

    public Integer getSelected() {
        return selected;
    }

    public void setSelected(Integer selected) {
        this.selected = selected;
    }

    public Integer getRejected() {
        return rejected;
    }

    public void setRejected(Integer rejected) {
        this.rejected = rejected;
    }

    public Integer getInvited() {
        return invited;
    }

    public void setInvited(Integer invited) {
        this.invited = invited;
    }

    public String getApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(String approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

}
