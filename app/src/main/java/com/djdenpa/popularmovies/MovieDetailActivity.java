package com.djdenpa.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djdenpa.popularmovies.themoviedb.MovieInformation;
import com.djdenpa.popularmovies.themoviedb.TheMovieDbApi;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

/**
 * Created by denpa on 7/4/2017.
 */

public class MovieDetailActivity extends AppCompatActivity {

  public static final String MOVIE_NAME_EXTRA = "MovieDetailActivity_MOVIE_NAME_EXTRA";
  public static final String MOVIE_ID_EXTRA = "MovieDetailActivity_MOVIE_ID_EXTRA";

  protected TextView mMovieTitle;
  protected TextView mErrorMessage;
  protected ImageView mMoviePoster;
  protected ProgressBar mLoadingIndicator;

  protected TextView mMovieYear;
  protected TextView mMovieRating;
  protected TextView mMovieSynopsis;
  protected TextView mMovieDuration;
  protected TextView mMovieReleaseDate;

  protected int mMovieId = -1;
  protected MovieInformation movie;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getResources().getConfiguration().orientation == 	android.content.res.Configuration.ORIENTATION_LANDSCAPE){
      setContentView(R.layout.activity_movie_details_landscape);
    }else{
      setContentView(R.layout.activity_movie_details);
    }

    setTitle(R.string.movie_details_header);

    mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
    mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);
    mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
    mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

    mMovieYear = (TextView) findViewById(R.id.tv_movie_year);
    mMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
    mMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
    mMovieDuration = (TextView) findViewById(R.id.tv_movie_duration);
    mMovieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);

    Intent intentThatStartedThisActivity = getIntent();

    // COMPLETED (2) Display the weather forecast that was passed from MainActivity
    if (intentThatStartedThisActivity != null) {
      if (intentThatStartedThisActivity.hasExtra(MOVIE_NAME_EXTRA)) {
        String movieName = intentThatStartedThisActivity.getStringExtra(MOVIE_NAME_EXTRA);
        mMovieTitle.setText(movieName);
      }
      if (intentThatStartedThisActivity.hasExtra(MOVIE_ID_EXTRA)) {
        mMovieId = intentThatStartedThisActivity.getIntExtra(MOVIE_ID_EXTRA,-1);
      }
    }


    if (mMovieId <= 0){
      showErrorMessage("Invalid Movie Id");
      return;
    }

    new LoadMovieDetailsTask().execute(mMovieId);
  }


  private void showErrorMessage(String message) {
    mLoadingIndicator.setVisibility(View.GONE);
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(message);
  }



  /**
   * Run the function to get the ArrayList of MovieInformation objects.
   */
  public class LoadMovieDetailsTask extends AsyncTask<Integer, Void, MovieInformation> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected MovieInformation doInBackground(Integer... movieId) {
      try {
        return TheMovieDbApi.GetMovieById(movieId[0]);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    protected void onPostExecute(MovieInformation movieData) {
      mLoadingIndicator.setVisibility(View.GONE);
      if (movieData != null) {
        movie = movieData;

        String voteString = String.format( "%.1f/10 (%d votes)", movieData.voteAverage, movieData.voteCount);
        mMovieRating.setText(voteString);
        mMovieDuration.setText(String.format("%dmin", movieData.duration));
        mMovieSynopsis.setText(movieData.plotSynopsis);

        if (movieData.releaseDate != null){
          SimpleDateFormat df = new SimpleDateFormat("yyyy");
          mMovieYear.setText(df.format(movieData.releaseDate));
          SimpleDateFormat dfRelease = new SimpleDateFormat("dd MMM yyyy");
          mMovieReleaseDate.setText(String.format("Release Date: %s", dfRelease.format(movieData.releaseDate)));
        }

        Picasso.with(getBaseContext()).load(movieData.posterUrlSmall).into(mMoviePoster, new com.squareup.picasso.Callback() {
          @Override
          public void onSuccess() {
          }
          @Override
          public void onError() {
          }
        });


      } else {
        showErrorMessage("Unable to fetch Movie Data from ID");
      }
    }
  }
}
