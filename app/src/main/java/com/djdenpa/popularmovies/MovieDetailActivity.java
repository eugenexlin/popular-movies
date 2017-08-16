package com.djdenpa.popularmovies;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.djdenpa.popularmovies.database.MovieContract;
import com.djdenpa.popularmovies.database.NetworkUtils;
import com.djdenpa.popularmovies.themoviedb.MovieInformation;
import com.djdenpa.popularmovies.themoviedb.TheMovieDbApi;
import com.djdenpa.popularmovies.themoviedb.VideoInformation;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;

/**
 * Created by denpa on 7/4/2017.
 *
 * Activity for viewing a single movie's details
 */

public class MovieDetailActivity extends AppCompatActivity{

  private static final String MOVIE_VIDEO_TRAILER_STATE = "movie.video.trailer.state";
  private static final String MOVIE_VIDEO_OTHER_STATE = "movie.video.other.state";

  public static final String MOVIE_NAME_EXTRA = "MovieDetailActivity_MOVIE_NAME_EXTRA";
  public static final String MOVIE_ID_EXTRA = "MovieDetailActivity_MOVIE_ID_EXTRA";
  public static final String MOVIE_IS_FAVORITE = "MovieDetailActivity_MOVIE_IS_FAVORITE";

  private TextView mMovieTitle;
  private TextView mErrorMessage;
  private ImageView mMoviePoster;
  private ProgressBar mLoadingIndicator;

  private CheckBox mCheckBoxFavorite;

  private TextView mMovieYear;
  private TextView mMovieRating;
  private TextView mMovieSynopsis;
  private TextView mMovieDuration;
  private TextView mMovieReleaseDate;

  private int mMovieId = -1;

  private MovieInformation movie;

  private boolean mLoadBigImage = false;

  private TextView mVideoTrailersHeader;
  private MovieVideoItemAdapter mAdapterVideoTrailers;
  private ListView mListViewVideoTrailers;
  private TextView mVideoOthersHeader;
  private MovieVideoItemAdapter mAdapterVideoOthers;
  private ListView mListViewVideoOthers;

  private ImageView mHorizontalLine;

  private ProgressBar mLoadingVideos;

  private Context mContext;

