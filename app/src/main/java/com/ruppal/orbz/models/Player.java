package com.ruppal.orbz.models;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

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

/**
 * Created by ruppal on 7/19/17.
 */

public class Player {

    public static Song currentlyPlayingSong;
    public static com.spotify.sdk.android.player.Player spotifyPlayer;
    public static YouTubePlayer youTubePlayer;
    public static com.spotify.sdk.android.player.Player.OperationCallback spotifyCallback;

    public static SimpleExoPlayer exoPlayer;
    public static ComponentListener componentListener;

    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "PlayerActivity";

    public static long playbackPosition;
    public static int currentWindow;
    public static boolean playWhenReady = true;

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
                if (exoPlayer != null) {
                    prepareExoPlayerFromFileUri(song.getSongUri());
                } else { Log.e("player", "local player not initialized");}
                break;
            default:
                break;
        }
    }

    public static void pauseSong(Song song, Context context, View view){
        switch (song.getService()){
            case Song.SPOTIFY:
                pauseSongFromSpotify(song, context, view);
                break;
            case Song.LOCAL:
                releasePlayer();
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

    private static void playSongFromSpotify(Song song){
        spotifyPlayer.playUri(null, "spotify:track:" + song.getUid() , 0, 0);
        song.playing = true;
        currentlyPlayingSong = song;
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
    public void startPlayer() {exoPlayer.setPlayWhenReady(true);}

    public void pause() {exoPlayer.setPlayWhenReady(false);}

    public int getCurrentPosition() {return (int) exoPlayer.getCurrentPosition();}

    public static Song getCurrentlyPlayingSong() {
        return currentlyPlayingSong;
    }

    public static void setCurrentlyPlayingSong(Song currentlyPlayingSong) { Player.currentlyPlayingSong = currentlyPlayingSong;}

    public static com.spotify.sdk.android.player.Player getSpotifyPlayer() {
        return spotifyPlayer;
    }

    public static void setSpotifyPlayer(com.spotify.sdk.android.player.Player spotifyPlayer) {Player.spotifyPlayer = spotifyPlayer;}

    public static YouTubePlayer getYouTubePlayer() {
        return youTubePlayer;
    }

    public static void setYouTubePlayer(YouTubePlayer youTubePlayer) {Player.youTubePlayer = youTubePlayer;}

    public static void setComponentListener(ComponentListener componentListener){Player.componentListener = componentListener;}

}