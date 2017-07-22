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
import com.raizlabs.android.dbflow.sql.language.SQLite;
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
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * Created by jchavando on 7/13/17.
 */


public class PlaylistFragment extends SongListFragment implements AddPlaylistDialogFragment.AddPlaylistListener{ //implements ComplexRecyclerViewAdapter.PlaylistAdapterListener
//extends SongListFragment
    SpotifyClient spotifyClient;
    Playlist playlist;
    String newPlaylist;

    FloatingActionButton fabAddPlaylist;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getLocalPlaylists();
        populatePlaylists();
        //fabAddPlaylist = (FloatingActionButton) view.findViewById(R.id.fabAddPlaylist);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotifyClient = new SpotifyClient();
//        songs = new ArrayList<>();
//        populatePlaylists();
        //fabAddPlaylist.setOnClickListener(this); //TODO fix
        setHasOptionsMenu(true);


    }

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





    //    @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (isVisibleToUser) {
//            getLocalPlaylists();
//        }
//    }

    public void getLocalPlaylists(){
        //is there a faster way to do this?
        List<PlaylistTable> playlistTableList = SQLite.select().
                from(PlaylistTable.class).queryList();
        for (int i =0; i < playlistTableList.size(); i++){
            PlaylistTable playlistTable = playlistTableList.get(i);
            //search songs in this playlist table
            List<SongTable> songTableList = SQLite.select().
                    from(SongTable.class).
//                    where(PlaylistTable_Table.playlistName.is(playlistTable.getPlaylistName())).
                    queryList();
            ArrayList<Song> songsInPlaylist = new ArrayList<>();
            for (int j=0; j< songTableList.size(); j++){
                SongTable songTable = songTableList.get(j);
                Song song = DatabaseHelper.songFromSongTable(songTable);
                songsInPlaylist.add(song);
            }
            Playlist playlist = DatabaseHelper.playlistFromPlaylistTable(playlistTable);
            playlist.setTracks(songsInPlaylist);
            songs.add(playlist);
        }
    }

    public void populatePlaylists(){
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



   // @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            case fabAddPlaylist:
//                showPlaylistFragment(); //TODO
//                break;
//        }
//    }


    public void showPlaylistFragment() {
        Toast.makeText(getContext(), "clicked fab", Toast.LENGTH_SHORT).show();
        FragmentManager fm = getActivity().getSupportFragmentManager();

        //AddPlaylistDialogFragment addPlaylist = AddPlaylistDialogFragment.newInstance("some_title");
        //addPlaylist.setTargetFragment(PlaylistFragment.this, 300);
        //addPlaylist.show(fm, "add playlist");
        AddPlaylistDialogFragment addPlaylist = AddPlaylistDialogFragment.newInstance("some_title");
        addPlaylist.show(fm, "lastfm_login");
    }

    @Override
    public void onFinishDialog(String newPlaylist) {
        this.newPlaylist = newPlaylist;

        //add to list of existing playlists
    }

//    @Override
//    public void onClick(View v) {
//
//            switch (v.getId()) {
//                case R.id.fabAddPlaylist:
//                    showPlaylistFragment();
//                    break;
//            }
//
//    }
}

