/*
 * Copyright (c) 2016. Self Training Systems, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by TienNguyen <tien.workinfo@gmail.com - tien.workinfo@icloud.com>, October 2015
 */

package com.training.tiennguyen.new_york_times.uis;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.training.tiennguyen.new_york_times.R;
import com.training.tiennguyen.new_york_times.utils.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ArticleDetailsActivity extends AppCompatActivity {
    @Bind(R.id.imgArticle)
    protected ImageView imageView;
    @Bind(R.id.headerArticle)
    protected TextView textView;
    @Bind(R.id.webArticle)
    protected WebView webView;
    private ShareActionProvider miShareAction;
    private Intent shareIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_details);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        String headline_main = intent.getStringExtra(Constants.HEADLINE_MAIN);
        String web_url = intent.getStringExtra(Constants.WEB_URL);
        String multimedia = intent.getStringExtra(Constants.MULTIMEDIA);
        //ArticlesObject object = (ArticlesObject) intent.getSerializableExtra(Constants.ARTICLES_DETAILS);
        //ArticlesObject object = (ArticlesObject) Parcels.unwrap(intent.getParcelableExtra(Constants.ARTICLES_DETAILS));

        textView.setText(headline_main);
        if (multimedia != null && multimedia.length() > 0) {
            Glide.with(this).load(multimedia).into(imageView);
            imageView.setVisibility(View.VISIBLE);
        } else {
            imageView.setVisibility(View.GONE);
        }
        webView.setWebViewClient(new WebViewClient() {
            /**
             * Give the host application a chance to take over the control when a new
             * url is about to be loaded in the current WebView. If WebViewClient is not
             * provided, by default WebView will ask Activity Manager to choose the
             * proper handler for the url. If WebViewClient is provided, return true
             * means the host application handles the url, while return false means the
             * current WebView handles the url.
             * This method is not called for requests using the POST "method".
             *
             * @param view The WebView that is initiating the callback.
             * @param url  The url to be loaded.
             * @return True if the host application wants to leave the current WebView
             * and handle the url itself, otherwise return false.
             */
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
        webView.loadUrl(web_url);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_article_details, menu);
        MenuItem item = menu.findItem(R.id.menu_item_share);
        final ShareActionProvider miShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, webView.getUrl());
        miShare.setShareIntent(shareIntent);
        return true;
    }
}
