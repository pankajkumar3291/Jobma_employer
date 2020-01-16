package com.jobma.employer.model.liveInterview;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOEncodedResponse implements Serializable {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("data")
    @Expose
    private EOEncodedResponseData data;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public EOEncodedResponseData getData() {
        return data;
    }

    public void setData(EOEncodedResponseData data) {
        this.data = data;
    }

}
