package com.djdenpa.popularmovies.themoviedb;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Comparator;

/**
 * Created by denpa on 7/30/2017.
 *
 * Trailers/ videos object
 */

public class VideoInformation implements Parcelable {

  public String videoId;

  public String key;
  public String site;

  public String name;
  public String type;
  public int size;

  public VideoInformation(JSONObject jObj) throws JSONException, ParseException {

    videoId = jObj.getString("id");
    key = jObj.getString("key");
    site = jObj.getString("site");
    name = jObj.getString("name");
    type = jObj.getString("type");
    size = jObj.getInt("size");

  }


  protected VideoInformation(Parcel in) {
    videoId = in.readString();
    key = in.readString();
    site = in.readString();
    name = in.readString();
    type = in.readString();
    size = in.readInt();
  }

  public static final Creator<VideoInformation> CREATOR = new Creator<VideoInformation>() {
    @Override
    public VideoInformation createFromParcel(Parcel in) {
      return new VideoInformation(in);
    }

    @Override
    public VideoInformation[] newArray(int size) {
      return new VideoInformation[size];
    }
  };

  public String videoUrl(){
    switch(site.toUpperCase()){
      case "YOUTUBE":
        return "http://youtu.be/" + key;
      default:
        throw new UnsupportedOperationException("Unimplemented video site: " + site);
    }
  }
  public String thumbnailUrl(){
    switch(site.toUpperCase()){
      case "YOUTUBE":
        return "https://img.youtube.com/vi/" + key + "/default.jpg";
      default:
        return ""; //just don't load if not supported
    }
  }

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeString(videoId);
    dest.writeString(key);
    dest.writeString(site);
    dest.writeString(name);
    dest.writeString(type);
    dest.writeInt(size);
  }


  public static class VideoInformationNameSorter implements Comparator<VideoInformation>{
    @Override
    public int compare(VideoInformation o1, VideoInformation o2) {
      return o1.name.compareTo(o2.name);
    }
  }
}


