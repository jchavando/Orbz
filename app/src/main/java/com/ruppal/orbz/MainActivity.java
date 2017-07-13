package com.ruppal.orbz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.clients.GooglePlayClient;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.clients.YouTubeClient;
import com.ruppal.orbz.models.Song;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MainActivity extends AppCompatActivity {
    public static final String SPOTIFY_PLAYER = "SPOTIFY_PLAYER";
    public static final String SPOTIFY_ACCESS_TOKEN = "SPOTIFY_ACCESS_TOKEN";
    public static final String GOOGLE_ACCESS_TOKEN = "GOOGLE_ACCESS_TOKEN";
    String spotifyClientId;
    Player mPlayer;
    Intent intent;
    SpotifyClient spotifyClient;
    ArrayList<Song> spotifyResults;
    String spotifyAccessToken;
    String googleAccessToken;
    ArrayList<Song> googleResults;
    GooglePlayClient googlePlayClient;
    YouTubeClient youtubeClient;
    ArrayList<Song> youtubeResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spotifyResults = new ArrayList<>();
        googlePlayClient = new GooglePlayClient();
        youtubeClient = new YouTubeClient();
        spotifyClientId = getString(R.string.spotify_client_id);
        intent = getIntent();
        spotifyAccessToken = intent.getStringExtra(SPOTIFY_ACCESS_TOKEN);
        googleAccessToken = intent.getStringExtra(GOOGLE_ACCESS_TOKEN);
        spotifyClient = new SpotifyClient(spotifyAccessToken);
        getSpotifyPlayer(spotifyAccessToken);
        googleResults = new ArrayList<>();
        youtubeResults = new ArrayList<>();

//        mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
//        intent=getIntent();
//        mPlayer = Parcels.unwrap(intent.getParcelableExtra(SPOTIFY_PLAYER));
//        mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here
                spotifyClient.search(query, "track", new JsonHttpResponseHandler() {
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

                youtubeClient.search(query, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                            JSONArray items = response.getJSONArray("items");
                            for (int i =0 ; i<items.length(); i++){
                                JSONObject item = items.getJSONObject(i);
                                Song song = Song.fromJSON(Song.YOUTUBE, item);
                                youtubeResults.add(song);
                            }
                            addToTextBox();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
//                googlePlayClient.loginToGoogleMusic(googleAccessToken, new JsonHttpResponseHandler(){
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        super.onSuccess(statusCode, headers, response);
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                        super.onSuccess(statusCode, headers, response);
//                    }
//
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                        super.onSuccess(statusCode, headers, responseString);
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        super.onFailure(statusCode, headers, responseString, throwable);
//                        Log.e("search", throwable.toString());
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        super.onFailure(statusCode, headers, throwable, errorResponse);
//                        Log.e("search", throwable.toString());
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                        super.onFailure(statusCode, headers, throwable, errorResponse);
//                        Log.e("search", throwable.toString());
//                    }
//                });
//
//                googlePlayClient.search(googleAccessToken, query, new JsonHttpResponseHandler(){
//                    @Override
//                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                        JSONArray songs = null;
//                        try {
//                            songs = response.getJSONArray("songs");
//                            for (int i =0; i<songs.length(); i++){
//                                Song song = Song.fromJSON(Song.GOOGLE_PLAY , songs.getJSONObject(i));
//                                googleResults.add(song);
//                            }
//                            addToTextBox();
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                        super.onFailure(statusCode, headers, responseString, throwable);
//                        Log.e("search", throwable.toString());
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                        super.onFailure(statusCode, headers, throwable, errorResponse);
//                        Log.e("search", throwable.toString());
//                    }
//
//                    @Override
//                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                        super.onFailure(statusCode, headers, throwable, errorResponse);
//                        Log.e("search", throwable.toString());
//                    }
//                });

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


    public void getSpotifyPlayer(String accessToken){
        Config playerConfig = new Config(this, accessToken, spotifyClientId);
        mPlayer = Spotify.getPlayer(playerConfig, this, null);
    }
    public void addToTextBox(){
        TextView tvSongs = (TextView) findViewById(R.id.tvSongs);
        String songs = "";
        for (int i =0 ;i < spotifyResults.size(); i++){
            songs += (spotifyResults.get(i).title + ", ");
        }
        songs+="GOOGLE PLAY \n\n";
        for (int i =0 ;i < googleResults.size(); i++){
            songs += (googleResults.get(i).title + ", ");
        }
        tvSongs.setText(songs);
    }



}
