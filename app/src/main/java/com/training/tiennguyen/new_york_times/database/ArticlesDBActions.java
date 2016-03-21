/*
 * Copyright (c) 2016. Self Training Systems, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by TienNguyen <tien.workinfo@gmail.com - tien.workinfo@icloud.com>, October 2015
 */

package com.training.tiennguyen.new_york_times.database;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.training.tiennguyen.new_york_times.models.ArticlesObject;
import com.training.tiennguyen.new_york_times.models.ArticlesResponse;
import com.training.tiennguyen.new_york_times.utils.Constants;

import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * ArticlesDBActions
 *
 * @author Created by TienVNguyen on 19/03/2016.
 * @deprecated Temperately not used
 */
public class ArticlesDBActions {
    /**
     * fetchArticles
     *
     * @param query query
     * @return ArrayList<ArticlesObject>
     */
    public ArrayList<ArticlesObject> fetchArticles(String query) {
        RequestParams params = new RequestParams();
        params.put(Constants.API_KEY, ArticlesRestClient.API_KEY);
        params.put(Constants.QUERY, query);
        params.put(Constants.PAGE, 0);

        final ArrayList<ArticlesObject> articlesObjects = new ArrayList<>();
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
                try {
                    if (response.optJSONObject("response") != null) {
                        ArticlesResponse response1 = gson.fromJson(
                                response.optJSONObject("response").toString(),
                                ArticlesResponse.class);

                        articlesObjects.clear();
                        articlesObjects.addAll(response1.getDocs());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });

        return articlesObjects;
    }
}
