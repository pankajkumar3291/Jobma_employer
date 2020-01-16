package com.jobma.employer.model.setupInterview;


import androidx.annotation.NonNull;

import java.io.Serializable;

public class EOCandidatesObject implements Serializable {

    private String fullName;
    private String emailId;
    private String phoneNumber;
    private int value = 0;

    public EOCandidatesObject(String name, String email, String phone) {
        this.fullName = name;
        this.emailId = email;
        this.phoneNumber = phone;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @NonNull
    @Override
    public String toString() {
        return this.fullName;
    }
}
