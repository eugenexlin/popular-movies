package com.djdenpa.popularmovies;


import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.djdenpa.popularmovies.themoviedb.VideoInformation;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by denpa on 8/6/2017.
 *
 */

public class MovieVideoItemAdapter extends ArrayAdapter<VideoInformation> {

  public final ArrayList<VideoInformation> mVideoData = new ArrayList<>();

  public MovieVideoItemAdapter(@NonNull Context context, @LayoutRes int resource) {
    super(context, resource);
  }

  public void setData(ArrayList<VideoInformation> videoData){
    mVideoData.clear();
    for (VideoInformation video: videoData){
      mVideoData.add(video);
    }
    notifyDataSetChanged();
  }

  private Context mContext = null;
  private MoviePosterViewHolder mViewHolder;

  public View getView(int position, View convertView, ViewGroup parent) {

    if (convertView == null) {
      convertView = LayoutInflater.from(this.getContext())
              .inflate(R.layout.video_item_layout, parent, false);

      mViewHolder = new MoviePosterViewHolder(convertView);

      convertView.setTag(mViewHolder);
    } else {
      mViewHolder = (MoviePosterViewHolder) convertView.getTag();
    }

    VideoInformation video = mVideoData.get(position);
    if (video != null) {

      mViewHolder.name.setText(video.name);

      String thumbnailUrl = video.thumbnailUrl();
      if (thumbnailUrl != ""){
        Picasso.with(mContext).load(video.thumbnailUrl()).into(mViewHolder.mThumbnail, new com.squareup.picasso.Callback() {
          @Override
          public void onSuccess() {
          }
          @Override
          public void onError() {
          }
        });
      }
    }

    return convertView;
  }

  @Override
  public int getCount() {
    return mVideoData.size();
  }

  class MoviePosterViewHolder{

    ImageView mThumbnail;
    TextView name;

    public MoviePosterViewHolder(View itemView) {
      mThumbnail = (ImageView) itemView.findViewById(R.id.iv_video_thumb);
      name = (TextView) itemView.findViewById(R.id.tv_video_name);

    }

  }

}
