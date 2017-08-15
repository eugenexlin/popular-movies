package com.djdenpa.popularmovies.themoviedb;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by denpa on 7/1/2017.
 *
 * Movie fields object
 */

public class MovieInformation implements Parcelable {

  private final static SimpleDateFormat RELEASE_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

  public int movieId;

  public String originalTitle;
  public Date releaseDate;
  public String plotSynopsis;

  //public String posterPath;
  public String posterUrlSmall;
  public String posterUrlLarge;

  public double voteAverage;
  public long voteCount;
  public double popularity;

  public int duration;

  private MovieInformation(Parcel in) {
    movieId = in.readInt();
    originalTitle = in.readString();
    //plotSynopsis = in.readString();
    //posterPath = in.readString();
    posterUrlSmall = in.readString();
    //posterUrlLarge = in.readString();
    voteAverage = in.readDouble();
    voteCount = in.readLong();
    popularity = in.readDouble();
    duration = in.readInt();
  }

  public static final Creator<MovieInformation> CREATOR = new Creator<MovieInformation>() {
    @Override
    public MovieInformation createFromParcel(Parcel in) {
      return new MovieInformation(in);
    }

    @Override
    public MovieInformation[] newArray(int size) {
      return new MovieInformation[size];
    }
  };

  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(movieId);
    dest.writeString(originalTitle);
    //dest.writeString(plotSynopsis);
    //dest.writeString(posterPath);
    dest.writeString(posterUrlSmall);
    //dest.writeString(posterUrlLarge);
    dest.writeDouble(voteAverage);
    dest.writeLong(voteCount);
    dest.writeDouble(popularity);
    dest.writeInt(duration);
  }

  /**
   *
   * @param BindMinimal The Poster View only needs some data. Fill in less for smaller Parcelable.
   */
  public MovieInformation(JSONObject jObj, boolean BindMinimal) throws JSONException, ParseException {
    movieId = jObj.getInt("id");

    originalTitle = jObj.getString("original_title");

    String posterPath = jObj.getString("poster_path");
    posterUrlSmall = TheMovieDbApi.THE_MOVIE_DB_POSTER_BASE_URL + "w185" + posterPath;

    voteAverage = jObj.getInt("vote_average");
    popularity = jObj.getDouble("popularity");

    if (BindMinimal){
      return;
    }

    //information below is just for detailed mode.
    //otherwise we can bind less to minimize parcelable size.

    posterUrlLarge = TheMovieDbApi.THE_MOVIE_DB_POSTER_BASE_URL + "w780" + posterPath;

    releaseDate = RELEASE_DATE_FORMAT.parse(jObj.getString("release_date"));
    voteCount = jObj.getInt("vote_count");
    plotSynopsis = jObj.getString("overview");

    //below are fields that might not exist on the query result.
    if (jObj.has("runtime")) {
      duration = jObj.getInt("runtime");
    }
  }

}
