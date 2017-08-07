package com.ruppal.orbz.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.ItemDecoration;
import com.ruppal.orbz.MainActivity;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.database.PlaylistTable;
import com.ruppal.orbz.models.Player;
import com.ruppal.orbz.models.Playlist;
import com.ruppal.orbz.models.Song;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;

/**
 * Created by jchavando on 7/13/17.
 */

public class SongListFragment extends Fragment implements ComplexRecyclerViewAdapter.SongAdapterListener, ComplexRecyclerViewAdapter.PlaylistAdapterListener,  YouTubePlayer.Provider {

    private ComplexRecyclerViewAdapter.PlaylistAdapterListener playlistAdapterListener;
    private final int REQUEST_CODE = 20;
    public ComplexRecyclerViewAdapter complexAdapter;
    public ArrayList<Object> songs;
    public RecyclerView rvSongs;
    public static ArrayList<Song> localSongList;
    public static ArrayList<PlaylistTable> localPlaylistTables;
    static ComplexRecyclerViewAdapter.AddSongToPlaylistAdapterListener addSongToPlaylistAdapterListener;
    static ComplexRecyclerViewAdapter.AddCommentAdapterListener addCommentAdapterListener;

    public static FragmentManager fragmentManager;
    public FragmentTransaction transaction;
    SpotifyClient spotifyClient;
    String SONG_TO_PLAY = "SONG_TO_PLAY";
    ImageView ivAlbumCoverPlayer;
    FrameLayout youtube_fragment;
    FrameLayout playlistFrameLayout;
    public static boolean isPlaylistSongsFragment;
    //ImageButton ibAddComment;
    boolean clicked = false;
    private static int VERTICAL_ITEM_SPACING = 10;


    public  PlaylistSongsFragment childFragment;


    @Override
    public void initialize(String s, YouTubePlayer.OnInitializedListener onInitializedListener) {
    }

