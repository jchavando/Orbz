package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.R;
import com.ruppal.orbz.models.Player;
import com.ruppal.orbz.models.Song;

import static com.ruppal.orbz.models.Player.queue;

/**
 * Created by jchavando on 7/26/17.
 */


public class QueueFragment extends SongListFragment implements AddCommentDialogFragment.AddCommentListener,
                                                    Player.highlightCurrentSongListenerQueue,
                                                    ComplexRecyclerViewAdapter.AddCommentAdapterListener,
                                                    LocalListFragment.addSongToQueueListenerLocal,
                                                    GQFragment.addSongToQueueListenerGQ{

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        clearSongsList();
        populateQueue();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addCommentAdapterListener = this;
        setHasOptionsMenu(true);
        Player.setmHighlightCurrentSongListenerQueue(this);
        LocalListFragment.setmAddSongToQueueListenerLocal(this);
        GQFragment.setmAddSongToQueueListenerGQ(this);
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
                Player.queue.clear();
                clearSongsList();
                return true;
            default:
                break;
        }
        return false;
    }

    @Override
    public void addComment(String comment, Song song) {
        song.setComment(comment);
        //Toast.makeText(getContext(), "comment: " +comment, Toast.LENGTH_SHORT).show();
        complexAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSongPlayingChanged() {
        complexAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSongQueueAddedLocal() {
        //populate queue again
        populateQueue();
    }

    @Override
    public void onSongQueueAddedGQ() {
        populateQueue();
    }
}
