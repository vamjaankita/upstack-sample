package com.giphytest.ui.controller;

import com.giphytest.bean.GiphyImageInfo;

import java.util.List;


public interface GiphyListView extends LoadDataView {

    void renderGiphyList(List<GiphyImageInfo> giphyImageInfoList);

    void viewGiphy(GiphyImageInfo giphyImageInfo);
}
