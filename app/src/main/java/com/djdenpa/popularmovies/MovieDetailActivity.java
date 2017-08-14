package com.djdenpa.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.djdenpa.popularmovies.database.MovieContract;
import com.djdenpa.popularmovies.database.MovieDbHelper;
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

  public static final String MOVIE_NAME_EXTRA = "MovieDetailActivity_MOVIE_NAME_EXTRA";
  public static final String MOVIE_ID_EXTRA = "MovieDetailActivity_MOVIE_ID_EXTRA";

  private TextView mMovieTitle;
  private TextView mErrorMessage;
  private ImageView mMoviePoster;
  private ProgressBar mLoadingIndicator;

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

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    if (getResources().getConfiguration().orientation == 	android.content.res.Configuration.ORIENTATION_LANDSCAPE){
      setContentView(R.layout.activity_movie_details_landscape);
    }else{
      setContentView(R.layout.activity_movie_details);
    }

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
    }

    DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();
    if (displayMetrics.density < 2.4f){
      mLoadBigImage = true;
    }

    if (mMovieId <= 0){
      showErrorMessage("Invalid Movie Id");
      return;
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

    new LoadMovieVideosTask().execute(mMovieId);

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


      } else {
        showErrorMessage("Unable to fetch Movie Data from ID");
      }
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.movie_detail_menu, menu);

    MovieDbHelper dbHelper = new MovieDbHelper(mContext);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    MenuItem menuFav = menu.findItem(R.id.action_favorite);
    if (checkHasFavorite(db)){
      menuFav.setTitle(R.string.action_remove_favorite);
    }
    db.close();

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
    if (id == R.id.action_favorite) {
      if (mMovieId < 0){
        Toast.makeText(this, R.string.favorite_null_movie_id,Toast.LENGTH_SHORT).show();
        return true;
      }
      //avoid spam
      View menuView = findViewById(id);
      menuView.setEnabled(false);
      toggleFavorite();
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  public void toggleFavorite(){
    new AsyncTask<Void, Void, String>() {
      protected String doInBackground(Void... params) {
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        try {
          if (checkHasFavorite(db)) {
            //remove from favorite
            db.delete(MovieContract.MovieInformationEntry.TABLE_NAME, MovieContract.MovieInformationEntry.COLUMN_MOVIE_ID + "=" + mMovieId, null);
            db.delete(MovieContract.MoviePosterEntry.TABLE_NAME, MovieContract.MoviePosterEntry.COLUMN_MOVIE_ID + "=" + mMovieId, null);
            return "Movie removed from favorites.";
          } else {
            //favorite
            String movieJson = TheMovieDbApi.GetMovieJsonById(mMovieId);

            ContentValues cv = new ContentValues();
            cv.put(MovieContract.MovieInformationEntry.COLUMN_MOVIE_ID, mMovieId);
            cv.put(MovieContract.MovieInformationEntry.COLUMN_MOVIE_JSON, movieJson);
            db.insert(MovieContract.MovieInformationEntry.TABLE_NAME, null, cv);

            MovieInformation movie = new MovieInformation(new JSONObject(movieJson), false);
            byte[] imageData;

            //we might not need the low res one, all high res whee
//            imageData = NetworkUtils.getBitmapBytesFromURL(movie.posterUrlSmall);
//            ContentValues cvPoster = new ContentValues();
//            cvPoster.put(MovieContract.MoviePosterEntry.COLUMN_MOVIE_ID, mMovieId);
//            cvPoster.put(MovieContract.MoviePosterEntry.COLUMN_POSTER_PATH, movie.posterUrlSmall);
//            cvPoster.put(MovieContract.MoviePosterEntry.COLUMN_POSTER_BYTES, imageData);
//            db.insert(MovieContract.MoviePosterEntry.TABLE_NAME, null, cvPoster);

            imageData = NetworkUtils.getBitmapBytesFromURL(movie.posterUrlLarge);
            ContentValues cvPosterBig = new ContentValues();
            cvPosterBig.put(MovieContract.MoviePosterEntry.COLUMN_MOVIE_ID, mMovieId);
            cvPosterBig.put(MovieContract.MoviePosterEntry.COLUMN_POSTER_PATH, movie.posterUrlLarge);
            cvPosterBig.put(MovieContract.MoviePosterEntry.COLUMN_POSTER_BYTES, imageData);
            db.insert(MovieContract.MoviePosterEntry.TABLE_NAME, null, cvPosterBig);

            return "Movie saved to favorites.";
          }
        }catch(Exception ex){
          ex.printStackTrace();
          return "An error has occurred";
        }finally{
          db.close();
        }
      }
      protected void onPostExecute(String msg) {
        Toast.makeText(mContext, msg ,Toast.LENGTH_SHORT).show();
      }
    }.execute();

  }

  public boolean checkHasFavorite(SQLiteDatabase db){
    String countQuery = MovieContract.MovieInformationEntry.TABLE_NAME + " WHERE " +MovieContract.MovieInformationEntry.COLUMN_MOVIE_ID + " = " + mMovieId;
    long cnt =  DatabaseUtils.queryNumEntries(db, countQuery);
    return cnt >= 1;
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
  public static void forceFullHeightListViews(ListView listView){
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
