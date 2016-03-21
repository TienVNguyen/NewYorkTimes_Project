/*
 * Copyright (c) 2016. Self Training Systems, Inc - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by TienNguyen <tien.workinfo@gmail.com - tien.workinfo@icloud.com>, October 2015
 */

package com.training.tiennguyen.new_york_times.models;

import java.util.ArrayList;

/**
 * ArticlesResponse
 *
 * @author Created by TienVNguyen on 19/03/2016.
 */
public class ArticlesResponse {
    private ArrayList<ArticlesObject> docs;

    public ArticlesResponse(ArrayList<ArticlesObject> docs) {
        this.docs = docs;
    }

    public ArrayList<ArticlesObject> getDocs() {
        return docs;
    }

    public void setDocs(ArrayList<ArticlesObject> docs) {
        this.docs = docs;
    }
}
