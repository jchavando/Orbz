package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ruppal.orbz.R;
import com.ruppal.orbz.SongAdapter;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Song;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by jchavando on 7/13/17.
 */

public class SongListFragment extends Fragment implements SongAdapter.SongAdapterListener{

    public interface SongSelectedListener{
        public void onSongSelected(Song song);
    }


    private final int REQUEST_CODE = 20;
    public SongAdapter songAdapter;
    public ArrayList<Song> songs;
    public RecyclerView rvSongs;
    SpotifyClient spotifyClient;
    public Player mPlayer;


    //inflation happens inside onCreateView

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        spotifyClient = new SpotifyClient();
        getSpotifyPlayer();
        //inflate the layout
        View v = inflater.inflate(R.layout.fragments_songs_list, container, false);

        //find RecyclerView
        rvSongs = (RecyclerView) v.findViewById(R.id.rvSong);
        //init the arraylist (data source)
        songs = new ArrayList<>();
        //construct adapter from datasource
        songAdapter = new SongAdapter(songs, this); //this
        //recyclerView setup (layout manager, use adapter)
        rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvSongs.setAdapter(songAdapter);


        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvSongs.addItemDecoration(itemDecoration);

        return v;
    }

    @Override
    public void onItemSelected(View view, int position, boolean isPic) {
        Song song = songs.get(position);
        Toast.makeText(getContext(), "hello", Toast.LENGTH_LONG).show();
        if (song.getService() == Song.SPOTIFY){
            playSongFromSpotify(song);
        }
//        ((SongSelectedListener) getActivity()).onSongSelected(song);

    }

    public void playSongFromSpotify(Song song){
        mPlayer.playUri(null, "spotify:track:" + song.getUid() , 0, 0);
//        spotifyClient.startAndResume(song.getUid(), null, null, new JsonHttpResponseHandler(){
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
//                super.onSuccess(statusCode, headers, response);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
//                super.onSuccess(statusCode, headers, response);
//            }
//
//            @Override
//            public void onSuccess(int statusCode, Header[] headers, String responseString) {
//                super.onSuccess(statusCode, headers, responseString);
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
//                super.onFailure(statusCode, headers, responseString, throwable);
//                Log.e("playSong", throwable.toString());
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Log.e("playSong", throwable.toString());
//            }
//
//            @Override
//            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
//                super.onFailure(statusCode, headers, throwable, errorResponse);
//                Log.e("playSong", throwable.toString());
//            }
//        });
    }

    public void getSpotifyPlayer(){
        Config playerConfig = new Config(getContext(), SpotifyClient.accessToken, getString(R.string.spotify_client_id));
        mPlayer = Spotify.getPlayer(playerConfig, this, null);
    }


    public void addItems (String service, JSONArray response){
        for (int i = 0; i < response.length(); i++){
            //convert each object to a Song model
            //add that Song model to our data source
            //notify the adapter that we've added an item (list view)
            Song song = null;
            try {
                song = Song.fromJSON(service, response.getJSONObject(i));
                songs.add(song);
                songAdapter.notifyItemInserted(songs.size()-1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    //adds one tweet at top
//    public void postTweet(Song song){
//        tweets.add(0, tweet);
//        tweetAdapter.notifyItemInserted(0);
//        rvTweets.scrollToPosition(0);
//    }

//    @Override
//    public void onItemSelected(View view, int position, boolean isPic) {
//        Song song = songs.get(position);
//        if(!isPic) {
//            //((SongSelectedListener) getActivity()).onTweetSelected(tweet);
//        } else {
//            //((TweetSelectedListener) getActivity()).onImageSelected(tweet);
//        }
//
//    }





}
