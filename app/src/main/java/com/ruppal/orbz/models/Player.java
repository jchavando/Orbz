package com.ruppal.orbz.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageButton;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
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

    public static com.spotify.sdk.android.player.Player.OperationCallback mOperationCallback;
    public static Activity activity; //todo dont forget to chnage this for playlist activity
    public static ImageButton playButton;
    public static ImageButton pauseButton;
    public static int grey = R.color.disable_button;
    public static int white = Color.WHITE;
    //Player.EVENT_CHANGE;
    public static Song currentlyPlayingSong;
    public static com.spotify.sdk.android.player.Player spotifyPlayer;
    public static YouTubePlayer youTubePlayer;
    public static com.spotify.sdk.android.player.Player.OperationCallback spotifyCallback;
    public static ArrayList<Song> queue = new ArrayList<>();
    //public static final PlayerNotificationCallback.EventType TRACK_END;
    public static PlayerEvent kSpPlaybackNotifyMetadataChanged;

    public static ArrayList<Song> queueRemoved = new ArrayList<>(); //act like a stack
    public static int positionInQueue=0;
    public static SimpleExoPlayer exoPlayer;
    public static ComponentListener componentListener;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "PlayerActivity";

    public static long playbackPosition;
    public static int currentWindow;
    public static boolean playWhenReady = true;

    public static Activity getActivity() {
        return activity;


//        PlayerEvent kSpPlaybackNotifyMetadataChanged = PlayerEvent.kSpPlaybackNotifyMetadataChanged;
    }

    public static void setActivity(Activity activity) {
        Player.activity = activity;
        pauseButton = (ImageButton) activity.findViewById(R.id.exoPlayer_pause);
        playButton = (ImageButton) activity.findViewById(R.id.exoPlayer_play);
    }

    /*
    
    while(true ) {
        //go to next song in queue
        //position += 1;
        if (song.duration_ms == spotifyPlayer.getPlaybackState().positionMs) break;
        else if (skipped song in spotify) break;
    }

     */

    public static void setSpotifyPlayer(com.spotify.sdk.android.player.Player spotifyPlayer) {
        Player.spotifyPlayer = spotifyPlayer;
        spotifyPlayer.getMetadata();

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

                //Toast.makeText(getActivity().getApplicationContext(), "There was an error playing your video", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBuffering(boolean b) {

            }

            @Override
            public void onSeekTo(int i) {

            }
        });

        youTubePlayer.setPlayerStateChangeListener(new YouTubePlayer.PlayerStateChangeListener() {
            @Override
            public void onLoading() {

            }

            @Override
            public void onLoaded(String s) {

            }

            @Override
            public void onAdStarted() {

            }

            @Override
            public void onVideoStarted() {

            }

            @Override
            public void onVideoEnded() {
                skipToNextInQueue();

            }

            @Override
            public void onError(YouTubePlayer.ErrorReason errorReason) {

            }
        });

    }


    public static void playNextSongInQueue() {
        if (queue.size()>0){

            playSong(queue.get(positionInQueue));
            if (positionInQueue != queue.size()-1) {
                positionInQueue+=1;
            }
            //queueRemoved.add(0, queue.get(0));
            //queue.remove(0);
        }
    }

    public static void skipToNextInQueue(){

        playNextSongInQueue();

    }

    //TODO on click
    public static void skipToPreviousInQueue(){
        if (queue.size() > 0) {
            //positionInQueueRemoved = positionInQueueRemoved +1;
            if (positionInQueue > 0) {
                positionInQueue -= 1;
                playSong(queue.get(positionInQueue));
            }
        }
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
                if (exoPlayer != null) {
                    prepareExoPlayerFromFileUri(song.getSongUri());
                } else { Log.e("player", "local player not initialized");}
                break;
            default:
                break;
        }
    }




    public static void initializePlayer(Context context) {
        if (exoPlayer == null) {
            // a factory to create an AdaptiveVideoTrackSelection
            TrackSelection.Factory adaptiveTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(BANDWIDTH_METER);
            // using a DefaultTrackSelector with an adaptive video selection factory
            exoPlayer = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(context),
                    new DefaultTrackSelector(adaptiveTrackSelectionFactory), new DefaultLoadControl());
            exoPlayer.addListener(componentListener);
            exoPlayer.setVideoDebugListener(componentListener);
            exoPlayer.setAudioDebugListener(componentListener);
            exoPlayer.setPlayWhenReady(playWhenReady);
            exoPlayer.seekTo(currentWindow, playbackPosition);
        }
    }

    public static void prepareExoPlayerFromFileUri(Uri uri){

        DataSpec dataSpec = new DataSpec(uri);
        final FileDataSource fileDataSource = new FileDataSource();
        try {
            fileDataSource.open(dataSpec);
        } catch (FileDataSource.FileDataSourceException e) {
            e.printStackTrace();
        }

        DataSource.Factory factory = new DataSource.Factory() {
            @Override
            public DataSource createDataSource() {
                return fileDataSource;
            }
        };
        MediaSource audioSource = new ExtractorMediaSource(fileDataSource.getUri(),
                factory, new DefaultExtractorsFactory(), null, null);

        exoPlayer.prepare(audioSource);
    }

    public static MediaSource buildMediaSource(Uri uri) {
        com.google.android.exoplayer2.upstream.DataSource.Factory dataSourceFactory = new DefaultHttpDataSourceFactory("ua", BANDWIDTH_METER);
        DashChunkSource.Factory dashChunkSourceFactory = new DefaultDashChunkSource.Factory(
                dataSourceFactory);
        return new DashMediaSource(uri, dataSourceFactory, dashChunkSourceFactory, null, null);
    }

    public static void releasePlayer() {
        if (exoPlayer != null) {
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            playWhenReady = exoPlayer.getPlayWhenReady();
            exoPlayer.removeListener(componentListener);
            exoPlayer.setVideoListener(null);
            exoPlayer.setVideoDebugListener(null);
            exoPlayer.setAudioDebugListener(null);
            exoPlayer.release();
            exoPlayer = null;
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

    public static Song getCurrentlyPlayingSong() {
        return currentlyPlayingSong;
    }

    public static void setCurrentlyPlayingSong(Song currentlyPlayingSong) { Player.currentlyPlayingSong = currentlyPlayingSong;}

    public static com.spotify.sdk.android.player.Player getSpotifyPlayer() {
        return spotifyPlayer;
    }



    public static void setComponentListener(ComponentListener componentListener){Player.componentListener = componentListener;}



    public static void setPlayButtonColors(){
        playButton.setColorFilter(grey); // Grey Tint
        pauseButton.setColorFilter(white); // White Tint
    }

    public static void setPauseButtonColors(){
        pauseButton.setColorFilter(grey);
        playButton.setColorFilter(white); // White Tint
    }




}