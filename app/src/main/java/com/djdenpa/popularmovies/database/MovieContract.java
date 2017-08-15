package com.djdenpa.popularmovies.database;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by denpa on 8/12/2017.
 */

public class MovieContract {

  public static final String CONTENT_AUTHORITY = "com.djdenpa.popularmovies";
  public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

  public static final String PATH_MOVIE = "movie";
  public static final String PATH_POSTER = "poster";

  public static final class MovieInformationEntry implements BaseColumns{

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_MOVIE)
            .build();

    public static final String TABLE_NAME = "movieInformation";
    public static final String COLUMN_MOVIE_ID = "movieId";
    public static final String COLUMN_MOVIE_JSON = "movieJsonData";

  }

  public static final class MoviePosterEntry implements BaseColumns{

    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
            .appendPath(PATH_POSTER)
            .build();
    public static final String TABLE_NAME = "moviePosters";
    public static final String COLUMN_MOVIE_ID = "movieId";
    public static final String COLUMN_POSTER_BYTES = "posterBytes";
  }

}
