package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuItem;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

/**
 * Created by jchavando on 7/13/17.
 */

public class SearchFragment extends SongListFragment {

    SpotifyClient spotifyClient;

    private final String TAG = "SpotifyClient";
    String accessToken;
    String query;
    String type;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //client = TwitterApplication.getRestClient();
        populateTimeline();



    }


//    private void populateTimeline() {
//        query= getArguments().getString("q"); ;
//        type = getArguments().getString("type");
//        spotifyClient.search(accessToken, query, type, new JsonHttpResponseHandler() {
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                Log.d(TAG, response.toString());
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                addItems("spotify", response);
//
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                Log.d(TAG, responseString);
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                Log.d(TAG, errorResponse.toString());
//                throwable.printStackTrace();
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                Log.d(TAG, errorResponse.toString());
//                throwable.printStackTrace();
//            }
//        });
//
//    }


    private void populateTimeline(){
        MenuItem searchItem = menu.findItem(R.id.action_search);


        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                String hello = "hllo";
                spotifyClient.search(spotifyAccessToken, query, "track", new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        JSONObject tracks = null;
                        try {
                            tracks = response.getJSONObject("tracks");
                            JSONArray items = tracks.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++){
                                JSONObject item = items.getJSONObject(i);
                                Song song = Song.fromJSON(Song.SPOTIFY, item);
                                spotifyResults.add(song);
                            }
                            addToTextBox();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        super.onSuccess(statusCode, headers, response);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
                        super.onSuccess(statusCode, headers, responseString);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.e("search", throwable.toString());
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Log.e("search", errorResponse.toString());
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                        Log.e("search", errorResponse.toString());
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                });

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}





