package com.djdenpa.popularmovies;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by denpa on 8/9/2017.
 */

public class MovieReviewActivity extends AppCompatActivity {

  public static final String MOVIE_ID_EXTRA = "MovieReviewActivity_MOVIE_ID_EXTRA";

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_movie_reviews);

    setTitle(R.string.movie_review_header);

  }

}
