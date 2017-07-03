package com.djdenpa.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * Created by denpa on 6/25/2017.
 */

public class MoviePosterViewHolder extends RecyclerView.ViewHolder {

  public final ImageView mMoviePoster;
  public final TextView mMovieTitle;

  public final TableRow mPopularityRow;
  public final TextView mPopularity;
  public final ProgressBar mPopularityBar;

  public final TableRow mRatingRow;
  public final TextView mRating;
  public final ProgressBar mRatingBar;

  public MoviePosterViewHolder(View itemView) {
    super(itemView);

    mMoviePoster = (ImageView) itemView.findViewById(R.id.iv_movie_poster);
    mMovieTitle = (TextView) itemView.findViewById(R.id.tv_movie_title);

    mPopularityRow = (TableRow) itemView.findViewById(R.id.tr_movie_popularity);
    mPopularity = (TextView) itemView.findViewById(R.id.tv_movie_popularity);
    mPopularityBar = (ProgressBar) itemView.findViewById(R.id.pb_movie_popularity_bar);

    mRatingRow = (TableRow) itemView.findViewById(R.id.tr_movie_rating);
    mRating = (TextView) itemView.findViewById(R.id.tv_movie_rating);
    mRatingBar = (ProgressBar) itemView.findViewById(R.id.pb_movie_rating_bar);
  }
}
