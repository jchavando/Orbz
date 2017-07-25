package com.ruppal.orbz.models;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubePlayer;
import com.ruppal.orbz.R;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;

/**
 * Created by ruppal on 7/19/17.
 */

public class Player {
    public static Song currentlyPlayingSong;
    public static com.spotify.sdk.android.player.Player spotifyPlayer;
    public static YouTubePlayer youTubePlayer;
    public static com.spotify.sdk.android.player.Player.OperationCallback mOperationCallback;
    public static Activity activity; //todo dont forget to chnage this for playlist activity
    public static ImageButton playButton;
    public static ImageButton pauseButton;
    public static int grey = R.color.disable_button;
    public static int white = Color.WHITE;

    public static Activity getActivity() {
        return activity;
    }

    public static void setActivity(Activity activity) {
        Player.activity = activity;
        pauseButton = (ImageButton) activity.findViewById(R.id.exoPlayer_pause);
        playButton = (ImageButton) activity.findViewById(R.id.exoPlayer_play);
    }

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

    public static void setYouTubePlayer(YouTubePlayer player) {
        Player.youTubePlayer = player;
        youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {
                setPlayButtonColors();
            }

            @Override
            public void onPaused() {
                setPauseButtonColors();
            }

            @Override
            public void onStopped() {
                Toast.makeText(getActivity().getApplicationContext(), "There was an error playing your video", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

            }
        });

    }

    public static void stopAllSongs(){
        //stop spotify player
        if (spotifyPlayer != null) {
            spotifyPlayer.pause(new com.spotify.sdk.android.player.Player.OperationCallback() {
                @Override
                public void onSuccess() {
                    //want to stop. so dont put anything here
                }

                @Override
                public void onError(Error error) {
                    Log.e("stop music", error.toString());
                }
            });
        }
        //stop youtube player
        if (youTubePlayer != null) {
            youTubePlayer.pause();
        }
    }

    public static void playSong(Song song){
        setPlayButtonColors();
        stopAllSongs();
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

    public static void pauseSong(Song song){
        if (pauseButton!= null && playButton!=null) {
            setPauseButtonColors();
        }
        switch (song.getService()){
            case Song.SPOTIFY:
                pauseSongFromSpotify(song);
                break;
            case Song.YOUTUBE:
                pauseSongFromYoutube(song);
            default:
                break;
        }

    }

    public static void unPauseSong(Song song){
        setPlayButtonColors();
        switch (song.getService()){
            case Song.SPOTIFY:
                unPauseSongFromSpotify(song);
                break;
            case Song.YOUTUBE:
                unPauseSongFromYoutube(song);
            default:
                break;
        }
    }

    private static void unPauseSongFromYoutube(Song song){
        if (youTubePlayer != null){
            youTubePlayer.play();
        }
        else{
            Log.e("youtube player", "youtube player null");
        }
    }


    private static void pauseSongFromYoutube (Song song){
        if (youTubePlayer != null){
            song.playing = false;
            youTubePlayer.pause();
        }
        else{
            Log.e("pause from youtube", "youtube player null");
        }
    }


    private static void unPauseSongFromSpotify(Song song){
        if (mOperationCallback!=null && spotifyPlayer!=null){
            PlaybackState mCurrentPlaybackState = spotifyPlayer.getPlaybackState();
            if (mCurrentPlaybackState!=null && !mCurrentPlaybackState.isPlaying){
                spotifyPlayer.resume(mOperationCallback);
            }
        }
    }

    private static void pauseSongFromSpotify(final Song song){
        mOperationCallback = new com.spotify.sdk.android.player.Player.OperationCallback() {
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
//            Drawable playButton = context.getResources().getDrawable(R.drawable.exo_controls_play);
//            ((ImageView) view).setImageDrawable(playButton);
            spotifyPlayer.pause(mOperationCallback);
        }
    }



    public static void setPlayButtonColors(){
        playButton.setColorFilter(grey); // Grey Tint
        pauseButton.setColorFilter(white); // White Tint
    }

    public static void setPauseButtonColors(){
        pauseButton.setColorFilter(grey);
        playButton.setColorFilter(white); // White Tint
    }
}