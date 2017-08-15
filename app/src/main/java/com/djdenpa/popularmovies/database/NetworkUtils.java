package com.djdenpa.popularmovies.database;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.djdenpa.popularmovies.themoviedb.MovieInformation;

import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Eugene on 8/13/2017.
 */

public class NetworkUtils {

  public static byte[] getBitmapBytesFromURL(String src) {
    try {
      URL url = new URL(src);
      HttpURLConnection connection = (HttpURLConnection) url
        .openConnection();
      connection.setDoInput(true);
      connection.connect();
      byte[] input = IOUtils.toByteArray( connection.getInputStream());
      return input;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  //based on https://stackoverflow.com/questions/7729133/using-asynctask-to-load-images-in-listview
  public static class LoadThumbnail extends AsyncTask<Void, Void, Bitmap> {
    private Context mContext;
    private ImageView mImage;
    private MovieInformation mMovie;

    public LoadThumbnail(Context context, ImageView image, MovieInformation movie) {
      this.mContext = context;
      this.mImage = image;
      this.mMovie = movie;
    }

    @Override
    protected Bitmap doInBackground(Void... params) {
      Bitmap result = null;

      String whereClause = MovieContract.MoviePosterEntry.COLUMN_MOVIE_ID + " = ?";
      String[] whereArgs = new String[] {
              String.valueOf(mMovie.movieId)
      };

      Cursor cursor = mContext.getContentResolver().query(
              MovieContract.MoviePosterEntry.CONTENT_URI,
              null,
              whereClause,
              whereArgs,
              MovieContract.MovieInformationEntry.COLUMN_MOVIE_ID);

      if (cursor != null && cursor.moveToFirst()){
        byte[] imageData = cursor.getBlob(cursor.getColumnIndex(MovieContract.MoviePosterEntry.COLUMN_POSTER_BYTES));
        result = BitmapFactory.decodeByteArray(imageData,0, imageData.length);
      }

      return result;
    }
    @Override
    protected void onPostExecute(Bitmap result) {
      if(result != null){
        mImage.setImageBitmap(result);
      }
    }

  }
}
