package com.giphytest.rest;

import com.giphytest.bean.Data;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;



public class GiphyResponse {
    @SerializedName("data") @Expose
    private List<Data> data = new ArrayList<>();
    public GiphyResponse() {
    }

    /**
     * @param data
     */
    public GiphyResponse(final List<Data> data) {
        this.data = data;
    }

    /**
     * @return The data
     */
    public List<Data> getData() {
        return data;
    }

    /**
     * @param data The data
     */
    public void setData(final List<Data> data) {
        this.data = data;
    }
}
