package com.djdenpa.popularmovies.themoviedb;

import android.net.Uri;

import com.djdenpa.popularmovies.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by denpa on 7/1/2017.
 *
 * Class to handle pulling information from Movie API, and returning objects.
 */

public class TheMovieDbApi {

  public static final String THE_MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/";
  public static final String THE_MOVIE_DB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
  public static final String THE_MOVIE_DB_API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;

  final static String QUERY_PARAM_API_KEY = "api_key";

  /**
   * Takes in parameters, and returns an ArrayList of movies
   */
  public static ArrayList<MovieInformation> QueryMovies(ApiParams params){
    HashMap<String,String> queryString = new HashMap<>();
    queryString.put("page", String.valueOf(params.page));
    String path = "";
    switch(params.sort){
      case POPULARITY:
        path = "movie/popular";
        break;
      case RATING:
        path = "movie/top_rated";
        break;
    }
    Uri discoverUri = BuildTheMovieDbUrl(path, queryString);

    URL url;
    ArrayList<MovieInformation> result = new ArrayList<>();
    try {
      url = new URL(discoverUri.toString());
      String httpResult = getResponseFromHttpUrl(url);
      JSONObject moviesJson = new JSONObject(httpResult);

      JSONArray resultsJson = moviesJson.getJSONArray("results");

      for(int i=0; i<resultsJson.length(); i++){
        JSONObject movieJson = resultsJson.getJSONObject(i);
        result.add(new MovieInformation(movieJson, true));
      }

    } catch (Exception e) {
      //For the sake of brevity, I will capture all exceptions and log it.
      //This is one of the things that should work very well after it is in place.
      //There was an endless possibility of exceptions,
      //and this could be a place where catching all exceptions is acceptable
      e.printStackTrace();
      return null;
    }
    return result;
  }

  /**
   * takes in id, and returns a single MovieInformation object
   */
  public static MovieInformation GetMovieById(int id){
    HashMap<String,String> queryString = new HashMap<>();
    String path = "movie/" + id;
    Uri discoverUri = BuildTheMovieDbUrl(path, queryString);

    URL url;
    try {
      url = new URL(discoverUri.toString());
      String httpResult = getResponseFromHttpUrl(url);
      JSONObject movieJson = new JSONObject(httpResult);

      return new MovieInformation(movieJson, false);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * There seems to be no paging on this endpoint
   */
  public static ArrayList<VideoInformation> GetMovieVideos(int movieId){
    String path = "movie/" + movieId + "/videos";
    Uri videosUri = BuildTheMovieDbUrl(path, null);

    URL url;
    ArrayList<VideoInformation> result = new ArrayList<>();
    try {
      url = new URL(videosUri.toString());
      String httpResult = getResponseFromHttpUrl(url);
      JSONObject videosJson = new JSONObject(httpResult);

      JSONArray resultsJson = videosJson.getJSONArray("results");

      for(int i=0; i<resultsJson.length(); i++){
        JSONObject videoJson = resultsJson.getJSONObject(i);
        result.add(new VideoInformation(videoJson));
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return result;
  }

  /**
   * There is paging..
   */
  public static ArrayList<ReviewInformation> GetMovieReviews(int movieId, int page){
    HashMap<String,String> queryString = new HashMap<>();
    queryString.put("page", String.valueOf(page));

    String path = "movie/" + movieId + "/reviews";
    Uri videosUri = BuildTheMovieDbUrl(path, null);

    URL url;
    ArrayList<ReviewInformation> result = new ArrayList<>();
    try {
      url = new URL(videosUri.toString());
      String httpResult = getResponseFromHttpUrl(url);
      JSONObject reviewsJson = new JSONObject(httpResult);

      JSONArray resultsJson = reviewsJson.getJSONArray("results");

      for(int i=0; i<resultsJson.length(); i++){
        JSONObject videoJson = resultsJson.getJSONObject(i);
        result.add(new ReviewInformation(videoJson));
      }
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
    return result;
  }

  /** Builds a URL with the API key set.
   * params = null for no query string params
   * @param path relative path after base URL. does not need starting slash.
   * @param params can be null
   * **/
  private static Uri BuildTheMovieDbUrl(String path, Map<String,String> params){
    if (params == null){
      params = new HashMap<>();
    }
    if (!params.containsKey(QUERY_PARAM_API_KEY)){
      params.put(QUERY_PARAM_API_KEY,THE_MOVIE_DB_API_KEY);
    }

    Uri.Builder builder = Uri.parse(THE_MOVIE_DB_BASE_URL + path).buildUpon();

    for(Map.Entry<String,String> param : params.entrySet()){
      builder = builder.appendQueryParameter(param.getKey(), param.getValue());
    }

    return builder.build();
  }

  /**
   * Taken from Udacity Lessons.
   * HTTP GET to return the string response
   */
  private static String getResponseFromHttpUrl(URL url) throws IOException {
    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
    try {
      InputStream in = urlConnection.getInputStream();

      Scanner scanner = new Scanner(in);
      scanner.useDelimiter("\\A");

      boolean hasInput = scanner.hasNext();
      if (hasInput) {
        return scanner.next();
      } else {
        return null;
      }
    } finally {
      urlConnection.disconnect();
    }
  }


}
