/*
 * Copyright (c) 2016. Self Training Systems, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by TienNguyen <tien.workinfo@gmail.com - tien.workinfo@icloud.com>, October 2015
 */

package com.training.tiennguyen.new_york_times.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.training.tiennguyen.new_york_times.R;
import com.training.tiennguyen.new_york_times.models.ArticlesObject;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * ArticlesAdapter
 *
 * @author Created by TienVNguyen on 20/03/2016.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ArticlesHolder> {
    private ArrayList<ArticlesObject> responses;
    private Context context;

    public ArticlesAdapter(ArrayList<ArticlesObject> responses, Context context) {
        this.responses = responses;
        this.context = context;
    }

    /**
     * Called when RecyclerView needs a new {@link ArticlesHolder} of the given type to represent
     * an item.
     * <p/>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p/>
     * The new ViewHolder will be used to display items of the adapter using
     * {@link #onBindViewHolder(ArticlesHolder, int)}. Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     */
    @Override
    public ArticlesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(R.layout.holder_articles_list, parent, false);

        ArticlesHolder articlesHolder = new ArticlesHolder(contactView);
        return articlesHolder;
    }

    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the {@link ArticlesHolder#itemView} to reflect the item at the given
     * position.
     * <p/>
     * Note that unlike {@link ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use {@link ArticlesHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p/>
     * Override {@link #onBindViewHolder(ArticlesHolder, int)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(ArticlesHolder holder, int position) {
        ArticlesObject object = responses.get(position);

        ImageView imgView = holder.imgImage;
        TextView textView = holder.txtName;

        if (object.getMultimedia() != null && object.getMultimedia().size() > 0) {
            Glide.with(context).load("http://www.nytimes.com/" + object.getMultimedia().get(0).getUrl()).into(imgView);
            imgView.setVisibility(View.VISIBLE);
        } else {
            imgView.setVisibility(View.GONE);
        }
        textView.setText(object.getHeadline().get("main").toString());
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return responses.size();
    }

    /**
     * Provide a direct reference to each of the views within a data item
     */
    public static class ArticlesHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.imgImage)
        protected ImageView imgImage;
        @Bind(R.id.txtText)
        protected TextView txtName;

        public ArticlesHolder(View itemView) {
            super(itemView);

            ButterKnife.bind(this, itemView);
        }
    }

    public void clear() {
        responses.clear();
        notifyDataSetChanged();
    }

    public void addAll(ArrayList<ArticlesObject> articlesObjects) {
        responses.addAll(articlesObjects);
        notifyDataSetChanged();
    }
}
