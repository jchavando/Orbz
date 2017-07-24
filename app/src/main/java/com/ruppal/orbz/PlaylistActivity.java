package com.ruppal.orbz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.fragments.SongListFragment;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

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
    public Player mPlayer;
    Playlist mPlaylist;
    SongListFragment songListFragment;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        getSpotifyPlayer();
        spotifyClient = new SpotifyClient();
        songListFragment = new SongListFragment();
        // Lookup the recyclerview in activity layout
         rvSongs = (RecyclerView) findViewById(R.id.rvPlaylist);
         songs = new ArrayList<>();
        // Initialize contacts
         rvSongs.setLayoutManager(new LinearLayoutManager(this));
        //find RecyclerView

        //construct adapter from datasource
        complexAdapter = new ComplexRecyclerViewAdapter(songs, this, null, null);
        //recyclerView setup (layout manager, use adapter)
        //set the adapter
        rvSongs.setAdapter(complexAdapter);

       //unwrap playlist
       mPlaylist = (Playlist) Parcels.unwrap(getIntent().getParcelableExtra("tracks"));
       if (mPlaylist.getPlaylistService().equals(Song.SPOTIFY)){
           loadTracksFromSpotify(mPlaylist.getTracksUrl());
       }
       else if (mPlaylist.getPlaylistService().equals(Song.LOCAL)){
           loadTracksFromLocal(mPlaylist);
       }


       RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
       rvSongs.addItemDecoration(itemDecoration);

 }


//when
    public void addSong (Object song){
        songs.add((Song) song);
        complexAdapter.notifyItemInserted(songs.size()-1);
    }

    public void loadTracksFromLocal(Playlist playlist){
        ArrayList<Song> tracks = playlist.getTracks();
        for (int i=0; i<tracks.size(); i++){
            addSong(tracks.get(i));
        }
    }

    public void loadTracksFromSpotify(String tracksUrl){
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
    public void onItemSelected(View view, int position) {
        Song song = (Song) songs.get(position);
        if (song.getService() == Song.SPOTIFY) {
            if (!song.isPlaying()) {
                playSongFromSpotify(song);
            } else {
                Toast.makeText(this, song.getTitle() + " already playing", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void getSpotifyPlayer(){
        Config playerConfig = new Config(this, SpotifyClient.accessToken, getString(R.string.spotify_client_id));
        mPlayer = Spotify.getPlayer(playerConfig, this, null);
    }
    public void playSongFromSpotify(Song song){
        String playingNow = "playing " + song.getTitle() + " from spotify";
        Toast.makeText(this, playingNow, Toast.LENGTH_LONG).show();
        mPlayer.playUri(null, "spotify:track:" + song.getUid() , 0, 0);
        song.playing = true;
    }



    @Override
    public void onPauseButtonClicked(View view, int position) {
        final Song song = (Song) songs.get(position);
        Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                String nowPaused= "paused " + song.getTitle();
                //Toast.makeText(getContext(), nowPaused, Toast.LENGTH_LONG).show();
                song.playing = false;
            }

            @Override
            public void onError(Error error) {
                Log.e("playlist activity pause", error.toString());
            }


        };

//        PlaybackState mCurrentPlaybackState = mPlayer.getPlaybackState();
//        if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
//            mPlayer.pause(mOperationCallback);
//        } else {
//            Drawable playButton = getContext().getResources().getDrawable(R.drawable.exo_controls_play);
//            ((ImageView) view).setImageDrawable(playButton);
//            mPlayer.resume(mOperationCallback);
//        }
    }

    @Override
    public void onItemLongSelected(View view, int position) {

    }

    @Override
    public void onAddPlaylistSongClicked(View view, int position) {

    }


}
