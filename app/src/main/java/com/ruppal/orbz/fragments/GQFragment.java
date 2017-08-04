package com.ruppal.orbz.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.ruppal.orbz.ComplexRecyclerViewAdapter;
import com.ruppal.orbz.MainActivity;
import com.ruppal.orbz.R;
import com.ruppal.orbz.models.Message;
import com.ruppal.orbz.models.Player;
import com.ruppal.orbz.models.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by elviskahoro on 7/27/17.
 */

public class GQFragment extends SongListFragment implements Player.highlightCurrentSongListenerGroupQueue, ComplexRecyclerViewAdapter.SongAdapterListenerGroupQueue{

    ////////////////////////////////////////////////////////// Group Queue
    static final String TAG = MainActivity.class.getSimpleName();
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;
    // Create a handler which can run code periodically
    static final int POLL_INTERVAL = 1500; // milliseconds
    Handler myHandler = new Handler();  // android.os.Handler //fetch messages every second
    ArrayList<Message> mMessages;
    String userId;
    boolean mFirstLoad;
    public static addSongToQueueListenerGQ mAddSongToQueueListenerGQ;

    public interface addSongToQueueListenerGQ{
        void onSongQueueAddedGQ();
    }

    public static void setmAddSongToQueueListenerGQ(addSongToQueueListenerGQ addSongToQueueListenerGQ) {
        mAddSongToQueueListenerGQ = addSongToQueueListenerGQ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        Player.setmHighlightCurrentSongListenerGroupQueue(this);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_group_queue, menu);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (ParseUser.getCurrentUser() != null) { // start with existing user
            setupMessagePosting();
        } else { // If not logged in, login as a new anonymous user
            login();
        }
        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
        ArrayList<Object> queueList = MainActivity.passTest();
        for (Object songObject : queueList) {

            if(songObject instanceof Song) {
                Song currentSong = (Song) songObject;
                if(!currentSong.isPushed() && !currentSong.getService().equals(Song.LOCAL)){
                    Message message = new Message();
                    message.setUserId(ParseUser.getCurrentUser().getObjectId());
                    message.setTitle(currentSong.getTitle());
                    message.setArtistName(currentSong.getArtists().get(0).getName());
                    message.setService(currentSong.getService());
                    message.setUid(currentSong.getUid());
                    message.setArtistId(currentSong.getArtists().get(0).getUid());
                    message.setPopularity(currentSong.getPopularity());
                    message.setDuration(currentSong.getDuration_ms());
                    message.setAlbumCover(currentSong.getAlbumCoverUrl());
                    message.setQueued(currentSong.getQueued());
                    if(currentSong.getComment() != null)
                    message.setComment(currentSong.getComment());
                    if(currentSong.getService().equals(Song.SPOTIFY))
                    message.setAlbum(currentSong.getAlbum());

                    message.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
//                            Toast.makeText(getContext(), "Successfully created message on Parse",
//                                    Toast.LENGTH_SHORT).show();
                            refreshMessages();
                        }
                    });
                    ((Song) songObject).setPushed();
                }
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
                            currentMessage.getALBUMCOVER(),
                            currentMessage.getARTISTID(),
                            currentMessage.getARTISTNAME()
                        );
                        songToAdd.setService(currentMessage.getSERVICE());
                        songToAdd.setQueued(currentMessage.getQueued());
                        if(currentMessage.getComment()!=null)
                        songToAdd.setComment(currentMessage.getComment());
                        if(songToAdd.getTitle() != null && !songToAdd.getTitle().isEmpty())
                        songs.add(songToAdd);
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

    @Override
    public void onSongPlayingChanged() {
        complexAdapter.notifyDataSetChanged();
    }

    //todo refactor this code with design stuff
    @Override
    public void onItemSelected(View view, int position) {
        //play the song
        super.onItemSelected(view, position);
        if (songs!=null && Player.automaticQueue != null && position>=0 && position<songs.size()) {
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
        if (mAddSongToQueueListenerGQ!=null){
            mAddSongToQueueListenerGQ.onSongQueueAddedGQ();
        }
    }
}
