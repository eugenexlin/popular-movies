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
 */

public class TheMovieDbApi {

  public static final String THE_MOVIE_DB_BASE_URL = "https://api.themoviedb.org/3/";
  public static final String THE_MOVIE_DB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
  public static final String THE_MOVIE_DB_API_KEY = BuildConfig.THE_MOVIE_DB_API_KEY;

  final static String QUERY_PARAM_API_KEY = "api_key";

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

    URL url = null;
    ArrayList<MovieInformation> result = new ArrayList<>();
    try {
      url = new URL(discoverUri.toString());
      String httpResult = getResponseFromHttpUrl(url);
      JSONObject discoverJson = new JSONObject(httpResult);

      JSONArray resultsJson = discoverJson.getJSONArray("results");

      for(int i=0; i<resultsJson.length(); i++){
        JSONObject movieJson = resultsJson.getJSONObject(i);
        result.add(new MovieInformation(movieJson));
      }

      //int test = 1/0;

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

  /** Builds a URL with the API key set.
   * @param path relative path after base URL. does not need starting slash.
   * @param params can be null
   * **/
  protected static Uri BuildTheMovieDbUrl(String path, Map<String,String> params){
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
   * HTTP GET
   * @param url
   * @return
   * @throws IOException
   */
  public static String getResponseFromHttpUrl(URL url) throws IOException {
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
