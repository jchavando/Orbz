package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.R;
import com.ruppal.orbz.database.DatabaseHelper;
import com.ruppal.orbz.database.PlaylistTable;
import com.ruppal.orbz.models.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ruppal on 7/21/17.
 */

public class SelectPlaylistDialogFragment extends DialogFragment implements ComplexRecyclerViewAdapter.PlaylistSimpleAdapterListener {
    RecyclerView rvPlaylist;
    TextView tvAskUser;
    Song songSelected;
    Button btAddSong;
    int positionOfSelectedPlaylist;
    ArrayList<Object> playlists;
    ComplexRecyclerViewAdapter complexAdapter;
    PlaylistTable selectedPlaylist;
    ComplexRecyclerViewAdapter.AddSongToPlaylistAdapterListener mListener;

    public SelectPlaylistDialogFragment(Song song, ComplexRecyclerViewAdapter.AddSongToPlaylistAdapterListener listener){
        //empty constructor
        songSelected = song;
        mListener = listener;
    }

    public static SelectPlaylistDialogFragment newInstance(String title, Song song,ComplexRecyclerViewAdapter.AddSongToPlaylistAdapterListener listener ){
        SelectPlaylistDialogFragment selectPlaylistDialogFragment = new SelectPlaylistDialogFragment(song, listener);
        Bundle args = new Bundle();
        args.putString("title", title);
        selectPlaylistDialogFragment.setArguments(args);
        return selectPlaylistDialogFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playlists = new ArrayList<>();
        complexAdapter = new ComplexRecyclerViewAdapter(playlists, null, null, this); //this
        selectedPlaylist = null;
        //load playlists
        int numPlaylists = populateSimplePlaylists();
        Toast.makeText(getContext(), numPlaylists + " playlists added", Toast.LENGTH_SHORT).show();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.choose_playlist_dialog_fragment, container);
        tvAskUser = (TextView) view.findViewById(R.id.tvAskUser);
        int sizeArtists = songSelected.getArtists().size();
        String artistList="";
        for (int i = 0; i < sizeArtists; i++){
            artistList = artistList + songSelected.artists.get(i).name;
            if (i < sizeArtists -1){
                artistList += ", ";
            }
        }
        String askUserText = "Add "+ songSelected.getTitle()+ " by " + artistList +" from " + songSelected.getService() + " to a playlist";
        tvAskUser.setText(askUserText);
        btAddSong = (Button) view.findViewById(R.id.btAddSong);
        btAddSong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPlaylist == null){
                    Toast.makeText(getContext(), "select a playlist!", Toast.LENGTH_SHORT).show();
                }
                else {
                    //add songSelected to selectedPlaylist
                    mListener.addSongToPlaylist(songSelected, selectedPlaylist);
                    dismiss();
                }
            }
        });
        rvPlaylist = (RecyclerView) view.findViewById(R.id.rvPlaylistSimple);
        rvPlaylist.setLayoutManager(new LinearLayoutManager(getContext()));
        rvPlaylist.setAdapter(complexAdapter);
        return view;
    }

    @Override
    public void onPlaylistSimpleItemSelected(View view, int position) {
        //get the name of playlist you are adding song to
        //and send it back
        selectedPlaylist =(PlaylistTable) playlists.get(position);
        positionOfSelectedPlaylist = position;
        String selectedText = selectedPlaylist.getPlaylistName() + " selected";
        Toast.makeText(getContext(),selectedText , Toast.LENGTH_SHORT).show();
    }

    public int populateSimplePlaylists(){
        List<PlaylistTable> allPlaylists = DatabaseHelper.getAllPlaylists();
        for (int i=0; i<allPlaylists.size(); i++){
            PlaylistTable playlistTable = allPlaylists.get(i);
            playlists.add(playlistTable);
            complexAdapter.notifyItemInserted(allPlaylists.size()-1);
        }
        return allPlaylists.size();
    }
}
