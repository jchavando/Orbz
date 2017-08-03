package com.ruppal.orbz;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.ruppal.orbz.database.PlaylistTable;
import com.ruppal.orbz.models.Player;
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
    PlaylistSimpleAdapterListener mPlaylistSimpleListener;
    Context context ;
    private final int TYPE_SONG = 110;
    private final int TYPE_PLAYLIST = 111;
    private final int TYPE_PLAYLIST_SIMPLE = 112;
    private final int TYPE_QUEUE = 113;

    //define an interface required by the ViewHolder
    public interface SongAdapterListener{
        public void onItemSelected (View view, int position);
        public void onItemLongSelected (View view, int position);
        public void onAddPlaylistSongClicked(View view, int position);
        public void onAddCommentClicked(View view, int position);
    }

    public interface SongAdapterListenerGroupQueue extends SongAdapterListener{}
    public interface SongAdapterListenerPlaylist extends SongAdapterListener{}

    public interface PlaylistAdapterListener{
        public void onPlaylistItemSelected (View view, int position);
    }

    public interface PlaylistSimpleAdapterListener{
        public void onPlaylistSimpleItemSelected (View view, int position);
    }

    public interface AddSongToPlaylistAdapterListener{
        public void addSongToPlaylist(Song song, PlaylistTable playlist);
    }

    public interface AddCommentAdapterListener{
        public void addComment (String comment, Song song); //int position
    }

    // Provide a suitable constructor (depends on the kind of dataset) //List<Object>
    public ComplexRecyclerViewAdapter(List<Object> songsAndPlaylists, SongAdapterListener listener,
                                      PlaylistAdapterListener playlistAdapterListener,
                                      PlaylistSimpleAdapterListener playlistSimpleAdapterListener) {
        this.mSongsPlaylists = songsAndPlaylists;
        this.mPlaylistListener = playlistAdapterListener;
        this.mListener = listener;
        this.mPlaylistSimpleListener = playlistSimpleAdapterListener;
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
            case TYPE_PLAYLIST_SIMPLE:
                View playlistViewSimple = inflater.inflate(R.layout.item_choose_playlist, viewGroup, false);
                viewHolder = new ViewHolderPlaylistSimple(playlistViewSimple, mPlaylistSimpleListener, context);
                break;
            case TYPE_QUEUE:
                View queueView = inflater.inflate(R.layout.item_queue, viewGroup, false);
                viewHolder = new ViewHolderQueue(queueView, mListener, context);
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
            if (((Song) mSongsPlaylists.get(position)).getQueued()) return TYPE_QUEUE;
            else return TYPE_SONG;
        }
        else if(mSongsPlaylists.get(position) instanceof Playlist){
            return TYPE_PLAYLIST;
        }
        else if(mSongsPlaylists.get(position) instanceof PlaylistTable){
            return TYPE_PLAYLIST_SIMPLE;
        }
        return -1;
    }

    private void configureViewHolderSong(ViewHolderSong holder, int position) {
        //get the data according to position
        final Song song = (Song) mSongsPlaylists.get(position); //returns song object

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
            switch (song.getService()){
                case Song.LOCAL :
                    Drawable image = Drawable.createFromPath(song.getAlbumCoverUrl());
                    holder.ivAlbumCover.setImageDrawable(image);
                    break;
                default:
                    Glide.with(context)
                            .load(song.getAlbumCoverUrl())
                            .into(holder.ivAlbumCover);
                    Log.i("albumArt", song.getAlbumCoverUrl());
                    break;
            }
        } else {
            holder.ivAlbumCover.setImageResource(R.drawable.blacklogomissingalbum);
        }

        holder.tvService.setText(song.getService());
        if (song.isPlaying() || (Player.getCurrentlyPlayingSong()!=null &&
                Player.getCurrentlyPlayingSong().getService().equals(song.getService())
                && Player.getCurrentlyPlayingSong().getUid().equals(song.getUid()))){
//            holder.songRelativeLayout.setBackgroundColor(Color.MAGENTA);
            holder.tvArtistName.setTextColor(ContextCompat.getColor(context, R.color.songPlaying));
            holder.tvSongName.setTextColor(ContextCompat.getColor(context, R.color.songPlaying));
        }
        else {
            holder.tvArtistName.setTextColor(Color.WHITE);
            holder.tvSongName.setTextColor(Color.WHITE);
        }
    }


    private void configureViewHolderQueue(ViewHolderQueue queueHolder, ViewHolderSong songHolder, int position) {
        //add ViewHolderSong songHolder
        configureViewHolderSong(songHolder, position);
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

    private void configureViewHolderPlaylistSimple (ViewHolderPlaylistSimple holder, int position){
        PlaylistTable playlistTable = (PlaylistTable) mSongsPlaylists.get(position);

        holder.tvPlaylistName.setText(playlistTable.getPlaylistName());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case TYPE_SONG:
                ViewHolderSong viewHolderSong = (ViewHolderSong) holder; //
                configureViewHolderSong(viewHolderSong, position);
                break;
            case TYPE_PLAYLIST:
                ViewHolderPlaylist viewHolderPlaylist = (ViewHolderPlaylist) holder;
                configureViewHolderPlaylist(viewHolderPlaylist, position);
                break;
            case TYPE_PLAYLIST_SIMPLE:
                ViewHolderPlaylistSimple viewHolderPlaylistSimple = (ViewHolderPlaylistSimple) holder;
                configureViewHolderPlaylistSimple(viewHolderPlaylistSimple, position);
                break;
            case TYPE_QUEUE:
                ViewHolderSong viewHolderSong2 = (ViewHolderSong) holder;
                ViewHolderQueue viewHolderQueue = (ViewHolderQueue) holder;
                configureViewHolderQueue(viewHolderQueue, viewHolderSong2, position);
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
