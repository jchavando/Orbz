package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jchavando on 7/26/17.
 */

public class PlaylistSongsFragment extends SongListFragment { //implements ComplexRecyclerViewAdapter.PlaylistAdapterListener


    //recycler view for when you click on individual playlist
    RecyclerView rvSongs;
    SpotifyClient spotifyClient;
    ArrayList<Object> songs;
    public ComplexRecyclerViewAdapter complexAdapter;
    public Player mPlayer;
    Playlist mPlaylist;
    SongListFragment songListFragment;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // insertNestedFragment();
        Bundle arguments = getArguments();
        mPlaylist = Parcels.unwrap(arguments.getParcelable("tracks"));

        if (mPlaylist.getPlaylistService().equals(Song.SPOTIFY)){
            loadTracksFromSpotify(mPlaylist.getTracksUrl());
        }
        else if (mPlaylist.getPlaylistService().equals(Song.LOCAL)){
            loadTracksFromLocal(mPlaylist);
        }


    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        // Need to define the child fragment layout
//        View v = inflater.inflate(R.layout.fragments_playlist_songs, container, false);

//
//       // return inflater.inflate(R.layout.fragments_playlist_songs, container, false);
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_playlist);
//        getSpotifyPlayer();
//        spotifyClient = new SpotifyClient();
//        songListFragment = new SongListFragment();
//        // Lookup the recyclerview in activity layout
//        rvSongs = (RecyclerView) v.findViewById(R.id.rvPlaylist);

//        // Initialize contacts
//        rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
//        //find RecyclerView
//
//        //construct adapter from datasource
//        complexAdapter = new ComplexRecyclerViewAdapter(songs, this, null, null);
//        //recyclerView setup (layout manager, use adapter)
//        //set the adapter
//        rvSongs.setAdapter(complexAdapter);
//
//        //unwrap playlist
//        //mPlaylist = (Playlist) Parcels.unwrap(getIntent().getParcelableExtra("tracks"));
//
//        //mPlaylist = Parcels.unwrap(savedInstanceState.getParcelable("tracks"));
//
//        Bundle arguments = getArguments();
//        mPlaylist = Parcels.unwrap(arguments.getParcelable("tracks"));
//
//        if (mPlaylist.getPlaylistService().equals(Song.SPOTIFY)){
//            loadTracksFromSpotify(mPlaylist.getTracksUrl());
//        }
//        else if (mPlaylist.getPlaylistService().equals(Song.LOCAL)){
//            loadTracksFromLocal(mPlaylist);
//        }
//
//
//        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        rvSongs.addItemDecoration(itemDecoration);
//
        //return v;
  //  }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        spotifyClient = new SpotifyClient();
        songs = new ArrayList<>();
        //songListFragment = new SongListFragment();
        //complexAdapter = new ComplexRecyclerViewAdapter(songs, this, null, null);
       // setHasOptionsMenu(true);
        //spotifyPlaylists = new ArrayList<>();
        //fabAddPlaylist.setOnClickListener(this); //TODO fix
        //setHasOptionsMenu(true);
        //addSongToPlaylistAdapterListener = this;

    }

//    public void addSong (Object song){
//        songs.add((Song) song);
//        complexAdapter.notifyItemInserted(songs.size()-1);
//    }

    public void loadTracksFromLocal(Playlist playlist){
        ArrayList<Song> tracks = playlist.getTracks();
        for (int i=0; i<tracks.size(); i++){
            addSong(tracks.get(i));
        }
    }

    public void loadTracksFromSpotify(String tracksUrl){
        //clear the song list
        //songs.clear();
        spotifyClient.getPlayListTracks(tracksUrl, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray items = response.getJSONArray("items");
                    for (int i =0 ; i < items.length(); i++){
                        JSONObject item = items.getJSONObject(i);
                        JSONObject track = item.getJSONObject("track");
                        Song song = Song.fromJSON(Song.SPOTIFY, track);
                        addSong(song);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("playlists", e.toString());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("playlists", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("playlists", errorResponse.toString());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("playlists", errorResponse.toString());
            }

        });
    }



    public void getSpotifyPlayer(){
        Config playerConfig = new Config(getContext(), SpotifyClient.accessToken, getString(R.string.spotify_client_id));
        mPlayer = Spotify.getPlayer(playerConfig, this, null);
    }
    public void playSongFromSpotify(Song song) {
        String playingNow = "playing " + song.getTitle() + " from spotify";
        Toast.makeText(getContext(), playingNow, Toast.LENGTH_LONG).show();
        mPlayer.playUri(null, "spotify:track:" + song.getUid(), 0, 0);
        song.playing = true;
    }

//
//
//    @Override
//    public void onAddPlaylistSongClicked(View view, int position) {
//
//    }
//
//    @Override
//    public void onItemSelected(View view, int position) {
//        Song song = (Song) songs.get(position);
//        if (song.getService().equals(Song.SPOTIFY)) {
//            if (!song.isPlaying()) {
////                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.FILL_PARENT);
////                params.gravity = Gravity.TOP;
////                ivAlbumCoverPlayer.setLayoutParams(params);
//                ivAlbumCoverPlayer.bringToFront();
//                Glide.with(getContext())
//                        .load(song.getAlbumCoverUrl())
//                        .into(ivAlbumCoverPlayer);
//                playSong(song);
//                //playNextSongInQueue(); //TODO to test
//                playSong(song);//TODO play song
//
//
//            } else {
//                Toast.makeText(getContext(), song.getTitle() + " already playing", Toast.LENGTH_LONG).show();
//            }
//        }
//        if (song.getService().equals(Song.LOCAL)){
//            Player.playSong(song);
//        }
//        else if (song.getService().equals(Song.YOUTUBE)){
//
//            youtube_fragment.bringToFront();
//
//            initializeYoutubePlayerFragment(song); ////TODO play song
//        }
//    }
//
//    @Override
//    public void onItemLongSelected(View view, int position) {
//        //add to queue
//        Song song = (Song) songs.get(position);
//        Player.queue.add(song);
//        Toast.makeText(getContext(), "added to queue", Toast.LENGTH_SHORT).show();
//    }
//
//
//
//    @Override
//    public void onPauseButtonClicked(View view, int position) {
//        Song song = (Song) songs.get(position);
//        com.ruppal.orbz.models.Player.pauseSong(song);
//    }
}
