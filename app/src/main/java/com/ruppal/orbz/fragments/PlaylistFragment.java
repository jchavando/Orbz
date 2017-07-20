package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.database.DatabaseHelper;
import com.ruppal.orbz.database.PlaylistTable;
import com.ruppal.orbz.database.PlaylistTable_Table;
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


public class PlaylistFragment extends SongListFragment {

    SpotifyClient spotifyClient;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        spotifyClient = new SpotifyClient();
//        songs = new ArrayList<>();
        populatePlaylists();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getLocalPlaylists();
        }
    }

    public void getLocalPlaylists(){
        //is there a faster way to do this?
        List<PlaylistTable> playlistTableList = SQLite.select().
                from(PlaylistTable.class).queryList();
        for (int i =0; i < playlistTableList.size(); i++){
            PlaylistTable playlistTable = playlistTableList.get(i);
            //search songs in this playlist table
            List<SongTable> songTableList = SQLite.select().
                    from(SongTable.class).
                    where(PlaylistTable_Table.playlistId.is(playlistTable.getPlaylistId())).
                    queryList();
            ArrayList<Song> songsInPlaylist = new ArrayList<>();
            for (int j=0; j< songTableList.size(); i++){
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
}

