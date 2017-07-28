package com.ruppal.orbz.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ruppal.orbz.MainActivity;
import com.ruppal.orbz.R;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.models.Message;
import com.ruppal.orbz.models.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elviskahoro on 7/27/17.
 */

public class GQFragment extends QueueFragment {

    ////////////////////////////////////////////////////////// Group Queue
    static final String TAG = MainActivity.class.getSimpleName();
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 3000; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler //fetch messages every second
    ArrayList<Message> mMessages;
    String userId;

    //todo fix this
    //ChatAdapter mAdapter;


    // Keep track of initial load to scroll to the bottom of the ListView
    boolean mFirstLoad;
    ////////////////////////////////////////////////////////// Group Queue


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_group_queue, menu);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        spotifyClient = new SpotifyClient();
        //inflate the layout
        Activity activity = getActivity();
        View v = inflater.inflate(R.layout.fragments_songs_list, container, false);
        FrameLayout frameLayout = (FrameLayout) activity.findViewById(R.id.youtube_fragment);
        ivAlbumCoverPlayer = (ImageView) activity.findViewById(R.id.ivAlbumCoverPlayer);
        youtube_fragment = (FrameLayout) activity.findViewById(R.id.youtube_fragment);
        //find RecyclerView
        rvSongs = (RecyclerView) v.findViewById(R.id.rvSong);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        rvSongs.setLayoutManager(linearLayoutManager);

        //set the adapter
        rvSongs.setAdapter(complexAdapter);
        rvSongs.setBackgroundResource(R.drawable.watermark4);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        rvSongs.addItemDecoration(itemDecoration);

        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ////////////////////////////////////////////////////////// Group Queue
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            setupMessagePosting();
        } else { // If not logged in, login as a new anonymous user
            login();
        }

        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
        ////////////////////////////////////////////////////////// Group Queue
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            case R.id.addPlaylist:
                return false;
            case R.id.backToPlaylists:
                return false;
            case R.id.toggle:
                sendMessage();
                return true;
            default:
                break;
        }

        return false;
    }



    // Create an anonymous user using ParseAnonymousUtils and set sUserId
    void login() {
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: ", e);
                } else {
                    setupMessagePosting();
                }
            }
        });
    }

    // Setup button event handler which posts the entered message to Parse
    // Setup message field and posting
    void setupMessagePosting() {
        // Find the text field and button
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        userId = ParseUser.getCurrentUser().getObjectId();
    }

    void sendMessage(){

        for (Object songObject : songs) {

            if(songObject instanceof Song) {
                Song currentSong = (Song) songObject;
                Message message = new Message();
                message.setUserId(userId);
                message.setTitle(currentSong.getTitle());
                message.setArtistName(currentSong.getArtists().get(0).getName());
                message.setService(currentSong.getService());
                message.setUid(currentSong.getUid());
                message.setArtistId(currentSong.getArtists().get(0).getUid());
                message.setAlbum(currentSong.getAlbum());
                message.setPopularity(currentSong.getPopularity());
                message.setDuration(currentSong.getDuration_ms());

                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(getContext(), "Successfully created message on Parse",
                                Toast.LENGTH_SHORT).show();
                        refreshMessages();
                    }
                });
            }
        }
    }

    // Query messages from Parse so we can load them into the chat adapter
    void refreshMessages() {
        // Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);
        // Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        // get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");
        // Execute query to fetch all messages from Parse asynchronously
        // This is equivalent to a SELECT query with SQL
        query.findInBackground(new FindCallback<Message>() {
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    songs.clear();
                    for (Message currentMessage : messages){
                        Song songToAdd = new Song(
                               currentMessage.getALBUM(),
                                currentMessage.getUID(),
                                currentMessage.getTITLE(),
                                currentMessage.getPopularity(),
                                currentMessage.getDURATION(),
                                currentMessage.getALBUM(),
                                currentMessage.getARTISTID(),
                                currentMessage.getARTISTNAME()
                        );
                        addSong(songToAdd);
                    }
                    complexAdapter.notifyDataSetChanged();
                    if (mFirstLoad) {
                        rvSongs.scrollToPosition(0);
                        mFirstLoad = false;
                    }
                } else {
                    Log.e("message", "Error Loading Messages" + e);
                }
            }
        });
    }

    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };
}
