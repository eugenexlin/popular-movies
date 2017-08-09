package com.djdenpa.popularmovies;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by denpa on 8/6/2017.
 */

class MovieReviewViewHolder extends RecyclerView.ViewHolder {

  TextView mReviewTitle;
  TextView mReviewAuthor;
  TextView mReviewAuthorFirstChar;
  TextView mReviewContent;


  public MovieReviewViewHolder(View itemView) {
    super(itemView);

    mReviewTitle = (TextView) itemView.findViewById(R.id.tv_review_title);
    mReviewAuthor = (TextView) itemView.findViewById(R.id.tv_review_author);
    mReviewAuthorFirstChar = (TextView) itemView.findViewById(R.id.tv_author_first_char);
    mReviewContent = (TextView) itemView.findViewById(R.id.tv_review_content);
  }

}
