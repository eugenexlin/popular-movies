package com.djdenpa.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

import com.djdenpa.popularmovies.themoviedb.ReviewInformation;

import java.util.ArrayList;

/**
 * Created by denpa on 8/10/2017.
 *
 * parcelable for saving review state
 */

public class MovieReviewParcelable  implements Parcelable {
  public int mScrollPosition;
  public int mPageNum;
  public ArrayList<ReviewInformation> mReviewData;

  public MovieReviewParcelable() {
  }

  private MovieReviewParcelable(Parcel in) {
    mScrollPosition = in.readInt();
    mPageNum = in.readInt();
    mReviewData = (ArrayList<ReviewInformation>) in.readArrayList(null);
  }

  public static final Creator<MovieReviewParcelable> CREATOR = new Creator<MovieReviewParcelable>() {
    @Override
    public MovieReviewParcelable createFromParcel(Parcel in) {
      return new MovieReviewParcelable(in);
    }

    @Override
    public MovieReviewParcelable[] newArray(int size) {
      return new MovieReviewParcelable[size];
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
    dest.writeList(mReviewData);
  }

}
