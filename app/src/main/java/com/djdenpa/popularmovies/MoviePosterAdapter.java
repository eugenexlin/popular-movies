package com.djdenpa.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djdenpa.popularmovies.database.NetworkUtils;
import com.djdenpa.popularmovies.themoviedb.ApiParams;
import com.djdenpa.popularmovies.themoviedb.MovieInformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by denpa on 6/25/2017.
 *
 * Recycler View for holding movie posters
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterViewHolder> implements MoviePosterOnClickHandler {

  public final ArrayList<MovieInformation> mMovieData = new ArrayList<>();
  private double maxPopularity = 0;

  private Context mContext = null;

  public ApiParams.MovieSort sort = ApiParams.MovieSort.POPULARITY;

  public MoviePosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    mContext = parent.getContext();
    int layoutIdForListItem = R.layout.movie_poster_item;
    LayoutInflater inflater = LayoutInflater.from(mContext);

    View view = inflater.inflate(layoutIdForListItem, parent, false);
    return new MoviePosterViewHolder(view, this);
  }

  @Override
  public void onBindViewHolder(MoviePosterViewHolder holder, int position) {
    MovieInformation movie = mMovieData.get(position);
    holder.mMovieTitle.setText(movie.originalTitle);

    //popularity
    holder.mPopularityRow.setVisibility (((sort == ApiParams.MovieSort.POPULARITY) ? View.VISIBLE : View.GONE));
    holder.mPopularity.setText(String.format(Locale.US, "%.1f", movie.popularity));
    int maxPop = (int) Math.floor(maxPopularity*10);
    int pop = (int) Math.floor(movie.popularity*10);
    holder.mPopularityBar.setMax(maxPop);
    holder.mPopularityBar.setProgress(pop);

    //rating
    holder.mRatingRow.setVisibility (((sort == ApiParams.MovieSort.RATING) ? View.VISIBLE : View.GONE));
    holder.mRating.setText(String.format(Locale.US, "%.1f ★", movie.voteAverage));
    int rate = (int) Math.floor(movie.voteAverage*10);
    holder.mRatingBar.setMax(100);
    holder.mRatingBar.setProgress(rate);

    if (sort == ApiParams.MovieSort.FAVORITE) {
      //use db to fetch picture
      holder.mMoviePoster.setImageResource(android.R.color.transparent);
      new NetworkUtils.LoadThumbnail(mContext, holder.mMoviePoster, movie).execute();

    }else{
      //normal
      Picasso.with(mContext).load(movie.posterUrlSmall).into(holder.mMoviePoster, new com.squareup.picasso.Callback() {
        @Override
        public void onSuccess() {
        }
        @Override
        public void onError() {
        }
      });
    }
  }

  public void appendMovieData(ArrayList<MovieInformation> movies){
    if (movies == null){
      return;
    }
    if (movies.size() <= 0){
      return;
    }
    for( MovieInformation movie : movies){
      mMovieData.add(movie);
      if (movie.popularity > maxPopularity){
        maxPopularity = movie.popularity;
      }
    }
    notifyDataSetChanged();
  }

  public void restoreMovieData(ArrayList<MovieInformation> movieData){
    clearMovieData();
    appendMovieData(movieData);
  }

  public void clearMovieData(){
    mMovieData.clear();
    maxPopularity = 0;
    notifyDataSetChanged();
  }

  @Override
  public int getItemCount() {
    return mMovieData.size();
  }

  private OnLoadMoreListener mOnLoadMoreListener;
  public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
    this.mOnLoadMoreListener = onLoadMoreListener;
  }
  public void tryRunOnLoadMoreListener(){
    if (mOnLoadMoreListener != null){
      mOnLoadMoreListener.onLoadMore();
    }
  }



  @Override
  public void onClick(int position) {
    MovieInformation movie = mMovieData.get(position);
    Class destinationClass = MovieDetailActivity.class;
    Intent intentToStartDetailActivity = new Intent(mContext, destinationClass);

    intentToStartDetailActivity.putExtra(MovieDetailActivity.MOVIE_NAME_EXTRA, movie.originalTitle);
    intentToStartDetailActivity.putExtra(MovieDetailActivity.MOVIE_ID_EXTRA, movie.movieId);
    intentToStartDetailActivity.putExtra(MovieDetailActivity.MOVIE_IS_FAVORITE, sort == ApiParams.MovieSort.FAVORITE);
    mContext.startActivity(intentToStartDetailActivity);

  }


}