package com.djdenpa.popularmovies.themoviedb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by denpa on 7/1/2017.
 */

public class MovieInformation implements Serializable {

  protected final static SimpleDateFormat RELEASE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

  public int movieId;

  public String originalTitle;
  public Date releaseDate;
  public String plotSynopsis;

  public String posterPath;
  public String posterUrlSmall;
  public String posterUrlLarge;

  public double voteAverage;
  public long voteCount;
  public double popularity;

  public MovieInformation(JSONObject jObj) throws JSONException, ParseException {
    movieId = jObj.getInt("id");

    originalTitle = jObj.getString("original_title");
    releaseDate = RELEASE_DATE_FORMAT.parse(jObj.getString("release_date"));
    plotSynopsis = jObj.getString("overview");

    posterPath = jObj.getString("poster_path");
    posterUrlSmall = TheMovieDbApi.THE_MOVIE_DB_POSTER_BASE_URL + "w185" + posterPath;
    posterUrlLarge = TheMovieDbApi.THE_MOVIE_DB_POSTER_BASE_URL + "w780" + posterPath;

    voteAverage = jObj.getInt("vote_average");
    voteCount = jObj.getInt("vote_count");
    popularity = jObj.getDouble("popularity");
  }

}
