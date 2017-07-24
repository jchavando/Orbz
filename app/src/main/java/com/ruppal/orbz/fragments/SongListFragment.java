package com.ruppal.orbz.fragments;

import android.content.Intent;
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
import com.ruppal.orbz.MainActivity;
import com.ruppal.orbz.PlaylistActivity;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.database.DatabaseHelper;
import com.ruppal.orbz.models.Player;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by jchavando on 7/13/17.
 */

public class SongListFragment extends Fragment implements ComplexRecyclerViewAdapter.SongAdapterListener, ComplexRecyclerViewAdapter.PlaylistAdapterListener,  YouTubePlayer.Provider {

    @Override
    public void initialize(String s, YouTubePlayer.OnInitializedListener onInitializedListener) {
    }

    @Override
    public void onPlaylistItemSelected(View view, int position) {
        //Intent intent = new Intent(getContext(), PlaylistActivity.class);
        //intent.putExtra("tracks", playlist.getTracksUrl());
        // Navigate to contact details activity on click of card view.

        final Playlist playlist = (Playlist) songs.get(position);

        if (playlist != null) {
            // Fire an intent when a playlist is selected
            // Pass contact object in the bundle and populate details activity.
            Intent intent = new Intent(getContext(), PlaylistActivity.class);
            intent.putExtra("tracks", Parcels.wrap(playlist));
            getContext().startActivity(intent);
        }

        //startActivity(intent);

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
    private ComplexRecyclerViewAdapter.PlaylistAdapterListener playlistAdapterListener;


    public static ArrayList<Song> localSongList;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songs = new ArrayList<>();
        complexAdapter = new ComplexRecyclerViewAdapter(songs, this, this); //this
        localSongList = ((MainActivity)getActivity()).getLocalSongs();

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        spotifyClient = new SpotifyClient();
        //inflate the layout
        View v = inflater.inflate(R.layout.fragments_songs_list, container, false);
        frameLayout = (FrameLayout) getActivity().findViewById(R.id.youtube_fragment);

        //find RecyclerView
        rvSongs = (RecyclerView) v.findViewById(R.id.rvSong);
        //init the arraylist (data source)
//        songs = new ArrayList<>();
        //construct adapter from datasource
//        complexAdapter = new ComplexRecyclerViewAdapter(songs, this, this); //this
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
        if (song.getService().equals(Song.SPOTIFY)) {
            if (!song.isPlaying()) {
                com.ruppal.orbz.models.Player.playSong(song);
            } else {
                Toast.makeText(getContext(), song.getTitle() + " already playing", Toast.LENGTH_LONG).show();
            }
        }
        if (song.getService().equals(Song.LOCAL)){
            Player.prepareExoPlayerFromFileUri(song.getSongUri());
        }
        else if (song.getService().equals(Song.YOUTUBE)){
            initializeYoutubePlayerFragment(song);
        }
    }

    

    @Override
    public void onPauseButtonClicked(View view, int position) {
        Song song = (Song) songs.get(position);
        com.ruppal.orbz.models.Player.pauseSong(song, getContext(), view);

//        final Song song = (Song) songs.get(position);
//        Player.OperationCallback mOperationCallback = new Player.OperationCallback() {
//            @Override
//            public void onSuccess() {
//                String nowPaused= "paused " + song.getTitle();
//                //Toast.makeText(getContext(), nowPaused, Toast.LENGTH_LONG).show();
//                song.playing = false;
//            }
//
//            @Override
//            public void onError(Error error) {
//                Log.e("playlist activity pause", error.toString());
//
//            }
//
//
//        };

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
//        complexAdapter.notify();
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

    public static boolean containsIgnoreCase(final String str, final String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }
}