    @Override
    public void onPlaylistItemSelected(View view, int position) {
        final Playlist playlist = (Playlist) songs.get(position);

        if (playlist != null) {
            insertPlaylistSongsFragment(playlist);
            isPlaylistSongsFragment = true;
        }
    }
    // Embeds the child fragment dynamically
    private void insertPlaylistSongsFragment(Playlist playlist) {

        childFragment = new PlaylistSongsFragment(); //PlaylistSongsFragment
        Bundle arguments = new Bundle();
        arguments.putParcelable("tracks", Parcels.wrap(playlist));
        childFragment.setArguments(arguments);

        transaction = getChildFragmentManager().beginTransaction();
        playlistFrameLayout = (FrameLayout) getView().findViewById(R.id.flPlaylistFragment);
        playlistFrameLayout.bringToFront();
        transaction.replace(R.id.flPlaylistFragment, childFragment, "playlistfrag"); //add

        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void callChildFrag(){
        childFragment.playlistSongsBack();
    }


    @Override
    public void onResume() {
        super.onResume();
        rvSongs.bringToFront();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        songs = new ArrayList<>();
        localSongList = ((MainActivity)getActivity()).getLocalSongs();
        complexAdapter = new ComplexRecyclerViewAdapter(songs, this, this, null); //this
        fragmentManager = getFragmentManager();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        spotifyClient = new SpotifyClient();
        //inflate the layout
        Activity activity = getActivity();
        View v = inflater.inflate(R.layout.fragments_songs_list, container, false);
        //frameLayout = (FrameLayout) activity.findViewById(R.id.youtube_fragment);
        ivAlbumCoverPlayer = (ImageView) activity.findViewById(R.id.ivAlbumCoverPlayer);
        youtube_fragment = (FrameLayout) activity.findViewById(R.id.youtube_fragment);
        //ibAddComment = (ImageButton) activity.findViewById(R.id.ibAddComment);
        //find RecyclerView
        rvSongs = (RecyclerView) v.findViewById(R.id.rvSong);
        //recyclerView setup (layout manager, use adapter)
        rvSongs.setLayoutManager(new LinearLayoutManager(getContext()));
        //set the adapter
        rvSongs.setAdapter(complexAdapter);

        rvSongs.setBackgroundResource(R.drawable.soundwaves);
        RecyclerView.ItemDecoration itemDecoration = new ItemDecoration(VERTICAL_ITEM_SPACING);
        rvSongs.addItemDecoration(itemDecoration);
        if (complexAdapter!=null){
            complexAdapter.notifyDataSetChanged();
        }

        return v;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
    }

    @Override
    public void onItemSelected(View view, int position) {
        Song song = (Song) songs.get(position);
        Player.playSong(song);
    }


    public void grabInfo(){
        ((MainActivity) getActivity()).getAlbumArt();
    }



    @Override
    public void onItemLongSelected(View view, int position) {
        //add to queue
        Song song = (Song) songs.get(position);
        Player.queue.add(song);
        song.setQueued(true);
        //Toast when adding to queue, changes color of default
        Toast toast = Toast.makeText(getContext(), "added to queue!", Toast.LENGTH_SHORT);
        view = toast.getView();
        view.setBackgroundResource(R.drawable.rounded);
        TextView text = (TextView) view.findViewById(android.R.id.message);
        text.setBackgroundResource(R.color.transparentWhite);
        //text.setTextColor(0xf22fb1a4);
        toast.show();
    }

    @Override
    public void onAddPlaylistSongClicked(View view, int position) {

        Object song = songs.get(position);
        if (song instanceof Song){
            FragmentManager fm = getActivity().getSupportFragmentManager();
            SelectPlaylistDialogFragment selectPlaylistDialogFragment = SelectPlaylistDialogFragment.newInstance("Select a Playlist", (Song) song, addSongToPlaylistAdapterListener);
            selectPlaylistDialogFragment.show(fm, "lastfm_login");
        }
        else{
            //makeText(getContext(), "can only add a song to a playlist", Toast.LENGTH_SHORT).show();
            Toast toast = Toast.makeText(getContext(), "can only add a song to a playlist", Toast.LENGTH_SHORT);
            view = toast.getView();
            view.setBackgroundResource(R.drawable.rounded);
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setBackgroundResource(R.color.transparentWhite);
            toast.show();

        }

    }

    @Override
    public void onAddCommentClicked(View view, int position) {
        Object song = songs.get(position);
        if (song instanceof Song) {
            if(((Song) song).getComment() == null) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                AddCommentDialogFragment addCommentDialogFragment = AddCommentDialogFragment.newInstance("some title", (Song) song, addCommentAdapterListener);
                addCommentDialogFragment.show(fm, "add comment");

             } else {
                clicked = true;
                FragmentManager fm = getActivity().getSupportFragmentManager();
                ViewCommentDialogFragment viewCommentDialogFragment = ViewCommentDialogFragment.newInstance("some title", (Song) song, addCommentAdapterListener); //TODO VIEW comment
                viewCommentDialogFragment.show(fm, "view comment");
            }


        } else {

            //makeText(getContext(), "can only add a comment to a queued song", Toast.LENGTH_SHORT).show();


            Toast toast = Toast.makeText(getContext(), "can only add a comment to a queued song", Toast.LENGTH_SHORT);
            view = toast.getView();
            view.setBackgroundResource(R.drawable.rounded);
            TextView text = (TextView) view.findViewById(android.R.id.message);
            text.setBackgroundResource(R.color.transparentWhite);
            toast.show();
        }
    }

    public void addSongToPosition (Object song, int position){
        if (position < songs.size() && position >= 0){
            songs.add(position, song);
            complexAdapter.notifyItemInserted(position);
        }
        else{
//            Toast.makeText(getContext(), "failed to make playlist. check indexing", Toast.LENGTH_SHORT).show();
        }
    }

    public void addSong (Object song){
        songs.add(song);
        complexAdapter.notifyItemInserted(songs.size()-1);
    }

    public void clearSongsList(){
        songs.clear();
        complexAdapter.notifyDataSetChanged();
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
                complexAdapter.notifyItemInserted(songs.size()-1);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean containsIgnoreCase(final String str, final String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        final int len = searchStr.length();
        final int max = str.length() - len;
        for (int i = 0; i <= max; i++) {
            if (str.regionMatches(true, i, searchStr, 0, len)) {
                return true;
            }
        }
        return false;
    }
    

    //


}