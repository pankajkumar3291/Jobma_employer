package com.jobma.employer.model.applicants;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Catcher {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("fname")
    @Expose
    private String fname;
    @SerializedName("lname")
    @Expose
    private String lname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("jobma_catcher_parent")
    @Expose
    private String jobmaCatcherParent;

    private boolean ischecked;

    public boolean isIschecked() {
        return ischecked;
    }

    public void setIschecked(boolean ischecked) {
        this.ischecked = ischecked;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getJobmaCatcherParent() {
        return jobmaCatcherParent;
    }

    public void setJobmaCatcherParent(String jobmaCatcherParent) {
        this.jobmaCatcherParent = jobmaCatcherParent;
    }

}