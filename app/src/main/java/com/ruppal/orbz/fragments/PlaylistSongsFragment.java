package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
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

public class PlaylistSongsFragment extends SongListFragment implements com.ruppal.orbz.models.Player.highlightCurrentSongListenerPlaylist, ComplexRecyclerViewAdapter.SongAdapterListenerPlaylist {


    //recycler view for when you click on individual playlist
    RecyclerView rvSongs;
    SpotifyClient spotifyClient;
//    ArrayList<Object> songs;
//    public ComplexRecyclerViewAdapter complexAdapter;
    public Player mPlayer;
    Playlist mPlaylist;
    SongListFragment songListFragment;
    ViewPager vpPager;
    PlaylistFragment playlistFragment;


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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        spotifyClient = new SpotifyClient();
//        songs = new ArrayList<>();
        setHasOptionsMenu(true);
        playlistFragment = new PlaylistFragment();
        com.ruppal.orbz.models.Player.setmHighlightCurrentSongListenerPlaylist(this);


    }


    public void playlistSongsBack() {
        Toast.makeText(getContext(), "back to playlists", Toast.LENGTH_SHORT).show();
        getFragmentManager().popBackStack();
    }




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
//                Log.e("playlists", throwable.toString());
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Log.e("playlists", errorResponse.toString());
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


    @Override
    public void onSongPlayingChanged() {
        complexAdapter.notifyDataSetChanged();
    }

    //todo refactor this code with design stuff
    @Override
    public void onItemSelected(View view, int position) {
        //play the song
        super.onItemSelected(view, position);
        if (songs!=null && com.ruppal.orbz.models.Player.automaticQueue != null && position>=0 && position<songs.size()) {
            Song songSelected = (Song) songs.get(position);
            //clear old queue
            com.ruppal.orbz.models.Player.clearQueue();
            com.ruppal.orbz.models.Player.clearAutomaticQueue();
            //set songs for the new queue
            automaticallyPopulateQueue(songSelected);
        }
    }

    public void automaticallyPopulateQueue(Song songSelected){
        //required : position is position of song clicked, so want to start at next
        boolean beforeSelected = true;
        ArrayList<Song> songsBeforeSelected = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++){
            Song song = (Song) songs.get(i);
            if (song != songSelected && beforeSelected){
                songsBeforeSelected.add(song);
            }
            else{
                if (song == songSelected) {
                    beforeSelected = false;
                }
                else {
                    com.ruppal.orbz.models.Player.automaticQueue.add(song);
                }
            }
        }
        com.ruppal.orbz.models.Player.automaticQueue.addAll(songsBeforeSelected);
        com.ruppal.orbz.models.Player.automaticQueue.add(songSelected);
    }
}
