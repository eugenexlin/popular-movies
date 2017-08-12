package com.djdenpa.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.djdenpa.popularmovies.themoviedb.ReviewInformation;
import com.djdenpa.popularmovies.themoviedb.TheMovieDbApi;

import java.util.ArrayList;

/**
 * Created by denpa on 8/9/2017.
 */

public class MovieReviewActivity extends AppCompatActivity {

  public static final String MOVIE_ID_EXTRA = "MovieReviewActivity_MOVIE_ID_EXTRA";

  private MovieReviewItemAdapter mReviewAdapter;
  private ProgressBar mLoadingIndicator;
  private TextView mErrorMessage;
  private TextView mNoReviewMessage;
  private RecyclerView mRecyclerView;

  private DividerItemDecoration mDividerItemDecoration;

  private int mPageNum;
  private int mMovieId;

  private final int mLoadMoreThreshold = 5;
  private boolean isLoadingMoreMovies = false;
  private boolean isOutOfReviews = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_movie_reviews);

    setTitle(R.string.movie_review_header);

    mPageNum = 1;

    Intent intentThatStartedThisActivity = getIntent();
    if (intentThatStartedThisActivity != null) {
      if (intentThatStartedThisActivity.hasExtra(MOVIE_ID_EXTRA)) {
        mMovieId = intentThatStartedThisActivity.getIntExtra(MOVIE_ID_EXTRA,-1);
      }
    }

    mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
    mRecyclerView = (RecyclerView) findViewById(R.id.rv_movie_reviews);
    mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
    mNoReviewMessage = (TextView) findViewById(R.id.tv_no_reviews);

    mReviewAdapter = new MovieReviewItemAdapter();
    mReviewAdapter.setOnLoadMoreListener(new OnLoadMoreListener() {
      @Override
      public void onLoadMore() {
        FetchReviewPage();
      }
    });

    mRecyclerView.setAdapter(mReviewAdapter);
    final LinearLayoutManager layoutManager
            = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false){

      @Override
      public Parcelable onSaveInstanceState() {
        MovieReviewParcelable state = new MovieReviewParcelable();
        state.mScrollPosition = this.findFirstVisibleItemPosition();
        state.mReviewData = mReviewAdapter.mReviewData;
        state.mPageNum = mPageNum;
        return state;
      }

      @Override
      public void onRestoreInstanceState(Parcelable state) {
        super.onRestoreInstanceState(state);
        if(state != null && state instanceof MovieReviewParcelable) {
          MovieReviewParcelable movieState = (MovieReviewParcelable) state;
          mReviewAdapter.clearReviewData();
          mReviewAdapter.appendReviewData(movieState.mReviewData);
          int count = mReviewAdapter.getItemCount();
          if (movieState.mScrollPosition != RecyclerView.NO_POSITION && movieState.mScrollPosition < count) {
            this.scrollToPosition(movieState.mScrollPosition);
          }
          mPageNum = movieState.mPageNum;
        }
      }
    };

    mRecyclerView.setLayoutManager(layoutManager);

    mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
      @Override
      public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (isOutOfReviews){ return; }
        int movieCount = layoutManager.getItemCount();
        int lastVisibleIndex = layoutManager.findLastVisibleItemPosition();
        if (!isLoadingMoreMovies && movieCount <= (lastVisibleIndex + mLoadMoreThreshold)) {
          mReviewAdapter.tryRunOnLoadMoreListener();
          isLoadingMoreMovies = true;
        }
      }
    });

    mDividerItemDecoration = new DividerItemDecoration(this,
            layoutManager.getOrientation());
    mRecyclerView.addItemDecoration(mDividerItemDecoration);

    new AppendMoreReviewsTask().execute(mMovieId, mPageNum);
  }

  private void FetchReviewPage(){
    int pageToLoad = (++mPageNum);

    new AppendMoreReviewsTask().execute(mMovieId, pageToLoad);
  }

  /**
   * Run the function to get the ArrayList of MovieInformation objects.
   */
  private class AppendMoreReviewsTask extends AsyncTask<Integer, Void, ArrayList<ReviewInformation>> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<ReviewInformation> doInBackground(Integer... params) {
      try {
        return TheMovieDbApi.GetMovieReviews(params[0], params[1]);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    protected void onPostExecute(ArrayList<ReviewInformation> reviewData) {
      mLoadingIndicator.setVisibility(View.GONE);
      mNoReviewMessage.setVisibility(View.GONE);
      mErrorMessage.setVisibility(View.GONE);
      mRecyclerView.setVisibility(View.VISIBLE);
      if (reviewData != null) {
        if (reviewData.size() <= 0){
          if (mReviewAdapter.mReviewData.size() <= 0){
            mNoReviewMessage.setVisibility(View.VISIBLE);
          }
          //perhaps no more results..
          isOutOfReviews = true;
        }
        mReviewAdapter.appendReviewData(reviewData);
      } else {
        mErrorMessage.setVisibility(View.VISIBLE);
      }
      isLoadingMoreMovies = false;
    }
  }
}
