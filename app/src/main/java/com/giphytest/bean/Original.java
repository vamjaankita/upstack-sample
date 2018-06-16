package com.giphytest.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;



public class Original {

    @SerializedName("url") @Expose
    private String url;
    @SerializedName("mp4") @Expose
    private String videoUrl;

    /**
     * No args constructor for use in serialization
     */
    public Original() {
    }

    /**
     * @param url
     */
    public Original(final String url) {
        this.url = url;
    }

    /**
     * @return The url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url The url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return The mp4
     */
    public String getVideoUrl() {
        return videoUrl;
    }
    /**
     * @param 'mp4' The mp4
     */
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
}
