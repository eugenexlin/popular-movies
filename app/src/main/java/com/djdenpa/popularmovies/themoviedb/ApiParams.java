package com.djdenpa.popularmovies.themoviedb;

/**
 * Created by denpa on 7/1/2017.
 */

public class ApiParams {
  public int page = 1;
  public MovieSort sort = MovieSort.POPULARITY;

  public enum MovieSort{
    POPULARITY,
    RATING
  }

}
