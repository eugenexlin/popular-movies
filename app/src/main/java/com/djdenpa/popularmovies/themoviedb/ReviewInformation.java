package com.djdenpa.popularmovies.themoviedb;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by denpa on 7/30/2017.
 */

public class ReviewInformation implements Parcelable {
  public String reviewId;

  public String author;
  public String content;

  public ReviewInformation(JSONObject jObj) throws JSONException, ParseException {

    reviewId = jObj.getString("id");
    author = jObj.getString("author");
    content = jObj.getString("content");

  }

  protected ReviewInformation(Parcel in) {
    reviewId = in.readString();
    author = in.readString();
    content = in.readString();
  }

  public static final Creator<ReviewInformation> CREATOR = new Creator<ReviewInformation>() {
    @Override
    public ReviewInformation createFromParcel(Parcel in) {
      return new ReviewInformation(in);
    }

    @Override
    public ReviewInformation[] newArray(int size) {
      return new ReviewInformation[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(reviewId);
    dest.writeString(author);
    dest.writeString(content);
  }
}
