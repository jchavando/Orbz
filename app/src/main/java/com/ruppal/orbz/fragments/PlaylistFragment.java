package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.database.DatabaseHelper;
import com.ruppal.orbz.database.PlaylistTable;
import com.ruppal.orbz.database.SongTable;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


/**
 * Created by jchavando on 7/13/17.
 */


public class PlaylistFragment extends SongListFragment implements AddPlaylistDialogFragment.AddPlaylistListener, ComplexRecyclerViewAdapter.AddSongToPlaylistAdapterListener{ //implements ComplexRecyclerViewAdapter.PlaylistAdapterListener
//extends SongListFragment
    SpotifyClient spotifyClient;
    ArrayList<Playlist> spotifyPlaylists;
    Playlist playlist;
    String newPlaylist;
    ArrayList<Playlist> playlistsFromDatabase;
    FloatingActionButton fabAddPlaylist;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLocalPlaylists();
        addLocalPlaylistsToSongs();
        populateSpotifyPlaylists();

//        populateAllPlaylists();

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotifyClient = new SpotifyClient();
        spotifyPlaylists = new ArrayList<>();
        //fabAddPlaylist.setOnClickListener(this); //TODO fix
        setHasOptionsMenu(true);
        addSongToPlaylistAdapterListener = this;
    }

    public void addLocalPlaylistsToSongs(){
        for (int i = 0; i<playlistsFromDatabase.size(); i++){
            addSong(playlistsFromDatabase.get(i));
        }
    }


    public void getLocalPlaylists() {
        playlistsFromDatabase = DatabaseHelper.getLocalPlaylists();
    }

    public void updateLocalPlaylists(){
        for (int i =0 ; i < playlistsFromDatabase.size(); i++){
            songs.set(i, playlistsFromDatabase.get(i));
            complexAdapter.notifyItemChanged(i);
        }
    }

//    public void populateAllPlaylists(){
//        for (int i=0; i< spotifyPlaylists.size(); i++){
//            addSong(spotifyPlaylists.get(i));
//        }
//        for (int i =0 ; i < playlistsFromDatabase.size()){
//
//        }
//    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_playlist, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            case R.id.addPlaylist:
                showPlaylistFragment();
                return true;
            default:
                break;
        }

        return false;
    }



    public void populateSpotifyPlaylists(){
        //make sure to clear out songs so playlists show instead
//        clearSongsList();
        spotifyClient.getMyPlaylists(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                try {
                    JSONArray items = response.getJSONArray("items");
                    for (int i =0; i<items.length(); i++){
                        JSONObject item = items.getJSONObject(i);
                        Playlist playlist = Playlist.fromJSON(Song.SPOTIFY, item);
//                        spotifyPlaylists.add(playlist);
                        addSong(playlist);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Log.e("playlists", responseString);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("playlists", errorResponse.toString());
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                Log.e("playlists", errorResponse.toString());
            }
        });
    }


    public void showPlaylistFragment() {
        Toast.makeText(getContext(), "clicked fab", Toast.LENGTH_SHORT).show();
        FragmentManager fm = getActivity().getSupportFragmentManager();

        //AddPlaylistDialogFragment addPlaylist = AddPlaylistDialogFragment.newInstance("some_title");
        //addPlaylist.setTargetFragment(PlaylistFragment.this, 300);
        //addPlaylist.show(fm, "add playlist");
        AddPlaylistDialogFragment addPlaylist = AddPlaylistDialogFragment.newInstance("some_title", this);
        addPlaylist.show(fm, "lastfm_login");
    }

    @Override
    public void onFinishDialog(String newPlaylistName) {
        //adds playlist to songs
        //adds playlist to database
        Playlist newPlaylist = DatabaseHelper.makeNewLocalPlaylist(newPlaylistName);
        int positionInsert = 0;
        addSongToPosition(newPlaylist, positionInsert);
        rvSongs.scrollToPosition(positionInsert);
        playlistsFromDatabase.add(newPlaylist);
    }


    @Override
    public void addSongToPlaylist(Song song, PlaylistTable playlistTable) {
        //todo check if song is already in the playlist
        SongTable newSongTableAdded = DatabaseHelper.makeNewSongTable(song, playlistTable);
        //else find it
        //add to foreign key
        newSongTableAdded.setPlaylistTable(playlistTable);
        //add to playlist track by updating
        getLocalPlaylists();
        updateLocalPlaylists();
    }
}

