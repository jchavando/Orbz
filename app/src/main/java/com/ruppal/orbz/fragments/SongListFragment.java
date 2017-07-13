package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ruppal.orbz.R;
import com.ruppal.orbz.SongAdapter;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
/**
 * Created by jchavando on 7/13/17.
 */

public class SongListFragment extends Fragment implements SongAdapter.SongAdapterListener{

    public interface SongSelectedListener{
        public void onSongSelected(Song song);
    }

    @Override
    public void onItemSelected(View view, int position, boolean isPic) {
        Song song = songs.get(position);
        ((SongSelectedListener) getActivity()).onSongSelected(song);

    }


    private final int REQUEST_CODE = 20;
    public SongAdapter songAdapter;
    public ArrayList<Song> songs;
    public RecyclerView rvSongs;


    //inflation happens inside onCreateView

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //inflate the layout
        View v = inflater.inflate(R.layout.fragments_songs_list, container, false);

        //find RecyclerView
        rvSongs = (RecyclerView) v.findViewById(R.id.rvSong);
        //init the arraylist (data source)
        songs = new ArrayList<>();
        //construct adapter from datasource
        songAdapter = new SongAdapter(songs, this); //this
        //recyclerView setup (layout manager, use adapter)
        rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvSongs.setAdapter(songAdapter);


        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvSongs.addItemDecoration(itemDecoration);

        return v;
    }



    public void addItems (String service, JSONArray response){
        for (int i = 0; i < response.length(); i++){
            //convert each object to a Song model
            //add that Song model to our data source
            //notify the adapter that we've added an item (list view)
            Song song = null;
            try {
                song = Song.fromJSON(service, response.getJSONObject(i));
                songs.add(song);
                songAdapter.notifyItemInserted(songs.size()-1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    //adds one tweet at top
//    public void postTweet(Song song){
//        tweets.add(0, tweet);
//        tweetAdapter.notifyItemInserted(0);
//        rvTweets.scrollToPosition(0);
//    }

//    @Override
//    public void onItemSelected(View view, int position, boolean isPic) {
//        Song song = songs.get(position);
//        if(!isPic) {
//            //((SongSelectedListener) getActivity()).onTweetSelected(tweet);
//        } else {
//            //((TweetSelectedListener) getActivity()).onImageSelected(tweet);
//        }
//
//    }





}
