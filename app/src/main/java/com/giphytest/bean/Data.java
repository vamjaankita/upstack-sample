package com.giphytest.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Data {

    @SerializedName("id") @Expose
    private String id;

    @SerializedName("images") @Expose
    private Images images;

    /**
     * No args constructor for use in serialization
     */
    public Data() {
    }

    public Data(final Images images) {
        this.images = images;
    }

    /**
     * @return The images
     */
    public Images getImages() {
        return images;
    }

    /**
     * @param images The images
     */
    public void setImages(final Images images) {
        this.images = images;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
