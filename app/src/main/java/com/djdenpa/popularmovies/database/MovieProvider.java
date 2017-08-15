package com.djdenpa.popularmovies.database;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by denpa on 8/14/2017.
 *
 * content provider for movies.
 */

public class MovieProvider extends ContentProvider {
  private MovieDbHelper mMovieHelper;

  private static final int CODE_MOVIE_INFO = 100;
  private static final int CODE_MOVIE_POSTER = 101;

  private static final UriMatcher sUriMatcher = buildUriMatcher();

  @Override
  public boolean onCreate() {
    mMovieHelper = new MovieDbHelper(getContext());
    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
    final SQLiteDatabase db = mMovieHelper.getReadableDatabase();
    switch (sUriMatcher.match(uri)) {
      case CODE_MOVIE_INFO:
        return db.query(MovieContract.MovieInformationEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
      case CODE_MOVIE_POSTER:
        return db.query(MovieContract.MoviePosterEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }
    return null;
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    return null;
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues cv) {
    switch (sUriMatcher.match(uri)) {
      case CODE_MOVIE_INFO:
        mMovieHelper.getWritableDatabase().insert(MovieContract.MovieInformationEntry.TABLE_NAME, null, cv);
        return null;
      case CODE_MOVIE_POSTER:
        mMovieHelper.getWritableDatabase().insert(MovieContract.MoviePosterEntry.TABLE_NAME, null, cv);
        return null;
    }

    return null;
  }

  @Override
  public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
    switch (sUriMatcher.match(uri)) {
      case CODE_MOVIE_INFO:
        return mMovieHelper.getWritableDatabase().delete(
                MovieContract.MovieInformationEntry.TABLE_NAME,
                selection,
                selectionArgs);
      case CODE_MOVIE_POSTER:
        return mMovieHelper.getWritableDatabase().delete(
                MovieContract.MoviePosterEntry.TABLE_NAME,
                selection,
                selectionArgs);
    }
    return 0;
  }

  @Override
  public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
    return 0;
  }

  private static UriMatcher buildUriMatcher() {

    final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    final String authority = MovieContract.CONTENT_AUTHORITY;

    matcher.addURI(authority, MovieContract.PATH_MOVIE, CODE_MOVIE_INFO);
    matcher.addURI(authority, MovieContract.PATH_POSTER, CODE_MOVIE_POSTER);

    return matcher;
  }

}
