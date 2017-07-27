package com.ruppal.orbz.fragments;

import android.app.Activity;
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
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.MainActivity;
import com.ruppal.orbz.PlaylistActivity;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.database.PlaylistTable;
import com.ruppal.orbz.models.Player;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

import static com.ruppal.orbz.models.Player.playSong;

/**
 * Created by jchavando on 7/13/17.
 */

public class SongListFragment extends Fragment implements ComplexRecyclerViewAdapter.SongAdapterListener, ComplexRecyclerViewAdapter.PlaylistAdapterListener,  YouTubePlayer.Provider {

    private ComplexRecyclerViewAdapter.PlaylistAdapterListener playlistAdapterListener;
    private final int REQUEST_CODE = 20;
    public ComplexRecyclerViewAdapter complexAdapter;
    public ArrayList<Object> songs;
    public RecyclerView rvSongs;
    public static ArrayList<Song> localSongList;
    public static ArrayList<PlaylistTable> localPlaylistTables;
    static ComplexRecyclerViewAdapter.AddSongToPlaylistAdapterListener addSongToPlaylistAdapterListener;

    SpotifyClient spotifyClient;
    YouTubePlayerSupportFragment youTubePlayerFragment;
    String SONG_TO_PLAY = "SONG_TO_PLAY";
    FrameLayout frameLayout;
    FragmentTransaction fragmentTransaction;
    ImageView ivAlbumCoverPlayer;
    FrameLayout youtube_fragment;



    /*
    if(queue.size>=1{
        for ( Song song: queue) {
            if (a song is not playing){
                playSong(song);
                while (song is playing){
                 ®
                }
            }
     */

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
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songs = new ArrayList<>();
        localSongList = ((MainActivity)getActivity()).getLocalSongs();
        complexAdapter = new ComplexRecyclerViewAdapter(songs, this, this, null); //this
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        spotifyClient = new SpotifyClient();
        //inflate the layout
        Activity activity = getActivity();
        View v = inflater.inflate(R.layout.fragments_songs_list, container, false);
        frameLayout = (FrameLayout) activity.findViewById(R.id.youtube_fragment);
        ivAlbumCoverPlayer = (ImageView) activity.findViewById(R.id.ivAlbumCoverPlayer);
        youtube_fragment = (FrameLayout) activity.findViewById(R.id.youtube_fragment);
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

        rvSongs.setBackgroundResource(R.drawable.watermark4);
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
                playSong(song);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(getContext(), "Failed to initalize the youtube player", Toast.LENGTH_LONG).show();
            }
        });

    }

//
//    public void playNextSongInQueue() {
//        while (Player.queue.size() >= 1) { //got through queue
//            Log.d("song list fragment", String.valueOf(Player.queue.size()));
//            for (int i = 0; i < Player.queue.size(); i++) {
//
//               // if ( ) { //no song currently playing
//                    playSong(Player.queue.get(i));
//                    Log.d("what is playing in queue", String.valueOf(Player.queue.get(i)));
//                    Toast.makeText(getContext(), "playing song in queue", Toast.LENGTH_SHORT).show();
//                    Player.queue.remove(i);
//               // }
//            }
//        }
//    }


    @Override
    public void onItemSelected(View view, int position) {
        Song song = (Song) songs.get(position);
        switch(song.getService()){
            case Song.SPOTIFY :
                if(song.isPlaying())
                Toast.makeText(getContext(), song.getTitle() + " already playing", Toast.LENGTH_LONG).show();
                ivAlbumCoverPlayer.bringToFront();
                Glide.with(getContext())
                        .load(song.getAlbumCoverUrl())
                        .into(ivAlbumCoverPlayer);
                playSong(song);
                break;
            case Song.LOCAL :
                Player.playSong(song);
                Player.currentlyPlayingSong = song;
                break;
            case Song.YOUTUBE :
                youtube_fragment.bringToFront();
                initializeYoutubePlayerFragment(song);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemLongSelected(View view, int position) {
        //add to queue
        Song song = (Song) songs.get(position);
        Player.queue.add(song);
        Toast.makeText(getContext(), "added to queue", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddPlaylistSongClicked(View view, int position) {
        Object song = songs.get(position);
        if (song instanceof Song){
            //add song to playlist
            //DatabaseHelper.addSongToTestPlaylist((Song) song);
//            //update playlist view
//            DatabaseHelper.updateTestPlaylist();

            //launch select playlist fragment
            FragmentManager fm = getActivity().getSupportFragmentManager();
            SelectPlaylistDialogFragment selectPlaylistDialogFragment = SelectPlaylistDialogFragment.newInstance("Select a Playlist", (Song) song, addSongToPlaylistAdapterListener);
            selectPlaylistDialogFragment.show(fm, "lastfm_login");
        }
        else{
            Toast.makeText(getContext(), "can only add a song to a playlist", Toast.LENGTH_SHORT).show();
        }
    }

    public void addSongToPosition (Object song, int position){
        if (position < songs.size() && position >= 0){
            songs.add(position, song);
            complexAdapter.notifyItemInserted(position);
        }
        else{
            Toast.makeText(getContext(), "failed to make playlist. check indexing", Toast.LENGTH_SHORT).show();
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