/*
 * Copyright (c) 2016. Self Training Systems, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by TienNguyen <tien.workinfo@gmail.com - tien.workinfo@icloud.com>, October 2015
 */

package com.training.tiennguyen.new_york_times.uis;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.training.tiennguyen.new_york_times.R;
import com.training.tiennguyen.new_york_times.adapters.ArticlesAdapter;
import com.training.tiennguyen.new_york_times.database.ArticlesRestClient;
import com.training.tiennguyen.new_york_times.models.ArticlesObject;
import com.training.tiennguyen.new_york_times.models.ArticlesResponse;
import com.training.tiennguyen.new_york_times.utils.Constants;
import com.training.tiennguyen.new_york_times.utils.EndlessRecyclerViewScrollListener;
import com.training.tiennguyen.new_york_times.utils.ItemClickSupport;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class ArticlesListActivity extends AppCompatActivity {
    @Bind(R.id.toolbar)
    protected Toolbar toolbar;
    @Bind(R.id.rv_articles)
    protected RecyclerView rvArticles;
    @Bind(R.id.swipe_container)
    protected SwipeRefreshLayout swipeContainer;
    private ArrayList<ArticlesObject> list;
    private ArticlesAdapter adapter;
    private String query;
    private int page;

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

    public boolean isOnline() {
        Runtime runtime = Runtime.getRuntime();
        try {
            Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
            int exitValue = ipProcess.waitFor();
            return (exitValue == 0);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles_list);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        query = "";
        page = 0;
        list = new ArrayList<>();
        adapter = new ArticlesAdapter(list, this);

        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rvArticles.setAdapter(adapter);
        rvArticles.setLayoutManager(gridLayoutManager);
        rvArticles.setHasFixedSize(true);
        rvArticles.setItemAnimator(new SlideInUpAnimator());
        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(gridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                customLoadMoreDataFromApi(page);
            }
        });
        ItemClickSupport.addTo(rvArticles).setOnItemClickListener(
                new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        ArticlesObject object = list.get(position);

                        Intent intent = new Intent(getBaseContext(), ArticleDetailsActivity.class);
                        intent.putExtra(Constants.HEADLINE_MAIN, object.getHeadline().get("main").toString());
                        if (object.getMultimedia() != null && object.getMultimedia().size() > 0) {
                            intent.putExtra(Constants.MULTIMEDIA, "http://www.nytimes.com/" + object.getMultimedia().get(0).getUrl());
                        }
                        intent.putExtra(Constants.WEB_URL, object.getWeb_url().toString());
                        /*if (object.getMultimedia() == null)
                            object.setMultimedia(new ArrayList<Multimedia>());
                        intent.putExtra(Constants.ARTICLES_DETAILS, Parcels.wrap(object));*/
                        startActivity(intent);
                    }
                }
        );

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
            }
        });
        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
    }

    private void fetchTimelineAsync() {
        adapter.clear();

        if (!isNetworkAvailable() || !isOnline()) {
            new AlertDialog.Builder(this)
                    .setTitle("Network Failures")
                    .setMessage("Your connection failed to load! Please close the app and connect again!")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return;
        } else {
            fetchArticles(query, page);
        }

        swipeContainer.setRefreshing(false);
    }

    public void customLoadMoreDataFromApi(int offset) {
        // Send an API request to retrieve appropriate data using the offset value as a parameter.
        // Deserialize API response and then construct new objects to append to the adapter
        // Add the new objects to the data source for the adapter
        //list.addAll(moreItems);
        fetchArticles(query, offset);
        // For efficiency purposes, notify the adapter of only the elements that got changed
        // curSize will equal to the index of the first element inserted because the list is 0-indexed
        int curSize = adapter.getItemCount();
        adapter.notifyItemRangeInserted(curSize, list.size() - 1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_articles_list, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();

                fetchArticles(query, page);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * fetchArticles
     */
    public void fetchArticles(String queryCurrent, int currentPage) {
        RequestParams params = new RequestParams();
        params.put(Constants.API_KEY, ArticlesRestClient.API_KEY);
        params.put(Constants.QUERY, queryCurrent);
        params.put(Constants.PAGE, page);

        query = queryCurrent;
        page = currentPage;

        ArticlesRestClient.get("", params, new JsonHttpResponseHandler() {

            /**
             * Returns when request succeeds
             *
             * @param statusCode http response status line
             * @param headers    response headers if any
             * @param response   parsed response if any
             */
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
                Gson gson = gsonBuilder.create();
                ArrayList<ArticlesObject> docs;
                try {
                    if (response.optJSONObject("response") != null) {
                        ArticlesResponse responseObject = gson.fromJson(
                                response.optJSONObject("response").toString(),
                                ArticlesResponse.class);
                        docs = responseObject.getDocs();
                        list.addAll(docs);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });
    }

    public void settingDialogs(MenuItem item) {
    }
}
