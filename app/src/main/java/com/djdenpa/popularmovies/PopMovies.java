package com.djdenpa.popularmovies;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djdenpa.popularmovies.themoviedb.ApiParams;
import com.djdenpa.popularmovies.themoviedb.MovieInformation;
import com.djdenpa.popularmovies.themoviedb.TheMovieDbApi;

import java.util.ArrayList;

public class PopMovies extends AppCompatActivity {

  private static final String MOVIE_POSTER_STATE = "movieposter.recycleview.state";

  private RecyclerView mRecyclerView;

  private MoviePosterAdapter mMoviePosterAdapter;

  private ProgressBar mLoadingIndicator;

  private TextView mErrorMessageDiscover;

  private int mPageNum = 0;
  private ApiParams.MovieSort sort = ApiParams.MovieSort.POPULARITY;

  private final int mLoadMoreThreshold = 6;
  private boolean isLoadingMoreMovies = false;
  private boolean allowLoadingMoreMovies = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pop_movies);

    setTitle(R.string.popular_header);

    mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_posters);

    int columns = calculateNoOfColumns(getBaseContext());

    final GridLayoutManager layoutManager
            = new GridLayoutManager(this, columns, GridLayoutManager.VERTICAL, false){

      @Override
      public Parcelable onSaveInstanceState() {
        MoviePosterParcelable state = new MoviePosterParcelable();
        state.mScrollPosition = this.findFirstVisibleItemPosition();
        state.mMovieData = mMoviePosterAdapter.mMovieData;
        state.mPageNum = mPageNum;
        state.mSort = sort.name();
        return state;
      }

      @Override
      public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if(state != null && state instanceof MoviePosterParcelable) {
          MoviePosterParcelable movieState = (MoviePosterParcelable) state;
          mMoviePosterAdapter.restoreMovieData(movieState.mMovieData);
          int count = mMoviePosterAdapter.getItemCount();
          if (movieState.mScrollPosition != RecyclerView.NO_POSITION && movieState.mScrollPosition < count) {
            this.scrollToPosition(movieState.mScrollPosition);
          }
          mPageNum = movieState.mPageNum;
          sort = ApiParams.MovieSort.valueOf( movieState.mSort);
          mMoviePosterAdapter.sort = sort;
        }
      }
    };
    mRecyclerView.setLayoutManager(layoutManager);

    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        int movieCount = layoutManager.getItemCount();
        int lastVisibleIndex = layoutManager.findLastVisibleItemPosition();
        if (!isLoadingMoreMovies && movieCount <= (lastVisibleIndex + mLoadMoreThreshold)) {
          mMoviePosterAdapter.tryRunOnLoadMoreListener();
          isLoadingMoreMovies = true;
          allowLoadingMoreMovies = true;
        }
      }
    });

    mMoviePosterAdapter = new MoviePosterAdapter();
    mRecyclerView.setAdapter(mMoviePosterAdapter);

    mMoviePosterAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
      @Override
      public void onLoadMore() {
        PerformDiscoverMovies();
      }
    });

    mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    mErrorMessageDiscover = (TextView) findViewById(R.id.tv_error_message);

    if (savedInstanceState == null){
      PerformNewDiscoverMovies();
    }
  }

  /**
   * Based On Suggestion from Otieno Rowland code review.
   */
  public static int calculateNoOfColumns(Context context) {
    DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
    System.out.println(displayMetrics.density);
    //interesting means, I do not want it always just to be basically based on real life size.
    //I want tablets to display more columns, but also display them bigger.
    //density on phone might be around 2.5
    //density on tablets might be around 1.0
    //here it is basically making it MORE phone-ish number of columns on tablet, not just like 3x
    float interestingWidth = (displayMetrics.widthPixels * 2) / (displayMetrics.density + 2.5f);
    int scalingFactor = 200;
    int noOfColumns = (int) (interestingWidth / scalingFactor);
    if (noOfColumns < 2){
      noOfColumns = 2;
    }
    return noOfColumns;
  }


  /**
   * This function is called to initialize a fresh search on page 1 of the API.
   *
   */
  private void PerformNewDiscoverMovies(){
    mPageNum = 0;

    //this is if there is a background request for loading another page,
    //we don't want it to do anything when it completes.
    allowLoadingMoreMovies = false;
    mMoviePosterAdapter.clearMovieData();
    PerformDiscoverMovies();
  }

  private void PerformDiscoverMovies(){
    allowLoadingMoreMovies = true;
    int pageToLoad = (++mPageNum);

    ApiParams params = new ApiParams();
    params.page = pageToLoad;
    params.sort = sort;

    new AppendMoreMoviesTask().execute(params);
  }

  private void showErrorMessage() {
    //mRecyclerView.setVisibility(View.GONE);
    mErrorMessageDiscover.setVisibility(View.VISIBLE);
  }

  private void showMovies() {
    mRecyclerView.setVisibility(View.VISIBLE);
    mErrorMessageDiscover.setVisibility(View.GONE);
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelable(MOVIE_POSTER_STATE, mRecyclerView.getLayoutManager().onSaveInstanceState());
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    Parcelable moviePosterState = savedInstanceState.getParcelable(MOVIE_POSTER_STATE);
    mRecyclerView.getLayoutManager().onRestoreInstanceState(moviePosterState);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.pop_movies_menu, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_refresh) {
      PerformNewDiscoverMovies();
      return true;
    }
    if (id == R.id.action_sort) {
      if (sort == ApiParams.MovieSort.POPULARITY){
        sort = ApiParams.MovieSort.RATING;
        setTitle(R.string.highest_rated_header);
      }else if (sort == ApiParams.MovieSort.RATING){
        sort = ApiParams.MovieSort.POPULARITY;
        setTitle(R.string.popular_header);
      }
      mMoviePosterAdapter.sort = sort;
      PerformNewDiscoverMovies();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  /**
   * Run the function to get the ArrayList of MovieInformation objects.
   */
  private class AppendMoreMoviesTask extends AsyncTask<ApiParams, Void, ArrayList<MovieInformation>> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<MovieInformation> doInBackground(ApiParams... params) {
      try {
        return TheMovieDbApi.QueryMovies(params[0]);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    protected void onPostExecute(ArrayList<MovieInformation> movieData) {
      mLoadingIndicator.setVisibility(View.GONE);
      if (movieData != null) {
        showMovies();
        if (mPageNum <= 1 || allowLoadingMoreMovies){
          mMoviePosterAdapter.appendMovieData(movieData);
        }
      } else {
        showErrorMessage();
      }
      isLoadingMoreMovies = false;
    }
  }
}
