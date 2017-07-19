package com.ruppal.orbz;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;

import java.util.List;



/**
 * Created by jchavando on 7/13/17.
 */

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder>{

    private List<Song> mSongs;
    private List<Object> mSongsPlaylists;
    Context context;
    SpotifyClient client;

    private SongAdapterListener mListener;
    private final int REQUEST_CODE = 20;
    private final static int TYPE_SONG = 110;
    private final static int TYPE_PLAYLIST = 111;


    //define an interface required by the ViewHolder
    public interface SongAdapterListener{
        public void onItemSelected (View view, int position, boolean isPic);
        public void onPauseButtonClicked(View view, int position);
    }

    public SongAdapter(List<Object> songsPlaylists, SongAdapterListener listener){
        mSongsPlaylists = songsPlaylists;
        mListener = listener;
    }
    //pass in the Tweets array in the constructor
//    public SongAdapter(List<Song> tweets, SongAdapterListener listener) {
//        mSongs = tweets;
//        //client = TwitterApplication.getRestClient();
//        mListener = listener;
//    }

    public int getItemViewType (int position){
        if (mSongsPlaylists.get(position) instanceof Song) {
            return TYPE_SONG;
        }
        else if(mSongsPlaylists.get(position) instanceof Playlist){
            return TYPE_PLAYLIST;
        }
        return -1;
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
        int sizeArtists = song.getArtists().size();
        for (int i = 0; i < sizeArtists; i++){
            artistList = artistList + song.artists.get(i).name;
            if (i < sizeArtists -1){
                artistList += ", ";
            }
        }
        holder.tvArtistName.setText(artistList);
        if (song.getAlbumCoverUrl() != null) {
            Glide.with(context)
                    .load(song.getAlbumCoverUrl())
                    .into(holder.ivAlbumCover);
            Log.i("albumArt", song.getAlbumCoverUrl());
        }

        holder.tvService.setText(song.getService());
    }


    @Override
    public int getItemCount() {
        return mSongs.size();
    }



    //create ViewHolder class
    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvSongName;
        public TextView tvArtistName;
        public ImageView ivAlbumCover;
        public ImageView ivPause;
        public TextView tvService;

        public ViewHolder(View itemView) {
            super(itemView);
            //perform findViewById lookups
            tvSongName= (TextView) itemView.findViewById(R.id.tvSongName);
            tvArtistName = (TextView) itemView.findViewById(R.id.tvArtistName);
            ivAlbumCover = (ImageView) itemView.findViewById(R.id.ivAlbumCover);
            ivPause = (ImageView) itemView.findViewById(R.id.ivPause);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
//                    Toast.makeText(context, mSongs.get(position).title, Toast.LENGTH_LONG).show();
                    mListener.onItemSelected(v, position, false);
                }
            });
            ivPause.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Drawable playButton = context.getResources().getDrawable(R.drawable.exo_controls_play);
                    ((ImageView) v).setImageDrawable(playButton);
                    int position = getAdapterPosition();
                    mListener.onPauseButtonClicked(v, position);

                }
            });
            tvService = (TextView) itemView.findViewById(R.id.tvService);


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
