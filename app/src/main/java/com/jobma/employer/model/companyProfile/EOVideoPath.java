package com.jobma.employer.model.companyProfile;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class EOVideoPath implements Serializable {

    @SerializedName("rtsp")
    @Expose
    private String rtsp;
    @SerializedName("hls")
    @Expose
    private String hls;
    @SerializedName("fileurl")
    @Expose
    private String fileurl;
    @SerializedName("poster")
    @Expose
    private String poster;
    @SerializedName("uploaded_file_url")
    @Expose
    private String uploaded_file_url;
    @SerializedName("dash")
    @Expose
    private String dash;

    public String getUploaded_file_url() {
        return uploaded_file_url;
    }

    public void setUploaded_file_url(String uploaded_file_url) {
        this.uploaded_file_url = uploaded_file_url;
    }

    public String getRtsp() {
        return rtsp;
    }

    public void setRtsp(String rtsp) {
        this.rtsp = rtsp;
    }

    public String getDash() {
        return dash;
    }

    public void setDash(String dash) {
        this.dash = dash;
    }

    public String getHls() {
        return hls;
    }

    public void setHls(String hls) {
        this.hls = hls;
    }

    public String getFileurl() {
        return fileurl;
    }

    public void setFileurl(String fileurl) {
        this.fileurl = fileurl;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
