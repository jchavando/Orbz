package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;

import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.models.Player;
import com.ruppal.orbz.models.Song;

import java.util.ArrayList;

/**
 * Created by elviskahoro on 7/20/17.
 */

public class LocalListFragment extends SongListFragment implements Player.highlightCurrentSongListenerLocal, ComplexRecyclerViewAdapter.SongAdapterListenerLocal { //SearchFragment
    public static addSongToQueueListenerLocal mAddSongToQueueListenerLocal;

    public interface addSongToQueueListenerLocal{
        void onSongQueueAddedLocal();
    }

    public static void setmAddSongToQueueListenerLocal(addSongToQueueListenerLocal addSongToQueueListenerLocal) {
        mAddSongToQueueListenerLocal = addSongToQueueListenerLocal;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clearSongsList();
        printArrayList(localSongList);
        populateLocalList(localSongList);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Player.setmHighlightCurrentSongListenerLocal(this);
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

    @Override
    public void onSongPlayingChanged() {
        complexAdapter.notifyDataSetChanged();
    }


    //todo refactor this code
    @Override
    public void onItemSelected(View view, int position) {
        //play the song
        super.onItemSelected(view, position);
        if (songs!=null && com.ruppal.orbz.models.Player.automaticQueue != null && position>=0 && position<songs.size()) {
            Song songSelected = (Song) songs.get(position);
            //clear old queue
            com.ruppal.orbz.models.Player.clearQueue();
            com.ruppal.orbz.models.Player.clearAutomaticQueue();
            //set songs for the new queue
            automaticallyPopulateQueue(songSelected);
        }
    }



    public void automaticallyPopulateQueue(Song songSelected){
        //required : position is position of song clicked, so want to start at next
        boolean beforeSelected = true;
        ArrayList<Song> songsBeforeSelected = new ArrayList<>();
        for (int i = 0; i < songs.size(); i++){
            Song song = (Song) songs.get(i);
            if (song != songSelected && beforeSelected){
                songsBeforeSelected.add(song);
            }
            else{
                if (song == songSelected) {
                    beforeSelected = false;
                }
                else {
                    com.ruppal.orbz.models.Player.automaticQueue.add(song);
                }
            }
        }
        com.ruppal.orbz.models.Player.automaticQueue.addAll(songsBeforeSelected);
        com.ruppal.orbz.models.Player.automaticQueue.add(songSelected);
    }

    @Override
    public void onItemLongSelected(View view, int position) {
        //call super method
        super.onItemLongSelected(view, position);
        //call listener
        if (mAddSongToQueueListenerLocal!=null){
            mAddSongToQueueListenerLocal.onSongQueueAddedLocal();
        }
    }
}