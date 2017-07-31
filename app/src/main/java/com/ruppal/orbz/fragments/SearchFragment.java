package com.ruppal.orbz.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.MainActivity;
import com.ruppal.orbz.MapUtil;
import com.ruppal.orbz.R;
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
    ArrayList<Song> youtubeSongs = new ArrayList<>();
    ArrayList<Song> spotifySongs = new ArrayList<>();
    ArrayList<Song> localSongs = new ArrayList<>();
    private boolean youtubeReady = false;
    private boolean spotifyReady = false;
    private String theQuery;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotifyClient = new SpotifyClient();
        youTubeClient = new YouTubeClient();
        lastFMCLient = new LastFMClient();
        setHasOptionsMenu(true);
        //(getActivity()).setTitle("search");

       ((MainActivity) getActivity()).setActionBarTitle("search");

    }
    public void onResume(){
        super.onResume();

        // Set title bar
        ((MainActivity) getActivity()).getSupportActionBar().setTitle("search");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem refineItem = menu.findItem(R.id.refine_search);
        refineItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if (songs != null && theQuery != null) {
                    refineSongSearch(theQuery);
                    return true;
                }
                return false;
            }
        });
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                theQuery = query;
                SearchFragment searchFragment= (SearchFragment) SongPagerAdapter.mFragmentReferences.get(0);
                searchFragment.clearSongsList();
                searchFragment.searchSongs(query);

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        //return super.onCreateOptionsMenu(menu);
    }

    public void clearLocalLists(){
        if (localSongs!=null){
            localSongs.clear();
        }
        if (youtubeSongs!= null){
            youtubeSongs.clear();
        }
        if (spotifySongs != null){
            spotifySongs.clear();
        }
    }

    public void searchSongs(String query) {
        clearLocalLists();
        youtubeReady = false;
        spotifyReady = false;
        ArrayList<Song> test = searchConverter(searchLocal(query));
        for (Song thisSong : test)
        {
            localSongs.add(thisSong);
//            addSong(thisSong);
//            Log.d("Elvis Search Song Test", thisSong.getTitle());
        }
        searchSpotify(query);
        //moved this after spotify json returns, so look in searchSpotify
        searchYoutube(query);
    }

    public void mixUpSongs(){
        int spotifyLength = spotifySongs.size();
        int youtubeLength = youtubeSongs.size();
        int localLength = localSongs.size();
        int length = Math.max(Math.max(spotifyLength,youtubeLength) , localLength);

        for (int i=0;i<length; i++){
            //order is spotify, local, youtube
            if (i<spotifyLength){
                addSong(spotifySongs.get(i));
            }

            if(i<localLength){
                addSong(localSongs.get(i));
            }

            if (i<youtubeLength){
                addSong(youtubeSongs.get(i));
            }

        }
    }

    public void refineSongSearch(String query){
        ArrayList<Song> refinedSongList = new ArrayList<>();
        for(Object temp : songs){
            if(temp instanceof Song){
                refinedSongList.add((Song) temp);
            }
        }
        ArrayList<Song> currentList = searchConverter(searchAll(query, refinedSongList));
        clearSongsList();
        for(Song searchedSong : currentList)
        {
            addSong(searchedSong);
        }
    }

    public Map<Song, Integer> searchAll (String query, ArrayList<Song> workingArray){
        String[] queryList = query.split(" ");
        Map<Song, Integer> songMap = new LinkedHashMap<>();
        for (int i = 0; i <workingArray.size(); i++) {

            for (String temp : queryList) {

                if(containsIgnoreCase(workingArray.get(i).getArtist(), temp) || containsIgnoreCase(workingArray.get(i).getTitle(), temp)) {
                    Integer count = songMap.get(workingArray.get(i));
                    songMap.put(workingArray.get(i), (count == null) ? 1 : count + 1);
                }
            }
        }
        songMap = MapUtil.sortByValue(songMap);
        printMap(songMap);
        return songMap;
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

    public void searchYoutube (String query) {
        youTubeClient.search(query, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    JSONArray items = response.getJSONArray("items");
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);
                        Song song = Song.fromJSON(Song.YOUTUBE, item);
                        youtubeSongs.add(song);
//                        addSong(song);
                    }
                    youtubeReady = true;
                    if (spotifyReady){
                        mixUpSongs();
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
                        spotifySongs.add(song);
//                        addSong(song);
                    }
//                    searchYoutube(query);
                    spotifyReady = true;
                    if (youtubeReady){
                        mixUpSongs();
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

    public ArrayList<Song> searchConverter(Map<Song, Integer> songMap){
        ArrayList<Song> songListNew = new ArrayList<>();
        for(Song key : songMap.keySet()){
            songListNew.add(key);
        }
        return songListNew;
    }
}