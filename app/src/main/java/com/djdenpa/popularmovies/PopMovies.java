package com.djdenpa.popularmovies;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

  private RecyclerView mRecyclerView;

  private MoviePosterAdapter mMoviePosterAdapter;

  private ProgressBar mLoadingIndicator;

  private TextView mErrorMessageDiscover;

  private int mPageNum = 0;
  public ApiParams.MovieSort sort = ApiParams.MovieSort.POPULARITY;


  private int mLoadMoreThreshold = 6;
  private boolean isLoadingMoreMovies = false;
  private boolean allowLoadingMoreMovies = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_pop_movies);

    mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_posters);

    final GridLayoutManager layoutManager
            = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
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
        System.out.println("LoadingMore");
        PerformDiscoverMovies();
      }
    });

    mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    mErrorMessageDiscover = (TextView) findViewById(R.id.tv_error_message_discover);

    PerformNewDiscoverMovies();
  }


  /**
   * This function is called to initialize a fresh search on page 1 of the API.
   *
   */
  protected void PerformNewDiscoverMovies(){
    mPageNum = 0;

    //this is if there is a background request for loading another page,
    //we don't want it to do anything when it completes.
    allowLoadingMoreMovies = false;
    mMoviePosterAdapter.clearMovieData();
    PerformDiscoverMovies();
  }
  protected void PerformDiscoverMovies(){
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
      }else if (sort == ApiParams.MovieSort.RATING){
        sort = ApiParams.MovieSort.POPULARITY;
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
  public class AppendMoreMoviesTask extends AsyncTask<ApiParams, Void, ArrayList<MovieInformation>> {

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
      mLoadingIndicator.setVisibility(View.INVISIBLE);
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
