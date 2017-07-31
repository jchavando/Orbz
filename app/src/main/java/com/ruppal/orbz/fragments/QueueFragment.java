package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


import com.ruppal.orbz.MainActivity;
import com.ruppal.orbz.R;
import com.ruppal.orbz.models.Song;

import static com.ruppal.orbz.models.Player.queue;

/**
 * Created by jchavando on 7/26/17.
 */

public class QueueFragment extends SongListFragment {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clearSongsList();
        populateQueue();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    public void populateQueue(){
        for (int i = 0; i< queue.size(); i++){
            Song song = queue.get(i);
            addSong(song);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_queue, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.queue_clear:
                clearSongsList();
                return true;
            default:
                break;
        }
        return false;
    }
}
