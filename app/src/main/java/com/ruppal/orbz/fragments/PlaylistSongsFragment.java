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

public class PlaylistSongsFragment extends SongListFragment {


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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        super.onCreate(savedInstanceState);
        spotifyClient = new SpotifyClient();
        songs = new ArrayList<>();

    }
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        super.onCreateOptionsMenu(menu, inflater);
//        inflater.inflate(R.menu.menu_playlist, menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.action_search:
//                // Not implemented here
//                return false;
//            case R.id.addPlaylist:
//               // showPlaylistDialogFragment();
//                return true;
//            case R.id.backToPlaylists:
//                //transaction = getChildFragmentManager().beginTransaction(); //FragmentTransaction
//
//                getFragmentManager().popBackStack();
//                return true;
//            default:
//                break;
//        }
//
//        return false;
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

}