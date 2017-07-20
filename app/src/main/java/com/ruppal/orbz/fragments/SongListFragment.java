package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.database.DatabaseHelper;
import com.ruppal.orbz.models.Song;

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
    YouTubePlayerSupportFragment youTubePlayerFragment;
    String SONG_TO_PLAY = "SONG_TO_PLAY";
    FrameLayout frameLayout;
    FragmentTransaction fragmentTransaction;


    //inflation happens inside onCreateView

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        spotifyClient = new SpotifyClient();
        //inflate the layout
        View v = inflater.inflate(R.layout.fragments_songs_list, container, false);
        frameLayout = (FrameLayout) v.findViewById(R.id.youtube_fragment);
//      //find RecyclerView
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
        frameLayout.setVisibility(View.VISIBLE);
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.youtube_fragment, youTubePlayerFragment);
        fragmentTransaction.addToBackStack(SONG_TO_PLAY);
        fragmentTransaction.commit();
        youTubePlayerFragment.initialize(getString(R.string.googlePlay_client_id), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                com.ruppal.orbz.models.Player.setYouTubePlayer(youTubePlayer);
                com.ruppal.orbz.models.Player.playSong(song);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getContext(), "Failed to initalize the youtube player", Toast.LENGTH_LONG).show();
            }
        });

    }



    @Override
    public void onItemSelected(View view, int position) {
        Song song = (Song) songs.get(position);
        if (song.getService() == Song.SPOTIFY) {
            if (!song.isPlaying()) {
                com.ruppal.orbz.models.Player.playSong(song);
            } else {
                Toast.makeText(getContext(), song.getTitle() + " already playing", Toast.LENGTH_LONG).show();
            }
        }
        else if (song.getService() == Song.YOUTUBE){
            initializeYoutubePlayerFragment(song);
        }
    }

    

    @Override
    public void onPauseButtonClicked(View view, int position) {
        Song song = (Song) songs.get(position);
        com.ruppal.orbz.models.Player.pauseSong(song, getContext(), view);

    }

    @Override
    public void onItemLongSelected(View view, int position) {
        Object song = songs.get(position);
        if (song instanceof Song){
            DatabaseHelper.addSongToTestPlaylist((Song) song);
            Toast.makeText(getContext(), ((Song) song).getTitle() + " added to a local playlist", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(getContext(), "can only add a song to a playlist", Toast.LENGTH_SHORT).show();
        }
    }


    public void addSong (Object song){
        songs.add(song);
        complexAdapter.notifyItemInserted(songs.size()-1);
    }

    public void clearSongsList(){
        songs.clear();
        complexAdapter.notifyDataSetChanged();
    }

}
