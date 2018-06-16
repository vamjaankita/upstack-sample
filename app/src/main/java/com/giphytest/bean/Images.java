package com.giphytest.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class Images {

    @SerializedName("original") @Expose
    private Original original;


    /**
     * No args constructor for use in serialization
     */
    public Images() {
    }

    /**
     * @param fixedHeight
     */
    public Images(final Original fixedHeight) {
        this.original = fixedHeight;
    }

    /**
     * @return The fixedHeight
     */
    public Original getFixedHeight() {
        return original;
    }

    /**
     * @param original The fixed_height
     */
    public void setFixedHeight(final Original original) {
        this.original = original;
    }

}