  private boolean shouldLoadFromFavorite = false;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_movie_details);

    setTitle(R.string.movie_details_header);
    mContext = this;

    mMovieTitle = (TextView) findViewById(R.id.tv_movie_title);
    mMoviePoster = (ImageView) findViewById(R.id.iv_movie_poster);
    mErrorMessage = (TextView) findViewById(R.id.tv_error_message);
    mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

    mMovieYear = (TextView) findViewById(R.id.tv_movie_year);
    mMovieRating = (TextView) findViewById(R.id.tv_movie_rating);
    mMovieSynopsis = (TextView) findViewById(R.id.tv_movie_synopsis);
    mMovieDuration = (TextView) findViewById(R.id.tv_movie_duration);
    mMovieReleaseDate = (TextView) findViewById(R.id.tv_movie_release_date);
    mHorizontalLine = (ImageView) findViewById(R.id.iv_horizontal);

    Intent intentThatStartedThisActivity = getIntent();

    if (intentThatStartedThisActivity != null) {
      if (intentThatStartedThisActivity.hasExtra(MOVIE_NAME_EXTRA)) {
        String movieName = intentThatStartedThisActivity.getStringExtra(MOVIE_NAME_EXTRA);
        mMovieTitle.setText(movieName);
      }
      if (intentThatStartedThisActivity.hasExtra(MOVIE_ID_EXTRA)) {
        mMovieId = intentThatStartedThisActivity.getIntExtra(MOVIE_ID_EXTRA,-1);
      }
      if (intentThatStartedThisActivity.hasExtra(MOVIE_IS_FAVORITE)) {
        shouldLoadFromFavorite = intentThatStartedThisActivity.getBooleanExtra(MOVIE_IS_FAVORITE, false);
      }

    }

    DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
    if (displayMetrics.density < 2.4f){
      mLoadBigImage = true;
    }

    if (mMovieId <= 0){
      showErrorMessage("Invalid Movie Id");
      return;
    }

    mCheckBoxFavorite = (CheckBox) findViewById(R.id.cb_favorite);

    boolean isFavorite = checkHasFavorite();
    mCheckBoxFavorite.setChecked(isFavorite);
    mCheckBoxFavorite.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        mCheckBoxFavorite.setEnabled(false);
        if (mMovieId < 0){
          Toast.makeText(mContext, R.string.favorite_null_movie_id,Toast.LENGTH_SHORT).show();
          return;
        }
        toggleFavorite(mCheckBoxFavorite.isChecked());
        new android.os.Handler().postDelayed(
          new Runnable() {
            public void run() {
              mCheckBoxFavorite.setEnabled(true);
            }
          },
        2000);
      }
    });
    if (!isFavorite){
      if (shouldLoadFromFavorite){
        shouldLoadFromFavorite = false;
      }
    }

    new LoadMovieDetailsTask().execute(mMovieId);

    mListViewVideoTrailers = (ListView) findViewById(R.id.lv_video_trailers);
    mAdapterVideoTrailers = new MovieVideoItemAdapter(this, R.layout.movie_poster_item);
    mListViewVideoTrailers.setAdapter(mAdapterVideoTrailers);
    mVideoTrailersHeader = (TextView) findViewById(R.id.tv_trailers_header);
    mListViewVideoTrailers.setOnItemClickListener(new VideoClickListener(mAdapterVideoTrailers.mVideoData));
    //prevent it from auto scrolling down.
    mListViewVideoTrailers.setFocusable(false);

    mListViewVideoOthers = (ListView) findViewById(R.id.lv_video_others);
    mAdapterVideoOthers = new MovieVideoItemAdapter(this, R.layout.movie_poster_item);
    mListViewVideoOthers.setAdapter(mAdapterVideoOthers);
    mVideoOthersHeader = (TextView) findViewById(R.id.tv_other_videos_header);
    mListViewVideoOthers.setOnItemClickListener(new VideoClickListener(mAdapterVideoOthers.mVideoData));
    //prevent it from auto scrolling down.
    mListViewVideoOthers.setFocusable(false);

    mLoadingVideos = (ProgressBar) findViewById(R.id.pb_videos);

    if(savedInstanceState == null) {
      new LoadMovieVideosTask().execute(mMovieId);
    }
  }


  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putParcelableArrayList(MOVIE_VIDEO_TRAILER_STATE, mAdapterVideoTrailers.mVideoData);
    outState.putParcelableArrayList(MOVIE_VIDEO_OTHER_STATE, mAdapterVideoOthers.mVideoData);
  }

  @Override
  public void onRestoreInstanceState(Bundle savedInstanceState) {
    super.onRestoreInstanceState(savedInstanceState);
    if (savedInstanceState.containsKey(MOVIE_VIDEO_TRAILER_STATE)) {
      ArrayList<VideoInformation> videoTrailers = savedInstanceState.getParcelableArrayList(MOVIE_VIDEO_TRAILER_STATE);
      mAdapterVideoTrailers.setData(videoTrailers);
      forceFullHeightListViews(mListViewVideoTrailers);
    }
    if (savedInstanceState.containsKey(MOVIE_VIDEO_OTHER_STATE)) {
    ArrayList<VideoInformation> videoOthers = savedInstanceState.getParcelableArrayList(MOVIE_VIDEO_OTHER_STATE);
    mAdapterVideoOthers.setData(videoOthers);
    forceFullHeightListViews(mListViewVideoOthers);
    }
  }

  private void showErrorMessage(String message) {
    mLoadingIndicator.setVisibility(View.GONE);
    mErrorMessage.setVisibility(View.VISIBLE);
    mErrorMessage.setText(message);
  }

  public class VideoClickListener implements ListView.OnItemClickListener{
    ArrayList<VideoInformation> mReferenceData;
    public VideoClickListener(ArrayList<VideoInformation> referenceData){
      mReferenceData = referenceData;
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      VideoInformation video = mReferenceData.get(position);
      if (video != null){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(video.videoUrl()));

        if(intent.resolveActivity(getPackageManager()) != null){
          startActivity(intent);
        }
      }
    }
  }



  /**
   * Run the function to get the ArrayList of MovieInformation objects.
   */
  private class LoadMovieDetailsTask extends AsyncTask<Integer, Void, MovieInformation> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    protected MovieInformation doInBackground(Integer... movieId) {
      Cursor cursor = null;
      try {
        if (shouldLoadFromFavorite){

          String whereClause = MovieContract.MovieInformationEntry.COLUMN_MOVIE_ID + " = ?";
          String[] whereArgs = new String[] {
                  String.valueOf(movieId[0])
          };

          cursor = mContext.getContentResolver().query(
                  MovieContract.MovieInformationEntry.CONTENT_URI,
                  null,
                  whereClause,
                  whereArgs,
                  null
                  );
          if (cursor != null && cursor.moveToFirst()){
            String movieJson =  cursor.getString(cursor.getColumnIndex(MovieContract.MovieInformationEntry.COLUMN_MOVIE_JSON));
            return new MovieInformation(new JSONObject(movieJson), false);
          }
          return null;
        }else{
          return TheMovieDbApi.GetMovieById(movieId[0]);
        }
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      } finally {
        if (cursor != null){
          cursor.close();
        }
      }
    }



    @Override
    protected void onPostExecute(MovieInformation movieData) {
      mLoadingIndicator.setVisibility(View.GONE);
      if (movieData != null) {
        movie = movieData;

        String voteString = String.format(Locale.US, "%.1f/10 (%d votes)", movieData.voteAverage, movieData.voteCount);
        mMovieRating.setText(voteString);
        mMovieDuration.setText(String.format(Locale.US,"%dmin", movieData.duration));
        mMovieSynopsis.setText(movieData.plotSynopsis);

        if (movieData.releaseDate != null){
          SimpleDateFormat df = new SimpleDateFormat("yyyy", Locale.getDefault());
          mMovieYear.setText(df.format(movieData.releaseDate));
          DateFormat dfRelease = SimpleDateFormat.getDateInstance();

          mMovieReleaseDate.setText(String.format(Locale.US, "Release Date: %s", dfRelease.format(movieData.releaseDate)));
        }

        if (shouldLoadFromFavorite){
          new NetworkUtils.LoadThumbnail(mContext, mMoviePoster, movie).execute();
        }else{
          String loadUrl;
          if (mLoadBigImage){
            loadUrl = movieData.posterUrlLarge;
          }else{
            loadUrl = movieData.posterUrlSmall;
          }
          Picasso.with(getBaseContext()).load(loadUrl).into(mMoviePoster, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
            }
            @Override
            public void onError() {
            }
          });
        }


      } else {
        showErrorMessage("Unable to fetch Movie Data from ID");
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.movie_detail_menu, menu);

    return true;
  }


  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    if (id == R.id.action_reviews) {

      Class destinationClass = MovieReviewActivity.class;
      Intent intentToStartReviewActivity = new Intent(this, destinationClass);
      intentToStartReviewActivity.putExtra(MovieReviewActivity.MOVIE_ID_EXTRA, mMovieId);
      this.startActivity(intentToStartReviewActivity);

      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  private void toggleFavorite(final boolean shouldAddFavorite){
    new AsyncTask<Void, Void, String>() {
      protected String doInBackground(Void... params) {
        ContentResolver resolver = mContext.getContentResolver();
        try {
          if (!shouldAddFavorite) {
            //remove from favorite

            resolver.delete(MovieContract.MovieInformationEntry.CONTENT_URI, MovieContract.MovieInformationEntry.COLUMN_MOVIE_ID + "=" + mMovieId, null);
            resolver.delete(MovieContract.MoviePosterEntry.CONTENT_URI, MovieContract.MoviePosterEntry.COLUMN_MOVIE_ID + "=" + mMovieId, null);

            return "Movie removed from favorites.";
          } else {
            //favorite
            String movieJson = TheMovieDbApi.GetMovieJsonById(mMovieId);

            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieInformationEntry.COLUMN_MOVIE_ID, mMovieId);
            cv.put(MovieContract.MovieInformationEntry.COLUMN_MOVIE_JSON, movieJson);

            resolver.insert(MovieContract.MovieInformationEntry.CONTENT_URI, cv);

            MovieInformation movie = new MovieInformation(new JSONObject(movieJson), false);
            byte[] imageData;

            imageData = NetworkUtils.getBitmapBytesFromURL(movie.posterUrlLarge);
            ContentValues cvPosterBig = new ContentValues();
            cvPosterBig.put(MovieContract.MoviePosterEntry.COLUMN_MOVIE_ID, mMovieId);
            cvPosterBig.put(MovieContract.MoviePosterEntry.COLUMN_POSTER_BYTES, imageData);

            mContext.getContentResolver().insert(MovieContract.MoviePosterEntry.CONTENT_URI, cvPosterBig);

            return "Movie saved to favorites.";
          }
        }catch(Exception ex){
          ex.printStackTrace();
          return "An error has occurred";
        }
      }
      protected void onPostExecute(String msg) {
        Toast.makeText(mContext, msg ,Toast.LENGTH_SHORT).show();
      }
    }.execute();

  }

  private boolean checkHasFavorite(){

    String whereClause = MovieContract.MovieInformationEntry.COLUMN_MOVIE_ID + " = ?";
    String[] whereArgs = new String[] {
            String.valueOf(mMovieId)
    };

    Cursor cursor = getContentResolver().query(MovieContract.MovieInformationEntry.CONTENT_URI,
            new String[] {"COUNT(*) AS count"},
            whereClause,
            whereArgs,
            null);

    boolean result = false;
    if (cursor != null && cursor.moveToFirst()){
      result = cursor.getInt(0) > 0;
    }

    if (cursor != null){
      cursor.close();
    }

    return result;
  }



  /**
   * Run function to get every video for a movie.
   */
  private class LoadMovieVideosTask extends AsyncTask<Integer, Void, ArrayList<VideoInformation>> {

    @Override
    protected void onPreExecute() {
      super.onPreExecute();
      mLoadingVideos.setVisibility(View.VISIBLE);
    }

    @Override
    protected ArrayList<VideoInformation> doInBackground(Integer... params) {
      try {
        return TheMovieDbApi.GetMovieVideos(params[0]);
      } catch (Exception e) {
        e.printStackTrace();
        return null;
      }
    }

    @Override
    protected void onPostExecute(ArrayList<VideoInformation> videoData) {
      mLoadingVideos.setVisibility(View.GONE);
      if (videoData != null) {
        VideoInformation.VideoInformationNameSorter sorter = new VideoInformation.VideoInformationNameSorter();
        Collections.sort(videoData, sorter);
        ArrayList<VideoInformation> videoTrailers = new ArrayList<>();
        ArrayList<VideoInformation> videoOthers = new ArrayList<>();

        if (videoData.size() > 0) {
          mHorizontalLine.setVisibility(View.VISIBLE);
        }
        for (VideoInformation video : videoData){
          if(video.type.equals("Trailer")) {
            videoTrailers.add(video);
          }else{
            videoOthers.add(video);
          }
        }
        if (videoTrailers.size() > 0) {
          mAdapterVideoTrailers.setData(videoTrailers);
          forceFullHeightListViews(mListViewVideoTrailers);
          mVideoTrailersHeader.setVisibility(View.VISIBLE);
        }else{
          //hide if none
          mListViewVideoTrailers.setVisibility(View.GONE);
          mVideoTrailersHeader.setVisibility(View.GONE);
        }
        if (videoOthers.size() > 0) {
          mAdapterVideoOthers.setData(videoOthers);
          forceFullHeightListViews(mListViewVideoOthers);
          mListViewVideoOthers.setVisibility(View.VISIBLE);
        }else{
          //hide if none
          mListViewVideoOthers.setVisibility(View.GONE);
          mVideoOthersHeader.setVisibility(View.GONE);
        }
      } else {
      }
    }
  }

  /*
    I really wanted the trailer list to just scroll with the rest of the screen,
    and not squash itself into a tiny scrollable section.
    For now, I resorted to something I found
    https://stackoverflow.com/questions/1778485/android-listview-display-all-available-items-without-scroll-with-static-header
   */
  private static void forceFullHeightListViews(ListView listView){
    ListAdapter listAdapter = listView.getAdapter();
    if (listAdapter != null) {

      int numberOfItems = listAdapter.getCount();

      // Get total height of all items.
      int totalItemsHeight = 0;
      for (int itemPos = 0; itemPos < numberOfItems; itemPos++) {
        View item = listAdapter.getView(itemPos, null, listView);
        item.measure(0, 0);
        totalItemsHeight += item.getMeasuredHeight();
      }

      // Get total height of all item dividers.
      int totalDividersHeight = listView.getDividerHeight() *
              (numberOfItems - 1);

      // Set list height.
      ViewGroup.LayoutParams params = listView.getLayoutParams();
      params.height = totalItemsHeight + totalDividersHeight;
      listView.setLayoutParams(params);
      listView.requestLayout();
    }

  }
}
