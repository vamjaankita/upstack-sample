package com.giphytest.ui.controller;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.giphytest.App;
import com.giphytest.R;
import com.giphytest.bean.GiphyImageInfo;
import com.giphytest.ui.base.ActionBarProvider;
import com.giphytest.ui.base.BaseController;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.decoder.DecoderCounters;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlaybackControlView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;

import butterknife.BindView;
import butterknife.OnClick;
import io.objectbox.Box;


public class VideoPlayerController extends BaseController implements ExoPlayer.EventListener, SimpleExoPlayer.VideoListener, SurfaceHolder.Callback {

    public static String ARG = "GiphyImageInfo";

    @BindView(R.id.surface_view)
    SurfaceView mSurfaceView;

    @BindView(R.id.txtUpVote)
    TextView txtUpVote;

    @BindView(R.id.txtDownVote)
    TextView txtDownVote;

    @BindView(R.id.btnShare)
    LinearLayout btnShare;


    @BindView(R.id.aspect_ratio_layout)
    AspectRatioFrameLayout mAspectRatioLayout;

    @BindView(R.id.player_view)
    PlaybackControlView mPlaybackControlView;

    private SimpleExoPlayer mPlayer;
    private Box<GiphyImageInfo> giphyImageInfoBox;
    GiphyImageInfo giphyImageInfo;

    public VideoPlayerController(Bundle args) {
        super(args);
        setHasOptionsMenu(true);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        (((ActionBarProvider) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.player_controller, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        giphyImageInfoBox = ((App) getApplicationContext()).getBoxStore().boxFor(GiphyImageInfo.class);
        giphyImageInfo = (GiphyImageInfo) getArgs().getSerializable(ARG);
        stetUpSurface();
    }

    @OnClick(R.id.btnUpVote)
    void onUpVoteButtonClick() {
        if (giphyImageInfo.getTotalUpVote() != null) {
            giphyImageInfo.setTotalUpVote("" + (Integer.parseInt(giphyImageInfo.getTotalUpVote()) + 1));
            txtUpVote.setText(giphyImageInfo.getTotalUpVote());
            giphyImageInfoBox.put(giphyImageInfo);
        }
    }

    @OnClick(R.id.btnDownVote)
    void onDownVoteButtonClick() {
        if (giphyImageInfo.getTotalDownVote() != null) {
            giphyImageInfo.setTotalDownVote("" + (Integer.parseInt(giphyImageInfo.getTotalDownVote()) + 1));
            txtDownVote.setText(giphyImageInfo.getTotalDownVote());
            giphyImageInfoBox.put(giphyImageInfo);
        }
    }

    @OnClick(R.id.btnShare)
    void onShareButtonClick() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.putExtra(Intent.EXTRA_SUBJECT, "Giphy Video");
        share.putExtra(Intent.EXTRA_TEXT, giphyImageInfo.getVideoUrl());

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    @Override
    protected String getTitle() {
        return "Player";
    }

    private void stetUpSurface() {
        txtUpVote.setText(giphyImageInfo.getTotalUpVote());
        txtDownVote.setText(giphyImageInfo.getTotalDownVote());
        mSurfaceView.getHolder().addCallback(this);
        Handler handler = new Handler();
        ExtractorsFactory extractor = new DefaultExtractorsFactory();
        DefaultHttpDataSourceFactory dataSourceFactory = new DefaultHttpDataSourceFactory("ExoPlayer Demo");
        mPlayer = ExoPlayerFactory.newSimpleInstance(
                getActivity(),
                new DefaultTrackSelector(handler),
                new DefaultLoadControl()
        );
        mPlaybackControlView.requestFocus();
        mPlaybackControlView.setPlayer(mPlayer);
        // initialize source
        MediaSource videoSource = new ExtractorMediaSource(
                Uri.parse(giphyImageInfo.getVideoUrl()),
                dataSourceFactory,
                extractor,
                null,
                null
        );
        mPlayer.prepare(videoSource);
        mPlayer.setPlayWhenReady(true);
        mPlayer.addListener(this);
        mPlayer.setVideoListener(this);

    }

    @Override
    public void onVideoSizeChanged(int width, int height, int unappliedRotationDegrees, float pixelWidthHeightRatio) {
        // mAspectRatioLayout.setAspectRatio(pixelWidthHeightRatio);
    }

    @Override
    public void onRenderedFirstFrame(Surface surface) {

    }

    @Override
    public void onVideoDisabled(DecoderCounters counters) {

    }

    // ExoPlayer.EventListener
    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }
    // SurfaceHolder.Callback

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        if (mPlayer != null) {
            mPlayer.setVideoSurfaceHolder(surfaceHolder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            getRouter().popController(this);
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        if (mPlayer != null) {
            mPlayer.setVideoSurfaceHolder(null);
        }
    }

    @Override
    protected void onActivityPaused(@NonNull Activity activity) {
        super.onActivityPaused(activity);
        mPlayer.setPlayWhenReady(false);
    }

    @Override
    protected void onActivityStopped(@NonNull Activity activity) {
        super.onActivityStopped(activity);
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
