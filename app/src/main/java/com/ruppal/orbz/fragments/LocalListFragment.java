package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.ruppal.orbz.MainActivity;
import com.ruppal.orbz.models.Song;

import java.util.ArrayList;

/**
 * Created by elviskahoro on 7/20/17.
 */

public class LocalListFragment extends SongListFragment {

    public ArrayList<Song> localSongList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        localSongList = new ArrayList<>();
        localSongList = ((MainActivity)getActivity()).getLocalSongs();
        printArrayList(localSongList);
        songs = new ArrayList<>();

        populateLocalList(localSongList);
    }

    public void populateLocalList (ArrayList<Song> songsToAdd){
        for (int i = 0; i < songsToAdd.size(); i++)
        {
            addSong(songsToAdd.get(i));
        }
    }

    public void printArrayList(ArrayList<Song> songListPrint){
        for (Song test : songListPrint)
            Log.d("Elvis_Song_List", test.getTitle());
    }
}