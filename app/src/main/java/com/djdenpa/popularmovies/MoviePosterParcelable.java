package com.djdenpa.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import com.djdenpa.popularmovies.themoviedb.MovieInformation;

import java.util.ArrayList;

/**
 * Created by denpa on 7/5/2017.
 *
 * Hold the data to restore instance state
 */

public class MoviePosterParcelable implements Parcelable {
  public int mScrollPosition;
  public int mPageNum;
  public String mSort;
  public ArrayList<MovieInformation> mMovieData;

  public MoviePosterParcelable() {
  }

  private MoviePosterParcelable(Parcel in) {
    mScrollPosition = in.readInt();
    mPageNum = in.readInt();
    mSort = in.readString();
    mMovieData = (ArrayList<MovieInformation>) in.readSerializable();
  }

  public static final Creator<MoviePosterParcelable> CREATOR = new Creator<MoviePosterParcelable>() {
    @Override
    public MoviePosterParcelable createFromParcel(Parcel in) {
      return new MoviePosterParcelable(in);
    }

    @Override
    public MoviePosterParcelable[] newArray(int size) {
      return new MoviePosterParcelable[size];
    }
  };


  @Override
  public int describeContents() {
    return 0;
  }

  @Override
  public void writeToParcel(Parcel dest, int flags) {
    dest.writeInt(mScrollPosition);
    dest.writeInt(mPageNum);
    dest.writeString(mSort);
    dest.writeSerializable(mMovieData);
  }

}
