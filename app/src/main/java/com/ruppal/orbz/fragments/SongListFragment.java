package com.ruppal.orbz.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Song;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by jchavando on 7/13/17.
 */

public class SongListFragment extends Fragment implements ComplexRecyclerViewAdapter.SongAdapterListener, YouTubePlayer.Provider {

    @Override
    public void initialize(String s, YouTubePlayer.OnInitializedListener onInitializedListener) {
    }

    public interface SongSelectedListener{
        public void onSongSelected(Song song);
    }


    private final int REQUEST_CODE = 20;
    public ComplexRecyclerViewAdapter complexAdapter;
    public ArrayList<Object> songs;
    public RecyclerView rvSongs;
    SpotifyClient spotifyClient;
    public Player mPlayer;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    public YouTubePlayer yPlayer;
    String SONG_TO_PLAY = "SONG_TO_PLAY";
    FrameLayout frameLayout;


    //inflation happens inside onCreateView

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        spotifyClient = new SpotifyClient();
        getSpotifyPlayer();
        //inflate the layout
        View v = inflater.inflate(R.layout.fragments_songs_list, container, false);
        frameLayout = (FrameLayout) v.findViewById(R.id.youtube_fragment);
//        youTubePlayerFragment = (YouTubePlayerSupportFragment) getChildFragmentManager().findFragmentById(R.id.youtube_fragment);
//        youTubePlayerFragment = (YouTubePlayerSupportFragment) getFragmentManager().findFragmentById(R.id.youtube_fragment);
//        youTubePlayerFragment = (YouTubePlayerSupportFragment) getFragmentManager().findFragmentById(R.id.youtube_video_placeholder_fragment);
        //find RecyclerView
        rvSongs = (RecyclerView) v.findViewById(R.id.rvSong);
        //init the arraylist (data source)
        songs = new ArrayList<>();
        //construct adapter from datasource
        complexAdapter = new ComplexRecyclerViewAdapter(songs, this); //this
        //recyclerView setup (layout manager, use adapter)
        rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvSongs.setAdapter(complexAdapter);


        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvSongs.addItemDecoration(itemDecoration);

        return v;
    }




    public void initializeYoutubePlayerFragment(final Song song){
        youTubePlayerFragment = new YouTubePlayerSupportFragment();
        FragmentManager fragmentManager = getFragmentManager();
//        Bundle args = new Bundle();
//        args.putParcelable(SONG_TO_PLAY, Parcels.wrap(song));
//        youTubePlayerFragment.setArguments(args);
//        frameLayout.setMinimumWidth(200);
//        frameLayout.setMinimumHeight(110);
        frameLayout.setVisibility(View.VISIBLE);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.youtube_fragment, youTubePlayerFragment);
        fragmentTransaction.addToBackStack(SONG_TO_PLAY);
        fragmentTransaction.commit();
        youTubePlayerFragment.initialize(getString(R.string.googlePlay_client_id), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                yPlayer = youTubePlayer;
                playSongFromYoutube(song);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getContext(), "Failed to initalize the youtube player", Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    public void onItemSelected(View view, int position, boolean isPic) {
        Song song = (Song) songs.get(position);
        if (song.getService() == Song.SPOTIFY) {
            if (!song.isPlaying()) {
                playSongFromSpotify(song);
            } else {
                Toast.makeText(getContext(), song.getTitle() + " already playing", Toast.LENGTH_LONG).show();
//                pauseSongFromSpotify(song);
            }
        }
        else if (song.getService() == Song.YOUTUBE){
            Toast.makeText(getContext(), song.getTitle() + " is from youtube", Toast.LENGTH_LONG).show();
            initializeYoutubePlayerFragment(song);
        }

//        ((SongSelectedListener) getActivity()).onSongSelected(song);

    }




    @Override
    public void onPauseButtonClicked(View view, int position) {
        final Song song = (Song) songs.get(position);
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
        String playingNow = "playing " + song.getTitle() + " from spotify";
        Toast.makeText(getContext(), playingNow, Toast.LENGTH_LONG).show();
        mPlayer.playUri(null, "spotify:track:" + song.getUid() , 0, 0);
        song.playing = true;
    }

    public void playSongFromYoutube(Song song){
        if (yPlayer != null){
            yPlayer.loadVideo(song.getUid());
            yPlayer.play();
            String playingNow = "playing " + song.getTitle() + " from youtube";
            Toast.makeText(getContext(), playingNow, Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getContext(), "Something went wrong with the youtube player. Unable to play your video.", Toast.LENGTH_LONG).show();
        }
    }



    public void getSpotifyPlayer(){
        Config playerConfig = new Config(getContext(), SpotifyClient.accessToken, getString(R.string.spotify_client_id));
        mPlayer = Spotify.getPlayer(playerConfig, this, null);
    }

    public void addSong (Object song){
        songs.add(song);
        complexAdapter.notifyItemInserted(songs.size()-1);
    }

    public void clearSongsList(){
        songs.clear();
        complexAdapter.notifyDataSetChanged();
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
                complexAdapter.notifyItemInserted(songs.size()-1);
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
