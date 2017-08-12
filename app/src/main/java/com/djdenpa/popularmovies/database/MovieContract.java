package com.djdenpa.popularmovies.database;

import android.provider.BaseColumns;

/**
 * Created by denpa on 8/12/2017.
 */

public class MovieContract {

  public static final class MovieInformationEntry implements BaseColumns{
    public static final String TABLE_NAME = "movieInformation";
    public static final String COLUMN_MOVIE_ID = "movieId";
    public static final String COLUMN_MOVIE_JSON = "movieJsonData";
  }

  public static final class MoviePosterEntry implements BaseColumns{
    public static final String TABLE_NAME = "moviePosters";
    public static final String COLUMN_MOVIE_ID = "movieId";
    public static final String COLUMN_POSTER_PATH = "posterPath";
    public static final String COLUMN_POSTER_BYTES = "posterBytes";
  }

}
