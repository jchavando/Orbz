package com.ruppal.orbz.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.ruppal.orbz.MainActivity;
import com.ruppal.orbz.R;
import com.ruppal.orbz.SongAdapter;
import com.ruppal.orbz.clients.LastFMClient;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Song;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

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
    LastFMClient lastFMClient;
    public Player mPlayer;
    public MainActivity mainActivity;


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
        songAdapter = new SongAdapter(songs, this, mainActivity); //this
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
        if (song.getService() == Song.SPOTIFY) {
            if(!song.isPlaying()) {
                playSongFromSpotify(song);
            }
            else {
                Toast.makeText(getContext(), song.getTitle() + " already playing", Toast.LENGTH_LONG).show();
//                pauseSongFromSpotify(song);
            }
        }
//        ((SongSelectedListener) getActivity()).onSongSelected(song);

    }

    @Override
    public void onPauseButtonClicked(View view, int position) {
        final Song song = songs.get(position);
        Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
            @Override
            public void onSuccess() {
                String nowPaused= "paused " + song.getTitle();
                Toast.makeText(getContext(), nowPaused, Toast.LENGTH_LONG).show();
                song.playing = false;
            }

            @Override
            public void onError(Error error) {
                Log.e("play", error.toString());
            }
        };

        PlaybackState mCurrentPlaybackState = mPlayer.getPlaybackState();
        if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
            mPlayer.pause(mOperationCallback);
        } else {
            Drawable playButton = getContext().getResources().getDrawable(R.drawable.exo_controls_play);
            ((ImageView) view).setImageDrawable(playButton);
            mPlayer.resume(mOperationCallback);
        }
    }



    public void playSongFromSpotify(Song song){
        String playingNow = "playing " + song.getTitle();
        Toast.makeText(getContext(), playingNow, Toast.LENGTH_LONG).show();
        mPlayer.playUri(null, "spotify:track:" + song.getUid() , 0, 0);
        song.playing = true;
    }



    public void getSpotifyPlayer(){
        Config playerConfig = new Config(getContext(), SpotifyClient.accessToken, getString(R.string.spotify_client_id));
        mPlayer = Spotify.getPlayer(playerConfig, this, null);
    }

//
//    public void addItems (String service, JSONArray response){
//        for (int i = 0; i < response.length(); i++){
//            //convert each object to a Song model
//            //add that Song model to our data source
//            //notify the adapter that we've added an item (list view)
//            Song song = null;
//            try {
//                song = Song.fromJSON(service, response.getJSONObject(i));
//                songs.add(song);
//                songAdapter.notifyItemInserted(songs.size()-1);
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//    }

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
