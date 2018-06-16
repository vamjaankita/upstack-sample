package com.giphytest.ui.controller;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bluelinelabs.conductor.RouterTransaction;
import com.giphytest.App;
import com.giphytest.R;
import com.giphytest.bean.GiphyImageInfo;
import com.giphytest.bean.GiphyImageInfo_;
import com.giphytest.rest.GiphyService;
import com.giphytest.ui.adapter.GiphyAdapter;
import com.giphytest.ui.adapter.ItemOffsetDecoration;
import com.giphytest.ui.base.ActionBarProvider;
import com.giphytest.ui.base.BaseController;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.objectbox.Box;
import io.objectbox.query.Query;


public class MainController extends BaseController implements GiphyListView, SwipeRefreshLayout.OnRefreshListener {

    private static final int PORTRAIT_COLUMNS = 2;

    @BindView(R.id.rv_users)
    RecyclerView rv_users;

    @BindView(R.id.rl_progress)
    RelativeLayout rl_progress;

    @BindView(R.id.rl_retry)
    RelativeLayout rl_retry;

    @BindView(R.id.bt_retry)
    Button bt_retry;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private ItemOffsetDecoration itemOffsetDecoration;
    String searchGifs = "Search Gifs";

    boolean hasSearched;

    UserListPresenter userListPresenter;

    GiphyAdapter usersAdapter;
    private Box<GiphyImageInfo> giphyImageInfoBox;
    private Query<GiphyImageInfo> giphyImageInfoQuery;
    MenuItem searchMenuItem;

    public MainController() {
        setHasOptionsMenu(true);
    }

    @Override
    protected void onAttach(@NonNull View view) {
        super.onAttach(view);
        (((ActionBarProvider) getActivity()).getSupportActionBar()).show();
        (((ActionBarProvider) getActivity()).getSupportActionBar()).setDisplayHomeAsUpEnabled(false);
    }

    @NonNull
    @Override
    protected View inflateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup container) {
        return inflater.inflate(R.layout.giphylist_controller, container, false);
    }

    @Override
    protected void onViewBound(@NonNull View view) {
        super.onViewBound(view);
        giphyImageInfoBox = ((App) getApplicationContext()).getBoxStore().boxFor(GiphyImageInfo.class);
        giphyImageInfoQuery = giphyImageInfoBox.query().order(GiphyImageInfo_.id).build();
        usersAdapter = new GiphyAdapter(giphyImageInfoBox);
        userListPresenter = new UserListPresenter(giphyImageInfoBox, giphyImageInfoQuery);
        itemOffsetDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.gif_adapter_item_offset);
        this.userListPresenter.setView(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        setupRecyclerView();
        this.loadGiphyList();
    }

    @Override
    protected String getTitle() {
        return "Search Video";
    }

    @OnClick(R.id.bt_retry)
    void onRetryButtonClick() {
        this.loadGiphyList();
    }

    private void loadGiphyList() {
        this.userListPresenter.initialize();
    }

    @Override
    public void showLoading() {
        mSwipeRefreshLayout.setRefreshing(true);
        this.getActivity().setProgressBarIndeterminateVisibility(true);
    }

    @Override
    public void hideLoading() {
        mSwipeRefreshLayout.setRefreshing(false);
        this.rl_progress.setVisibility(View.GONE);
        this.getActivity().setProgressBarIndeterminateVisibility(false);
    }

    @Override
    public void showRetry() {
        this.rl_retry.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideRetry() {
        this.rl_retry.setVisibility(View.GONE);
    }

    @Override
    public void showError(String message) {
        mSwipeRefreshLayout.setRefreshing(true);
        this.showToastMessage(message);
    }

    @Override
    public Context context() {
        return getActivity();
    }

    private void setupRecyclerView() {
        this.usersAdapter.setOnItemClickListener(onItemClickListener);
        if (rv_users != null) {
            this.rv_users.setLayoutManager(new GridLayoutManager(getActivity(), PORTRAIT_COLUMNS));
            this.rv_users.addItemDecoration(itemOffsetDecoration);
            this.rv_users.setItemAnimator(new DefaultItemAnimator());
            this.rv_users.getRecycledViewPool().setMaxRecycledViews(0, PORTRAIT_COLUMNS + PORTRAIT_COLUMNS);
            this.rv_users.setHasFixedSize(true);
            this.rv_users.setItemViewCacheSize(GiphyService.DEFAULT_RESULTS_COUNT);
            this.rv_users.setDrawingCacheEnabled(true);
            this.rv_users.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
            this.rv_users.setAdapter(usersAdapter);
        }
    }

    protected void showToastMessage(String message) {
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    private GiphyAdapter.OnItemClickListener onItemClickListener = new GiphyAdapter.OnItemClickListener() {
        @Override
        public void onUserItemClicked(GiphyImageInfo giphyImageInfo) {
            if (userListPresenter != null && giphyImageInfo != null) {
                userListPresenter.onUserClicked(giphyImageInfo);
            }
        }
    };

    @Override
    public void renderGiphyList(List<GiphyImageInfo> giphyImageInfoList) {
        if (giphyImageInfoList != null) {
            this.usersAdapter.clear();
            mSwipeRefreshLayout.setRefreshing(false);
            this.usersAdapter.add(giphyImageInfoList);
        }
    }

    @Override
    public void viewGiphy(GiphyImageInfo giphyImageInfo) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(VideoPlayerController.ARG, giphyImageInfo);
        getRouter().pushController(RouterTransaction.with(new VideoPlayerController(bundle)));
    }


    @Override
    public void onCreateOptionsMenu(final Menu menu, final MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu_fragment_main, menu);

        searchMenuItem = menu.findItem(R.id.menu_search);

        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchView.setQueryHint(searchGifs);

        // Set contextual action on search icon click
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(final MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(final MenuItem item) {
                // When search is closed, go back to trending results
                if (hasSearched) {
                    loadGiphyList();
                    hasSearched = false;
                }
                return true;
            }
        });

        // Query listener for search bar
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextChange(final String newText) {
                // Search on type

                return false;
            }

            @Override
            public boolean onQueryTextSubmit(final String query) {
                if (!TextUtils.isEmpty(query)) {
                    loadSearchImages(query);
                    hasSearched = true;
                }
                return false;
            }
        });
    }


    private void loadSearchImages(String searchString) {
        this.userListPresenter.searchgiphy(searchString);
    }

    @Override
    public void onRefresh() {
        this.loadGiphyList();
        searchMenuItem.collapseActionView();
    }
}
