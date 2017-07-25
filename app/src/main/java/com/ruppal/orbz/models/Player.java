package com.ruppal.orbz.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.youtube.player.YouTubePlayer;
import com.ruppal.orbz.R;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.PlayerEvent;
import java.util.ArrayList;

//import java.lang.Enum<PlayerNotificationCallback.EventType>;

/**
 * Created by ruppal on 7/19/17.
 */

public class Player {
    //Player.EVENT_CHANGE;
    public static Song currentlyPlayingSong;
    public static com.spotify.sdk.android.player.Player spotifyPlayer;
    public static YouTubePlayer youTubePlayer;
    public static com.spotify.sdk.android.player.Player.OperationCallback spotifyCallback;
    public static ArrayList<Song> queue = new ArrayList<>();

   //public static final PlayerNotificationCallback.EventType TRACK_END;
    public static PlayerEvent kSpPlaybackNotifyMetadataChanged;
    public static Song getCurrentlyPlayingSong() {
        return currentlyPlayingSong;
    }

    public static void setCurrentlyPlayingSong(Song currentlyPlayingSong) {
        Player.currentlyPlayingSong = currentlyPlayingSong;
    }

    public static com.spotify.sdk.android.player.Player getSpotifyPlayer() {
        return spotifyPlayer;
    }

    public static void setSpotifyPlayer(com.spotify.sdk.android.player.Player spotifyPlayer) {
        Player.spotifyPlayer = spotifyPlayer;
    }

    public static YouTubePlayer getYouTubePlayer() {
        return youTubePlayer;
    }

    public static void setYouTubePlayer(YouTubePlayer youTubePlayer) {
        Player.youTubePlayer = youTubePlayer;
    }

    public static void playSong(Song song){
        switch (song.getService()){
            case Song.SPOTIFY:
                if (spotifyPlayer != null) {
                    playSongFromSpotify(song);
                }
                else{
                    Log.e("player", "spotify player not initialized");
                }
                break;

            case Song.YOUTUBE:
                if (youTubePlayer!=null) {
                    playSongFromYoutube(song);
                }
                else{
                    Log.e("player", "youtube player not initialized");
                }
                break;
            case Song.LOCAL:
               // TODO -- code for local
                break;
            default:
                break;
        }
    }

    private static void playSongFromSpotify(Song song){
        spotifyPlayer.playUri(null, "spotify:track:" + song.getUid() , 0, 0);
        song.playing = true;
        currentlyPlayingSong = song;
    }

    private static void playSongFromYoutube(Song song){
        if (youTubePlayer != null){
            youTubePlayer.loadVideo(song.getUid());
            youTubePlayer.play();
            song.playing=true;
            currentlyPlayingSong = song;
        }
        else{
            Log.e("player", "failed to play song from youtube");
        }
    }

    public static void pauseSong(Song song, Context context, View view){
        switch (song.getService()){
            case Song.SPOTIFY:
                pauseSongFromSpotify(song, context, view);
                break;
            default:
                break;
        }

    }

    private static void pauseSongFromSpotify(final Song song, Context context, View view){
        com.spotify.sdk.android.player.Player.OperationCallback mOperationCallback = new com.spotify.sdk.android.player.Player.OperationCallback() {
            @Override
            public void onSuccess() {
                song.playing = false;
            }

            @Override
            public void onError(Error error) {
                Log.e("play", error.toString());
            }
        };
        PlaybackState mCurrentPlaybackState = spotifyPlayer.getPlaybackState();
        if (mCurrentPlaybackState != null && mCurrentPlaybackState.isPlaying) {
            Drawable playButton = context.getResources().getDrawable(R.drawable.exo_controls_play);
            ((ImageView) view).setImageDrawable(playButton);
            spotifyPlayer.pause(mOperationCallback);
        } else {
            Drawable pauseButton = context.getResources().getDrawable(R.drawable.exo_controls_pause);
            ((ImageView) view).setImageDrawable(pauseButton);
            spotifyPlayer.resume(mOperationCallback);
        }
    }

}