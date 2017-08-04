package com.djdenpa.popularmovies.themoviedb;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by denpa on 7/30/2017.
 */

public class ReviewInformation {
  public String reviewId;

  public String author;
  public String content;

  public ReviewInformation(JSONObject jObj) throws JSONException, ParseException {

    reviewId = jObj.getString("id");
    author = jObj.getString("author");
    content = jObj.getString("content");

  }

}
