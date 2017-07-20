package com.ruppal.orbz;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;

import java.util.List;

/**
 * Created by ruppal on 7/19/17.
 */

public class ComplexRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // The items to display in your RecyclerView
    private List<Object> mSongsPlaylists;
    private SongAdapterListener mListener;
    private PlaylistAdapterListener mPlaylistListener;
    Context context ;
    private final int TYPE_SONG = 110;
    private final int TYPE_PLAYLIST = 111;




    //define an interface required by the ViewHolder
    public interface SongAdapterListener{
        public void onItemSelected (View view, int position, boolean isPic);
        public void onPauseButtonClicked(View view, int position);
    }

    public interface PlaylistAdapterListener{
        public void onPlaylistItemSelected (View view, int position);
    }


    // Provide a suitable constructor (depends on the kind of dataset) //List<Object>
    public ComplexRecyclerViewAdapter(List<Object> songsAndPlaylists, SongAdapterListener listener, PlaylistAdapterListener playlistAdapterListener) {
        this.mSongsPlaylists = songsAndPlaylists;
        this.mPlaylistListener = playlistAdapterListener;
        this.mListener = listener;

    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        context = viewGroup.getContext();
        switch (viewType) {
            case TYPE_SONG:
                View songView = inflater.inflate(R.layout.item_song, viewGroup, false);
                viewHolder = new ViewHolderSong(songView, mListener, context);
                break;
            case TYPE_PLAYLIST:
                View playlistView = inflater.inflate(R.layout.item_playlist, viewGroup, false);
                viewHolder = new ViewHolderPlaylist(playlistView, mPlaylistListener, context);
                break;
            default:
                viewHolder = null;
                break;
        }
        return viewHolder;
    }

    @Override
    public int getItemViewType (int position){
        if (mSongsPlaylists.get(position) instanceof Song) {
            return TYPE_SONG;
        }
        else if(mSongsPlaylists.get(position) instanceof Playlist){
            return TYPE_PLAYLIST;
        }
        return -1;
    }

    private void configureViewHolderSong(ViewHolderSong holder, int position) {
        //get the data according to position
        final Song song = (Song) mSongsPlaylists.get(position); //returns song object

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


    private void configureViewHolderPlaylist (ViewHolderPlaylist holder, int position) {
        //get the data according to position
        final Playlist playlist = (Playlist) mSongsPlaylists.get(position);

        //populate the views according to this data
        holder.tvPlaylistName.setText(playlist.getPlaylistName());
        holder.tvOwnerName.setText(playlist.getOwner().getName());
        String imageUrl = playlist.getImage();
        Glide.with(context)
                .load(imageUrl)
                .into(holder.ivPlayListArt);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_SONG:
                ViewHolderSong viewHolderSong = (ViewHolderSong) holder;
                configureViewHolderSong(viewHolderSong, position);
                break;
            case TYPE_PLAYLIST:
                ViewHolderPlaylist viewHolderPlaylist = (ViewHolderPlaylist) holder;
                configureViewHolderPlaylist(viewHolderPlaylist, position);
                break;
            default:
                Log.e("viewholder", "didnt bind anything to viewholder");
                break;
        }
    }



    @Override
    public int getItemCount() {
        return this.mSongsPlaylists.size();
    }
}
