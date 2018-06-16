package com.giphytest.ui.controller;

import android.support.annotation.NonNull;

import com.giphytest.Presenter.Presenter;
import com.giphytest.bean.Data;
import com.giphytest.bean.GiphyImageInfo;
import com.giphytest.rest.GiphyResponse;
import com.giphytest.rest.GiphyService;

import java.util.List;

import io.objectbox.Box;
import io.objectbox.query.Query;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


public class UserListPresenter implements Presenter {

    private GiphyListView viewListView;
    List<GiphyImageInfo> giphyImageInfoList;
    private Box<GiphyImageInfo> giphyImageInfoBox;
    private Query<GiphyImageInfo> giphyImageInfoQuery;

    public UserListPresenter(Box<GiphyImageInfo> giphyImageInfoBox, Query<GiphyImageInfo> giphyImageInfoQuery) {
        this.giphyImageInfoBox = giphyImageInfoBox;
        this.giphyImageInfoQuery = giphyImageInfoQuery;
        giphyImageInfoList = this.giphyImageInfoQuery.find();
    }

    public void setView(@NonNull GiphyListView view) {
        this.viewListView = view;
    }

    @Override
    public void resume() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void destroy() {
        this.viewListView = null;
    }

    void initialize() {
        this.loadGiphyList();
    }

    private void loadGiphyList() {
        this.hideViewRetry();
        this.showViewLoading();
        Observable<GiphyResponse> execute = this.getGiphyList();

        execute.subscribe(new Consumer<GiphyResponse>() {
            @Override
            public void accept(GiphyResponse giphyResponse) throws Exception {
                onDataManipulate(giphyResponse);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                UserListPresenter.this.hideViewLoading();
                UserListPresenter.this.showViewRetry();
            }
        });
    }

    void onUserClicked(GiphyImageInfo giphyImageInfo) {
        this.viewListView.viewGiphy(giphyImageInfo);
    }

    private void showViewLoading() {
        this.viewListView.showLoading();
    }

    private void hideViewLoading() {
        this.viewListView.hideLoading();
    }

    private void showViewRetry() {
        this.viewListView.showRetry();
    }

    private void hideViewRetry() {
        this.viewListView.hideRetry();
    }

    private Observable<GiphyResponse> getGiphyList() {
        return GiphyService.getInstance().getTrendingResults().share()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<GiphyResponse> getGiphyBySearchList(String searchString) {
        return GiphyService.getInstance().getSearchResults(searchString).share()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public void searchgiphy(String searchString) {
        Observable<GiphyResponse> execute = this.getGiphyBySearchList(searchString);
        execute.subscribe(new Consumer<GiphyResponse>() {
            @Override
            public void accept(GiphyResponse giphyResponse) throws Exception {
                giphyImageInfoList.clear();
                for (final Data datum : giphyResponse.getData()) {
                    String id = datum.getId();
                    String url = datum.getImages().getFixedHeight().getUrl();
                    String videoUrl = datum.getImages().getFixedHeight().getVideoUrl();

                    GiphyImageInfo giphyImageInfo = new GiphyImageInfo();
                    giphyImageInfo.setGiphyId(id);
                    giphyImageInfo.setUrl(url);
                    giphyImageInfo.setVideoUrl(videoUrl);
                    giphyImageInfo.setTotalUpVote("0");
                    giphyImageInfo.setTotalDownVote("0");
                    giphyImageInfoList.add(giphyImageInfo);
                }

                UserListPresenter.this.hideViewLoading();
                UserListPresenter.this.viewListView.renderGiphyList(giphyImageInfoList);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                UserListPresenter.this.hideViewLoading();
                UserListPresenter.this.showViewRetry();
            }
        });
    }

    private void onDataManipulate(GiphyResponse giphyResponse) {
        for (final Data datum : giphyResponse.getData()) {
            String id = datum.getId();
            String url = datum.getImages().getFixedHeight().getUrl();
            String videoUrl = datum.getImages().getFixedHeight().getVideoUrl();

            boolean isGiphyExist = false;
            if (giphyImageInfoList != null && giphyImageInfoList.size() > 0) {
                for (int i = 0; i < giphyImageInfoList.size(); i++) {
                    if (id.equals(giphyImageInfoList.get(i).getGiphyId())) {
                        isGiphyExist = true;
                        break;
                    }
                }
            }

            if (!isGiphyExist) {
                GiphyImageInfo giphyImageInfo = new GiphyImageInfo();
                giphyImageInfo.setGiphyId(id);
                giphyImageInfo.setUrl(url);
                giphyImageInfo.setVideoUrl(videoUrl);
                giphyImageInfo.setTotalUpVote("0");
                giphyImageInfo.setTotalDownVote("0");
                giphyImageInfoBox.put(giphyImageInfo);
            }
        }
        giphyImageInfoList = this.giphyImageInfoQuery.find();
        UserListPresenter.this.hideViewLoading();
        UserListPresenter.this.viewListView.renderGiphyList(giphyImageInfoList);
    }
}
