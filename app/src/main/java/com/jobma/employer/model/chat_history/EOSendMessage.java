package com.jobma.employer.model.chat_history;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EOSendMessage {

    @SerializedName("error")
    @Expose
    private Integer error;
    @SerializedName("data")
    @Expose
    private EOChatData data;
    @SerializedName("message")
    @Expose
    private String message;

    public Integer getError() {
        return error;
    }

    public void setError(Integer error) {
        this.error = error;
    }

    public EOChatData getData() {
        return data;
    }

    public void setData(EOChatData data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}