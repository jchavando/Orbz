package com.ruppal.orbz;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.ruppal.orbz.clients.GooglePlayClient;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.clients.YouTubeClient;
import com.ruppal.orbz.fragments.AddPlaylistDialogFragment;
import com.ruppal.orbz.fragments.LoginLastFMFragment;
import com.ruppal.orbz.fragments.SearchFragment;
import com.ruppal.orbz.fragments.SongPagerAdapter;
import com.ruppal.orbz.models.Artist;
import com.ruppal.orbz.models.Song;
import com.spotify.sdk.android.player.Config;
import com.spotify.sdk.android.player.Player;
import com.spotify.sdk.android.player.Spotify;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static final String SPOTIFY_PLAYER = "SPOTIFY_PLAYER";
    public static final String SPOTIFY_ACCESS_TOKEN = "SPOTIFY_ACCESS_TOKEN";
    public static final String GOOGLE_ACCESS_TOKEN = "GOOGLE_ACCESS_TOKEN";
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

    private FragmentPagerAdapter adapterViewPager;


    private LoginLastFMFragment lastFMLogin;







    //Elvis
    ArrayList<Song> localSongList;


    //Elvis

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

        //get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new SongPagerAdapter(getSupportFragmentManager(), this);
        //set the adapter for the pager
        vpPager.setAdapter(adapterViewPager);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(vpPager);


       /////////////Elvis Kahoro
        localSongList = new ArrayList<Song>();
        if(isExternalStorageWritable() && isExternalStorageReadable()){
            localSongSearch();
        } else {
            Toast.makeText(this, "Check Storage Permissions", Toast.LENGTH_SHORT).show();
        }
        //////////////////////////Elvis Kahoro



        tabLayout.getTabAt(0).setIcon(R.drawable.white_search);
        tabLayout.getTabAt(1).setIcon(R.drawable.play);
        tabLayout.getTabAt(2).setIcon(R.drawable.local_music);


    }


//    public void checkData(){
//        List<PlaylistTable> playlistTableList = SQLite.select().
//                from(PlaylistTable.class).queryList();
//        List<SongTable> songTableList = SQLite.select().
//                from(SongTable.class).queryList();
//        Log.i("MY_DATABASE", playlistTableList.toString());
//        Log.i("MY_DATABASE", songTableList.toString());
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchFragment searchFragment= (SearchFragment) SongPagerAdapter.mFragmentReferences.get(0);
                searchFragment.clearSongsList();
                searchFragment.searchSongs(query);

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }





    public void getSpotifyPlayer(String accessToken){
        Config playerConfig = new Config(this, accessToken, spotifyClientId);
        Player mPlayer = Spotify.getPlayer(playerConfig, this, null);
        com.ruppal.orbz.models.Player.setSpotifyPlayer(mPlayer);
    }











    //////////From Player Activity
    public ArrayList<Song> getLocalSongs(){
        return localSongList;
    }









    public void localSongSearch(){
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Cursor songCursor = contentResolver.query(songUri, null, null, null, null);

        if(songCursor != null && songCursor.moveToFirst())
        {
            Toast.makeText(this, "starting song cursor", Toast.LENGTH_SHORT).show();
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


    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return (Environment.MEDIA_MOUNTED.equals(state) || Environment.MEDIA_MOUNTED_READ_ONLY.equals(state));}

}
