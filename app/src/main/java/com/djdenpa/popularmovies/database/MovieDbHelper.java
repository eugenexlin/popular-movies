package com.djdenpa.popularmovies.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.djdenpa.popularmovies.database.MovieContract.*;

/**
 * Created by denpa on 8/12/2017.
 */

public class MovieDbHelper extends SQLiteOpenHelper {
  private static final String DATABASE_NAME = "movie.db";
  private static final int DATABASE_VERSION = 2;

  public MovieDbHelper(Context context){
    super(context, DATABASE_NAME, null, DATABASE_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {
    final String SQL_CREATE_MOVIE_INFO_TABLE = "CREATE TABLE " + MovieInformationEntry.TABLE_NAME + " (" +
            MovieInformationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MovieInformationEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
            MovieInformationEntry.COLUMN_MOVIE_JSON + " TEXT NOT NULL " +
        "); ";
    final String SQL_CREATE_MOVIE_POSTER_TABLE = "CREATE TABLE " + MoviePosterEntry.TABLE_NAME + " (" +
            MoviePosterEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            MoviePosterEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
            MoviePosterEntry.COLUMN_POSTER_BYTES + " BLOB NOT NULL " +
            "); ";

    db.execSQL(SQL_CREATE_MOVIE_INFO_TABLE);
    db.execSQL(SQL_CREATE_MOVIE_POSTER_TABLE);
  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    db.execSQL("DROP TABLE IF EXISTS " + MovieInformationEntry.TABLE_NAME);
    db.execSQL("DROP TABLE IF EXISTS " + MoviePosterEntry.TABLE_NAME);
    onCreate(db);
  }
}
