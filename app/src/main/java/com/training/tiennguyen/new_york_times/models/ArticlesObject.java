/*
 * Copyright (c) 2016. Self Training Systems, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by TienNguyen <tien.workinfo@gmail.com - tien.workinfo@icloud.com>, October 2015
 */

package com.training.tiennguyen.new_york_times.models;

import java.util.ArrayList;
import java.util.Map;

/**
 * ArticlesObject
 *
 * @author Created by TienVNguyen on 20/03/2016.
 */
public class ArticlesObject {
    private String web_url;
    private Map headline;
    private ArrayList<Multimedia> multimedia;

    public String getWeb_url() {
        return web_url;
    }

    public void setWeb_url(String web_url) {
        this.web_url = web_url;
    }

    public Map getHeadline() {
        return headline;
    }

    public void setHeadline(Map headline) {
        this.headline = headline;
    }

    public ArrayList<Multimedia> getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(ArrayList<Multimedia> multimedia) {
        this.multimedia = multimedia;
    }
}
