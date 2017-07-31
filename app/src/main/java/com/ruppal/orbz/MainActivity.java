package com.ruppal.orbz;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.ruppal.orbz.clients.GooglePlayClient;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.clients.YouTubeClient;
import com.ruppal.orbz.fragments.LoginLastFMFragment;
import com.ruppal.orbz.fragments.SearchFragment;
import com.ruppal.orbz.fragments.SongPagerAdapter;
import com.ruppal.orbz.models.Artist;
import com.ruppal.orbz.models.ComponentListener;
import com.ruppal.orbz.models.Song;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;

import static com.ruppal.orbz.fragments.PlaylistSongsFragment.isPlaylistSongsFragment;


public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener{

    public static final String SPOTIFY_PLAYER = "SPOTIFY_PLAYER";
    public static final String SPOTIFY_ACCESS_TOKEN = "SPOTIFY_ACCESS_TOKEN";
    public static final String GOOGLE_ACCESS_TOKEN = "GOOGLE_ACCESS_TOKEN";

    private FragmentPagerAdapter adapterViewPager;
    private LoginLastFMFragment lastFMLogin;
    private SimpleExoPlayerView playerView;

    String spotifyClientId;
    Intent intent;
    SpotifyClient spotifyClient;
    ArrayList<Song> spotifyResults;
    String spotifyAccessToken;
    String googleAccessToken;
    ArrayList<Song> googleResults;
    YouTubeClient youTubeClient;
    GooglePlayClient googlePlayClient;
    SearchFragment searchFragment;
    ViewPager vpPager;

    ArrayList<Song> localSongList;

    //Button Handling
    ImageButton exoPlay;
    ImageButton exoPause;
    ImageButton exoNext;
    ImageButton exoPrev;

    String realQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        spotifyResults = new ArrayList<>();
        googlePlayClient = new GooglePlayClient();
        spotifyClientId = getString(R.string.spotify_client_id);
        intent = getIntent();
        spotifyAccessToken = intent.getStringExtra(SPOTIFY_ACCESS_TOKEN);
        googleAccessToken = intent.getStringExtra(GOOGLE_ACCESS_TOKEN);
        spotifyClient = new SpotifyClient();
        getSpotifyPlayer(spotifyAccessToken);
        youTubeClient = new YouTubeClient();
        youTubeClient.setAccessToken(googleAccessToken);
        googleResults = new ArrayList<>();
        com.ruppal.orbz.models.Player.setActivity(this);

        //get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);

        //adapterViewPager = new SongPagerAdapter(getSupportFragmentManager(), this);
        adapterViewPager = new SongPagerAdapter(getSupportFragmentManager(), this);

        //set the adapter for the pager
        vpPager.setAdapter(adapterViewPager); //

        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);

        exoPlay = (ImageButton) findViewById(R.id.exoPlayer_play);
        exoPause = (ImageButton) findViewById(R.id.exoPlayer_pause);
        exoNext = (ImageButton) findViewById(R.id.exoPlayer_next);
        exoPrev = (ImageButton) findViewById(R.id.exoPlayer_previous);

        ComponentListener componentListener = new ComponentListener();
        playerView = (SimpleExoPlayerView) findViewById(R.id.exoPlayer_view);
        com.ruppal.orbz.models.Player.setComponentListener(componentListener);
        com.ruppal.orbz.models.Player.initializePlayer(this);
        playerView.setPlayer(com.ruppal.orbz.models.Player.exoPlayer);

        localSongList = new ArrayList<Song>();
        if(isExternalStorageWritable() && isExternalStorageReadable()){
            localSongSearch();
        } else {
            Toast.makeText(this, "Check Storage Permissions", Toast.LENGTH_SHORT).show();
        }

        tabLayout.getTabAt(0).setIcon(R.drawable.white_search);
        tabLayout.getTabAt(1).setIcon(R.drawable.white_playlist);
        tabLayout.getTabAt(2).setIcon(R.drawable.local_music);
        tabLayout.getTabAt(3).setIcon(R.drawable.white_queue);
        tabLayout.getTabAt(4).setIcon(R.drawable.white_jukebox);

        //change titles of action bar with each fragment
        vpPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                vpPager.setCurrentItem(position);
                switch(position) {
                    case 0:
                        getSupportActionBar().setTitle("");
                        break;
                    case 1:
                        getSupportActionBar().setTitle("Playlists");
                        break;
                    case 2:
                        getSupportActionBar().setTitle("Local Music");
                        break;
                    case 3:
                        getSupportActionBar().setTitle("Queue");
                        break;
                    case 4:
                        getSupportActionBar().setTitle("Group Queue");
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setActionBarTitle(String title) {
        getSupportActionBar().setTitle(title);
    }


    public static ArrayList<Object> passTest(){
        return SongPagerAdapter.mFragmentReferences.get(3).songs;

    }

//    public void checkData(){
//        List<PlaylistTable> playlistTableList = SQLite.select().
//                from(PlaylistTable.class).queryList();
//        List<SongTable> songTableList = SQLite.select().
//                from(SongTable.class).queryList();
//        Log.i("MY_DATABASE", playlistTableList.toString());
//        Log.i("MY_DATABASE", songTableList.toString());
//    }

    public void getSpotifyPlayer(String accessToken){
        Config playerConfig = new Config(this, accessToken, spotifyClientId);
        Player mPlayer = Spotify.getPlayer(playerConfig, this, null);
        com.ruppal.orbz.models.Player.setSpotifyPlayer(mPlayer);
    }

    public ArrayList<Song> getLocalSongs(){
        return localSongList;
    }

    public void localSongSearch(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst())
        {
            int songId = songCursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int songTitle = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int songArtist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int songData = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);

            do {
                long currentId = songCursor.getLong(songId);
                String currentTitle = songCursor.getString(songTitle);
                String currentData = songCursor.getString(songData);
                Artist artist= new Artist();
                String currentArtist = songCursor.getString(songArtist);
                artist.name = currentArtist;
                artist.uid = null;
                ArrayList<Artist> artists = new ArrayList<>();
                artists.add(artist);

                localSongList.add(new Song(currentId, currentTitle, currentArtist, currentData, artists));

            } while(songCursor.moveToNext());
        }
    }

    @SuppressLint("InlinedApi")
    private void hideSystemUi() {
        playerView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));
    }

    public void pauseCurrentSong(View view){
        com.ruppal.orbz.models.Player.pauseSong(com.ruppal.orbz.models.Player.getCurrentlyPlayingSong());
    }

    public void unPauseCurrentSong(View view) {
        com.ruppal.orbz.models.Player.unPauseSong(com.ruppal.orbz.models.Player.getCurrentlyPlayingSong());
    }

    public void nextInQueue(View v){
        com.ruppal.orbz.models.Player.skipToNextInQueue();
    }
    public void previousInQueue(View v){
        com.ruppal.orbz.models.Player.skipToPreviousInQueue();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
       /// setTitle(getTitleFromPosition(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onBackPressed() {
        if (isPlaylistSongsFragment) {
            SongPagerAdapter.mFragmentReferences.get(1).callChildFrag();//playlist fragment
            isPlaylistSongsFragment = false;

        } else {
            super.onBackPressed();
        }
    }


}