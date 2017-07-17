package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.clients.LastFMClient;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jchavando on 7/13/17.
 */

public class SearchFragment extends SongListFragment {

    SpotifyClient spotifyClient;
    LastFMClient lastFMCLient;

    //private final String TAG = "SpotifyClient";



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //client = TwitterApplication.getRestClient();
    }

    public void clearSongsList(){
        songs.clear();
        songAdapter.notifyDataSetChanged();
    }

    public void searchSongs(String query) {
        spotifyClient = new SpotifyClient();

        spotifyClient.search(query, "track", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                JSONObject tracks = null;
                try {
                    tracks = response.getJSONObject("tracks");
                    JSONArray items = tracks.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++){
                        JSONObject item = items.getJSONObject(i);
                        Song song = Song.fromJSON(Song.SPOTIFY, item);
                        songs.add(song);
                        songAdapter.notifyItemInserted(songs.size()-1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

        lastFMCLient = new LastFMClient();
        lastFMClient.search("track", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                JSONObject tracks = null;
                try {
                    tracks = response.getJSONObject("tracks");
                    JSONArray items = tracks.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++){
                        JSONObject item = items.getJSONObject(i);
                        Song song = Song.fromJSON(Song.LASTFM, item);
                        songs.add(song);
                        songAdapter.notifyItemInserted(songs.size()-1);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONArray response) {
                super.onSuccess(statusCode, headers, response);
            }

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString) {
                super.onSuccess(statusCode, headers, responseString);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }

        });

    }
}





