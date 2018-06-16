package com.giphytest.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.giphytest.R;
import com.giphytest.bean.GiphyImageInfo;
import com.giphytest.ui.controller.MainController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.objectbox.Box;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifDrawableBuilder;
import pl.droidsonroids.gif.GifImageView;


public class GiphyAdapter extends RecyclerView.Adapter<GiphyAdapter.GiphyAdapterViewHolder> {

    static final String TAG = MainController.class.getSimpleName();
    private static final int GIF_IMAGE_HEIGHT_PIXELS = 128;
    private static final int GIF_IMAGE_WIDTH_PIXELS = GIF_IMAGE_HEIGHT_PIXELS;
    private List<GiphyImageInfo> data = new ArrayList<>();
    private OnItemClickListener onItemClickListener;
    private Box<GiphyImageInfo> giphyImageInfoBox;

    public GiphyAdapter(Box<GiphyImageInfo> giphyImageInfoBox) {
        this.giphyImageInfoBox = giphyImageInfoBox;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public GiphyAdapterViewHolder onCreateViewHolder(final ViewGroup parent, final int position) {
        return new GiphyAdapterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.giphy_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(final GiphyAdapterViewHolder holder, final int position) {
        final Context context = holder.gifImageView.getContext();
        final GiphyImageInfo model = getItem(position);

        holder.txtUpVote.setText(model.getTotalUpVote());
        holder.txtDownVote.setText(model.getTotalDownVote());

        Glide.with(context)
                .load(model.getUrl())
                .asGif()
                .toBytes()
                .thumbnail(0.1f)
                .override(GIF_IMAGE_WIDTH_PIXELS, GIF_IMAGE_HEIGHT_PIXELS)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .error(R.mipmap.ic_launcher)
                .into(new SimpleTarget<byte[]>() {
                    @Override
                    public void onResourceReady(final byte[] resource,
                                                final GlideAnimation<? super byte[]> glideAnimation) {
                        // Load gif
                        final GifDrawable gifDrawable;
                        try {
                            gifDrawable = new GifDrawableBuilder().from(resource).build();
                            gifDrawable.setCornerRadius(15);
                            holder.gifImageView.setBackground(gifDrawable);
                        } catch (final IOException e) {
                            holder.gifImageView.setImageResource(R.mipmap.ic_launcher);
                        }
                        holder.gifImageView.setVisibility(View.VISIBLE);

                        // Turn off progressbar
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        if (Log.isLoggable(TAG, Log.INFO)) {
                            Log.i(TAG, "finished loading\t" + model);
                        }
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onUserItemClicked(model);
            }
        });

        holder.btnUpVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getTotalUpVote() != null) {
                    model.setTotalUpVote("" + (Integer.parseInt(model.getTotalUpVote()) + 1));
                    holder.txtUpVote.setText(model.getTotalUpVote());
                    giphyImageInfoBox.put(model);
                }
            }
        });

        holder.btnDownVote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (model.getTotalDownVote() != null) {
                    model.setTotalDownVote("" + (Integer.parseInt(model.getTotalDownVote()) + 1));
                    holder.txtDownVote.setText(model.getTotalDownVote());
                    giphyImageInfoBox.put(model);
                }
            }
        });
    }

    @Override
    public void onViewRecycled(final GiphyAdapterViewHolder holder) {
        super.onViewRecycled(holder);

        Glide.clear(holder.gifImageView);
        holder.gifImageView.setImageDrawable(null);
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public List<GiphyImageInfo> getList() {
        return data;
    }

    public GiphyImageInfo getItem(final int location) {
        return data.get(location);
    }

    public int getLocation(final GiphyImageInfo object) {
        return data.indexOf(object);
    }

    public void clear() {
        int size = data.size();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                data.remove(0);
            }

            notifyItemRangeRemoved(0, size);
        }
    }

    public boolean add(final List<GiphyImageInfo> giphyImageInfoList) {
        final boolean added = data.addAll(giphyImageInfoList);
        notifyDataSetChanged();
        return added;
    }

    public interface OnItemClickListener {
        void onUserItemClicked(GiphyImageInfo giphyImageInfo);
    }

    public class GiphyAdapterViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.gif_progress)
        ProgressBar progressBar;

        @BindView(R.id.gif_image)
        GifImageView gifImageView;

        @BindView(R.id.txtUpVote)
        TextView txtUpVote;

        @BindView(R.id.txtDownVote)
        TextView txtDownVote;

        @BindView(R.id.btnUpVote)
        LinearLayout btnUpVote;

        @BindView(R.id.btnDownVote)
        LinearLayout btnDownVote;

        GiphyAdapterViewHolder(final View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
