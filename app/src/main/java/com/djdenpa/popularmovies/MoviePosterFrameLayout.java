package com.djdenpa.popularmovies;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

public class MoviePosterFrameLayout extends FrameLayout
{

  public final static double MOVIE_POSTER_ASPECT_RATIO = 40.0/27.0;

  public MoviePosterFrameLayout(Context context, AttributeSet attrs)
  {
    super(context);
  }

  @Override protected void onMeasure (int widthMeasureSpec, int heightMeasureSpec)
  {
    int originalWidth = MeasureSpec.getSize(widthMeasureSpec);
    int calculatedHeight = (int) (originalWidth * MOVIE_POSTER_ASPECT_RATIO);

    super.onMeasure(
            MeasureSpec.makeMeasureSpec(originalWidth, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec(calculatedHeight, MeasureSpec.EXACTLY));
  }
}
