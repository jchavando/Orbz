package com.ruppal.orbz;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.ruppal.orbz.clients.GooglePlayClient;
import com.ruppal.orbz.clients.SpotifyClient;
import com.ruppal.orbz.fragments.SearchFragment;
import com.ruppal.orbz.fragments.SongPagerAdapter;
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
    Player mPlayer;
    Intent intent;
    SpotifyClient spotifyClient;
    ArrayList<Song> spotifyResults;
    String spotifyAccessToken;
    String googleAccessToken;
    ArrayList<Song> googleResults;
    GooglePlayClient googlePlayClient;
    SearchFragment searchFragment;
    ViewPager vpPager;
    private FragmentPagerAdapter adapterViewPager;


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
        googleResults = new ArrayList<>();

        //super.onCreate(savedInstanceState);
        //at top
        setContentView(R.layout.activity_main);

        //get the view pager
        vpPager = (ViewPager) findViewById(R.id.viewpager);
        adapterViewPager = new SongPagerAdapter(getSupportFragmentManager(), this);
        //set the adapter for the pager
        vpPager.setAdapter(adapterViewPager);

//        mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
//        intent=getIntent();
//        mPlayer = Parcels.unwrap(intent.getParcelableExtra(SPOTIFY_PLAYER));
//        mPlayer.playUri(null, "spotify:track:2TpxZ7JUBn3uw46aR7qd6V", 0, 0);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);


        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                SearchFragment searchFragment= (SearchFragment) ((SongPagerAdapter) vpPager.getAdapter()).mFragmentReferences.get(0);
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
        mPlayer = Spotify.getPlayer(playerConfig, this, null);
    }
//    public void addToTextBox(){
//        TextView tvSongs = (TextView) findViewById(R.id.tvSongs);
//        String songs = "";
//        for (int i =0 ;i < spotifyResults.size(); i++){
//            songs += (spotifyResults.get(i).title + ", ");
//        }
//        songs+="GOOGLE PLAY \n\n";
//        for (int i =0 ;i < googleResults.size(); i++){
//            songs += (googleResults.get(i).title + ", ");
//        }
//        tvSongs.setText(songs);
//    }



}
