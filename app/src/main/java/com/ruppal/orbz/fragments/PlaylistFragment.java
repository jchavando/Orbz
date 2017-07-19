package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jchavando on 7/13/17.
 */


public class PlaylistFragment extends SongListFragment {

    SpotifyClient spotifyClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotifyClient = new SpotifyClient();
        populatePlaylists();

    }

    public void populatePlaylists(){
        //make sure to clear out songs so playlists show instead
        spotifyClient.getMyPlaylists(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray items = response.getJSONArray("items");
                    JSONObject firstPlaylist = items.getJSONObject(0);
                    JSONObject tracks = firstPlaylist.getJSONObject("tracks");
                    String tracksUrl = tracks.getString("href");
                    loadTracks(tracksUrl);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("playlists", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("playlists", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("playlists", errorResponse.toString());
            }
        });
    }

    public void loadTracks(String tracksUrl){
        //clear the song list
        songs.clear();
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
}

