package com.ruppal.orbz;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.fragments.SongListFragment;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
  * Created by jchavando on 7/19/17.
  */

public class PlaylistActivity extends AppCompatActivity implements ComplexRecyclerViewAdapter.SongAdapterListener{

   //recycler view for when you click on individual playlist
    RecyclerView rvSongs;
   SpotifyClient spotifyClient;
   ArrayList<Object> songs;
    public ComplexRecyclerViewAdapter complexAdapter;
    private ComplexRecyclerViewAdapter.SongAdapterListener songAdapterListener;
    private ComplexRecyclerViewAdapter.PlaylistAdapterListener playlistAdapterListener;
    private SongListFragment songListFragment;

   Playlist mPlaylist;
   Context context;
   @Override
   protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        spotifyClient = new SpotifyClient();
        // Lookup the recyclerview in activity layout
         rvSongs = (RecyclerView) findViewById(R.id.rvPlaylist);
         songs = new ArrayList<>();
        // Initialize contacts
         rvSongs.setLayoutManager(new LinearLayoutManager(this));
        //find RecyclerView

        //construct adapter from datasource
        complexAdapter = new ComplexRecyclerViewAdapter(songs, this, null );
        //recyclerView setup (layout manager, use adapter)
        //set the adapter
        rvSongs.setAdapter(complexAdapter);

       //unwrap playlist
       //Playlist unwrapper_playlist =  Parcels.unwrap();
       mPlaylist= (Playlist) Parcels.unwrap(getIntent().getParcelableExtra("tracks"));
       loadTracks(mPlaylist.getTracksUrl());


       RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
       rvSongs.addItemDecoration(itemDecoration);

 }


//when
public void addSong (Object song){
    songs.add((Song) song);
    complexAdapter.notifyItemInserted(songs.size()-1);
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


    @Override
    public void onItemSelected(View view, int position, boolean isPic) {

    }

    @Override
    public void onPauseButtonClicked(View view, int position) {

    }
}
