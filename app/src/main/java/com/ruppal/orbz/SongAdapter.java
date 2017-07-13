package com.ruppal.orbz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Song;

import java.util.List;



/**
 * Created by jchavando on 7/13/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private List<Song> mSongs;
    Context context;
    SpotifyClient client;

    private SongAdapterListener mListener;
    private final int REQUEST_CODE = 20;


    //define an interface required by the ViewHolder
    public interface SongAdapterListener{
        public void onItemSelected (View view, int position, boolean isPic);
    }
    //pass in the Tweets array in the constructor
    public SongAdapter(List<Song> tweets, SongAdapterListener listener) {
        mSongs = tweets;
        //client = TwitterApplication.getRestClient();
        mListener = listener;
    }

    //for each row, inflate layout and cache (pass) references into ViewHolder
    //only invoked when need to create new row, otherwise the adapter will call onBindViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        View tweetView = inflater.inflate(R.layout.item_song, parent, false);
        ViewHolder viewHolder = new ViewHolder(tweetView);

        return viewHolder;
    }

    //bind the values based on the position of the element
    //repopulate data based on position
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //get the data according to position
        final Song song = mSongs.get(position); //returns tweet object

        //populate the views according to this data

        holder.tvSongName.setText(song.title);
        String artistList = "";

        //print out all artists in song
        for (int i = 0; i < song.artists.size(); i++){
            artistList = artistList + song.artists.get(i);
        }
        holder.tvArtistName.setText(artistList);
        holder.tvSource.setText("spotify");



//        if(tweet.retweeted) {
//            holder.tvRetweetCount.setTextColor(greenColor);
//            holder.ibRetweet.setImageResource(R.drawable.retweet_green);
//        } else {
//            holder.tvRetweetCount.setTextColor(blackColor);
//            holder.ibRetweet.setImageResource(R.drawable.ic_vector_retweet_stroke);
//        }
//
//        if(tweet.favorited){
//            holder.tvFavoriteCount.setTextColor(redColor);
//            holder.ibFavorites.setImageResource(R.drawable.ic_heart_filled);
//
//        } else {
//            holder.tvFavoriteCount.setTextColor(blackColor);
//            holder.ibFavorites.setImageResource(R.drawable.ic_vector_heart_stroke);
//
//        }

//
//        String relativeShortTimeAgo = replaceTime(getRelativeTimeAgo(tweet.createdAt));
//        holder.tvRelativeTimeStamp.setText(" · " + relativeShortTimeAgo); //20 minutes ago
//
//        Glide.with(context).load(tweet.user.profileImageUrl).into(holder.ibProfileImage);
//
//        Glide.with(context).load(tweet.imageUrl).into(holder.ivPic);

//
//        holder.ibProfileImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mListener.onItemSelected(v, position, true);
//            }
//        });

    }


    @Override
    public int getItemCount() {
        return mSongs.size();
    }



    //create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSongName;
        public TextView tvArtistName;
        public TextView tvSource;
        public ImageButton ibPlus;




        public ViewHolder(View itemView) {
            super(itemView);
            //perform findViewById lookups
            tvSongName= (TextView) itemView.findViewById(R.id.tvSongName);
            tvArtistName = (TextView) itemView.findViewById(R.id.tvArtistName);
            tvSource = (TextView) itemView.findViewById(R.id.tvSource);
            ibPlus = (ImageButton) itemView.findViewById(R.id.ibPlus);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            //handle row click event
            //handle onClick to play song
//            itemView.setOnClickListener(new View.OnClickListener(){
//
//                @Override
//                public void onClick(View v) {
//                    if(mListener != null){
//                        //get the position of row element
//                        int position = getAdapterPosition();
//                        //fire the listener callback
//                        mListener.onItemSelected(v, position, false);
//                    }
//                }
//            });



            //Retweet
//            ibRetweet.setOnClickListener(new ImageButton.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int position = getAdapterPosition();
//
//                    //make sure the position is valid, actually exits in the view
//                    if (position != RecyclerView.NO_POSITION) {
//                        //get the movie at the position
//                        Tweet tweet = mTweets.get(position);
//                        client.retweet(tweet.uid, new JsonHttpResponseHandler() {
//
//                            @Override
//                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                                Log.d("reweet", "success");
//                                try {
//                                    tvRetweetCount.setText(String.valueOf(Tweet.fromJSON(response).retweetCount));
//                                    ibRetweet.setImageResource(R.drawable.retweet_green);
//                                    tvRetweetCount.setTextColor(greenColor);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                                Log.d("retweet", "error");
//                            }
//                        });
//                    }
//                }
//
//            });

        }
    }
    public void clear() {
        mSongs.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Song> list) {
        mSongs.addAll(list);
        notifyDataSetChanged();
    }



}
