package com.djdenpa.popularmovies.themoviedb;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;

/**
 * Created by denpa on 7/30/2017.
 *
 * Trailers/ videos object
 */

public class VideoInformation {

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


  public String videoUrl(){
    switch(site.toUpperCase()){
      case "YOUTUBE":
        return "http://youtu.be/" + key;
      default:
        throw new UnsupportedOperationException("Unimplemented video site: " + site);
    }
  }


}


