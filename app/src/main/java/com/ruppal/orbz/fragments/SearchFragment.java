package com.ruppal.orbz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.MapUtil;
import com.ruppal.orbz.clients.LastFMClient;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.clients.YouTubeClient;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;



/**
 * Created by jchavando on 7/13/17.
 */

public class SearchFragment extends SongListFragment {

    SpotifyClient spotifyClient;

    LastFMClient lastFMCLient;
    Context context;

    YouTubeClient youTubeClient;

    private final String TAG = "YoutubeClient";


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotifyClient = new SpotifyClient();
        youTubeClient = new YouTubeClient();
        lastFMCLient = new LastFMClient();
    }

    public void searchSongs(String query) {
        searchSpotify(query);
        //moved this after spotify json returns, so look in searchSpotify
        searchYoutube(query);
        ArrayList<Song> test = searchConverter(searchLocal(query));
        for (Song thisSong : test)
        {
            addSong(thisSong);
            Log.d("Elvis Search Song Test", thisSong.getTitle());
        }
    }

    public void searchYoutube (String query){
        youTubeClient.search(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray items = response.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++){
                        JSONObject item = items.getJSONObject(i);
                        Song song = Song.fromJSON(Song.YOUTUBE, item);
                        addSong(song);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e(TAG, throwable.toString());
                Log.e(TAG, responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, throwable.toString());
                Log.e(TAG, errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e(TAG, throwable.toString());
                Log.e(TAG, errorResponse.toString());
            }
        });

    }

    public Map<Song, Integer> searchLocal (String query){
        String[] queryList = query.split(" ");
        Map<Song, Integer> songMap = new LinkedHashMap<>();
        for (int i = 0; i <localSongList.size(); i++) {

            for (String temp : queryList) {

                if(containsIgnoreCase(localSongList.get(i).getTitle(), temp) || containsIgnoreCase(localSongList.get(i).getArtist(), temp)) {
                    Integer count = songMap.get(localSongList.get(i));
                    songMap.put(localSongList.get(i), (count == null) ? 1 : count + 1);
                }
            }
        }
        songMap = MapUtil.sortByValue(songMap);
        printMap(songMap);
        return songMap;
    }

    public ArrayList<Song> searchConverter(Map<Song, Integer> songMap){
        ArrayList<Song> songListNew = new ArrayList<>();
        for(Song key : songMap.keySet()){
            songListNew.add(key);
        }
        return songListNew;
    }

    public void searchSpotify(final String query) {
        spotifyClient.search(query, "track", new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                JSONObject tracks = null;
                try {
                    tracks = response.getJSONObject("tracks");
                    JSONArray items = tracks.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        Song song = Song.fromJSON(Song.SPOTIFY, item);
                        addSong(song);
                    }
//                    searchYoutube(query);
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

        public void searchFM(String query){
        lastFMCLient.search(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                JSONObject tracks = null;
                try {
                    tracks = response.getJSONObject("tracks");
                    JSONArray items = tracks.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++){
                        JSONObject item = items.getJSONObject(i);
                        Song song = Song.fromJSON(Song.LASTFM, item);
                        addSong(song);
                        //songAdapter.notifyItemInserted(songs.size()-1);
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

    public void printMap(Map<Song, Integer> map){
        for (Map.Entry<Song, Integer> entry : map.entrySet()) {
            Log.d("Elvis_Song_Map","Key : " + entry.getKey().getTitle() + " Value : " + entry.getValue());
        }
    }
}