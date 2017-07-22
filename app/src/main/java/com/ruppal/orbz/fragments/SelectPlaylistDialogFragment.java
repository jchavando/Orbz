package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruppal.orbz.R;

import static com.ruppal.orbz.R.id.rvPlaylist;

/**
 * Created by ruppal on 7/21/17.
 */

public class SelectPlaylistDialogFragment extends DialogFragment {
    RecyclerView rvPlaylist;

    public SelectPlaylistDialogFragment(){
        //empty constructor
    }

    public static SelectPlaylistDialogFragment newInstance(String title){
        SelectPlaylistDialogFragment selectPlaylistDialogFragment = new SelectPlaylistDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        selectPlaylistDialogFragment.setArguments(args);
        return selectPlaylistDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_playlist_dialog_fragment, container);
        rvPlaylist = (RecyclerView) view.findViewById(R.id.rvPlaylist);
        rvPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlaylist.setAdapter(simpleAdapter);
        return view;
    }


}
