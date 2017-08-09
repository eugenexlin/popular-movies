package com.djdenpa.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.djdenpa.popularmovies.themoviedb.ReviewInformation;

import java.util.ArrayList;

/**
 * Created by denpa on 8/6/2017.
 *
 * View for the endless reviews.
 */

public class MovieReviewItemAdapter extends RecyclerView.Adapter<MovieReviewViewHolder> {

  public final ArrayList<ReviewInformation> mReviewData = new ArrayList<>();

  private Context mContext = null;

  @Override
  public MovieReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
    mContext = viewGroup.getContext();
    int layoutIdForListItem = R.layout.review_item_layout;
    LayoutInflater inflater = LayoutInflater.from(mContext);

    View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
    return new MovieReviewViewHolder(view);
  }
  @Override
  public void onBindViewHolder(MovieReviewViewHolder holder, int position) {
    ReviewInformation review = mReviewData.get(position);

    holder.mReviewAuthor.setText(review.author);
    if (review.author == ""){
      holder.mReviewAuthorFirstChar.setText(review.author.charAt(0));
    }
    //no title... maybe some other day
    //holder.mReviewTitle.setText(review.content);
    holder.mReviewContent.setText(review.content);

  }

  @Override
  public int getItemCount() {
    return 0;
  }
}
