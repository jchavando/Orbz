package com.ruppal.orbz;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.ruppal.orbz.models.Song;

import java.util.ArrayList;

/**
 * Created by jchavando on 7/19/17.
 */

public class PlaylistActivity extends AppCompatActivity{

    //recycler view for when you click on individual playlist
    RecyclerView rvSongs;

    ArrayList<Song> songs;

    //all songs in playlist

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Lookup the recyclerview in activity layout
        RecyclerView rvSongs = (RecyclerView) findViewById(R.id.rvPlaylist);

        // Initialize contacts
        songs = new ArrayList<>();//populate arraylist later
        rvSongs.setLayoutManager(new LinearLayoutManager(this));


        //find RecyclerView
        rvSongs = (RecyclerView) findViewById(R.id.rvSong);
        //init the arraylist (data source)
        songs = new ArrayList<>();
        //construct adapter from datasource
        SongAdapter songAdapter = new SongAdapter(songs, this, mainActivity); //this
        //recyclerView setup (layout manager, use adapter)
        rvSongs.setLayoutManager(new LinearLayoutManager(this));
        //set the adapter
        rvSongs.setAdapter(songAdapter);


    }



}
