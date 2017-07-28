package com.ruppal.orbz.models;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.dash.DashChunkSource;
import com.google.android.exoplayer2.source.dash.DashMediaSource;
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DataSpec;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSource;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerSupportFragment;
import com.ruppal.orbz.R;
import com.ruppal.orbz.fragments.SongListFragment;
import com.spotify.sdk.android.player.Error;
import com.spotify.sdk.android.player.PlaybackState;
import com.spotify.sdk.android.player.PlayerEvent;

import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.ruppal.orbz.R.id.youtube_fragment;

//import java.lang.Enum<PlayerNotificationCallback.EventType>;

/**
 * Created by ruppal on 7/19/17.
 */

public class Player {

    public static com.spotify.sdk.android.player.Player.OperationCallback mOperationCallback;
    public static Activity activity; //todo dont forget to chnage this for playlist activity
    public static ImageButton playButton;
    public static ImageButton pauseButton;
    public static SeekBar sbSongProgress;
    public static TextView tvTimeElapsed;
    public static TextView tvTimeRemaining;
    public static int grey = R.color.disable_button;
    public static int white = Color.WHITE;
    public static ScheduledExecutorService executor;
    public static Song currentlyPlayingSong;
    public static com.spotify.sdk.android.player.Player spotifyPlayer;
    public static YouTubePlayer youTubePlayer;
    public static com.spotify.sdk.android.player.Player.OperationCallback spotifyCallback;
    public static ArrayList<Song> queue = new ArrayList<>();
    public static PlayerEvent kSpPlaybackNotifyMetadataChanged;
    public static ArrayList<Song> queueRemoved = new ArrayList<>(); //act like a stack
    public static int positionInQueue= -1 ;
    public static SimpleExoPlayer exoPlayer;
    public static ComponentListener componentListener;
    public static Animation animationMoveHorizontal;
    private static final DefaultBandwidthMeter BANDWIDTH_METER = new DefaultBandwidthMeter();
    private static final String TAG = "PlayerActivity";
    public static Handler handler;
    public static long playbackPosition;
    public static int currentWindow;
    public static boolean playWhenReady = true;
    public static ImageView ivAlbumCover;
    public static YouTubePlayerSupportFragment youtubePlayerFragment;
    public static FrameLayout frameLayout;
    public static FragmentTransaction fragmentTransaction;
    public static TextView tvSongInfo;

    public static Activity getActivity() {
        return activity;


//        PlayerEvent kSpPlaybackNotifyMetadataChanged = PlayerEvent.kSpPlaybackNotifyMetadataChanged;
    }

    public static void setActivity(Activity activity) {
        Player.activity = activity;
        handler = new Handler();
        pauseButton = (ImageButton) activity.findViewById(R.id.exoPlayer_pause);
        playButton = (ImageButton) activity.findViewById(R.id.exoPlayer_play);
        ivAlbumCover = (ImageView) activity.findViewById(R.id.ivAlbumCoverPlayer);
        frameLayout = (FrameLayout) activity.findViewById(youtube_fragment);
        sbSongProgress = (SeekBar) activity.findViewById(R.id.sbSongProgress);
        tvTimeElapsed = (TextView) activity.findViewById(R.id.tvTimeElapsed);
        tvTimeRemaining = (TextView) activity.findViewById(R.id.tvTimeRemaining);
        tvSongInfo = (TextView) activity.findViewById(R.id.tvSongInfo);
        animationMoveHorizontal = AnimationUtils.loadAnimation(activity.getApplicationContext(),
                R.anim.move_horizontal);
        sbSongProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser && currentlyPlayingSong!=null) {
                    playCurrentSongFrom(progress);
                }
                if (currentlyPlayingSong!=null && currentlyPlayingSong.getService() == Song.SPOTIFY && almostEquals(progress, sbSongProgress.getMax())){
                    playNextSongInQueue();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private static boolean almostEquals(int j, int i){
        int oneSecond = 1000;
        return Math.abs(i-j)<oneSecond;
    }


    public static void setSpotifyPlayer(com.spotify.sdk.android.player.Player spotifyPlayer) {
        Player.spotifyPlayer = spotifyPlayer;
        spotifyPlayer.getMetadata();

    }

    public static YouTubePlayer getYouTubePlayer() {
        return youTubePlayer;
    }

    public static Runnable updateSeekBar (){
        switch (currentlyPlayingSong.getService()){
            case Song.SPOTIFY:
                return updateSeekBarSpotify;
            case Song.YOUTUBE:
                return updateSeekBarYoutube;
            case Song.LOCAL:
                return updateSeekBarLocal;
            default:
                return null;
        }
    }

    public static Runnable elapsedTimeRunnable (final int currentTime, final int duration){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                int second = (currentTime / 1000) % 60;
                int minute = (currentTime/ (1000 * 60)) % 60;
                int totalTimeRemaining = duration - currentTime;
                int secondRemaining = (totalTimeRemaining / 1000) % 60;
                int minuteRemaining = (totalTimeRemaining/ (1000 * 60)) % 60;
                //so 1 shows up as 01
                String secondString = (second < 10 ? "0" : "") + second;
                String secondRemainingString = (secondRemaining < 10 ? "0" : "") + secondRemaining;
                String timeElapsed = minute + ":" + secondString;
                String timeRemaining = minuteRemaining +":" + secondRemainingString;
                tvTimeElapsed.setText(timeElapsed);
                tvTimeRemaining.setText(timeRemaining);
            }
        };
        return runnable;
    }

    public static Runnable updateSeekBarSpotify = new Runnable() {
        @Override
        public void run() {
            if (spotifyPlayer!=null){
                final int currentTime = (int) spotifyPlayer.getPlaybackState().positionMs;
                sbSongProgress.setProgress(currentTime);
                handler.post(elapsedTimeRunnable(currentTime, currentlyPlayingSong.getDuration_ms()));
            }
        }
    };

    public static Runnable updateSeekBarLocal = new Runnable() {
        @Override
        public void run() {
            if (exoPlayer != null){
                final int currentTime = (int) exoPlayer.getCurrentPosition();
                sbSongProgress.setProgress(currentTime);
                handler.post(elapsedTimeRunnable(currentTime, (int) exoPlayer.getDuration()));

            }
        }
    };


    public static Runnable updateSeekBarYoutube = new Runnable() {
        @Override
        public void run() {
            if (youTubePlayer != null) {
                final int currentTime = youTubePlayer.getCurrentTimeMillis();
                sbSongProgress.setProgress(currentTime);
                handler.post(elapsedTimeRunnable(currentTime, youTubePlayer.getDurationMillis()));
            }
        }
    };



    public static void setYouTubePlayer(YouTubePlayer player) {
        Player.youTubePlayer = player;
        youTubePlayer.setPlaybackEventListener(new YouTubePlayer.PlaybackEventListener() {
            @Override
            public void onPlaying() {
                setPlayButtonColors();
                int duration = youTubePlayer.getDurationMillis();
                sbSongProgress.setMax(duration);
            }

            @Override
            public void onPaused() {
                setPauseButtonColors();
            }

            @Override
            public void onStopped() {

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
            if (positionInQueue != queue.size()-1) {
                positionInQueue+=1;
            }
            playSong(queue.get(positionInQueue));

        }
    }

    public static void skipToNextInQueue(){
        playNextSongInQueue();
    }

    //TODO on click
    public static void skipToPreviousInQueue(){
        if (queue.size() > 0) {
            if (positionInQueue > 0) {
                positionInQueue -= 1;
                playSong(queue.get(positionInQueue));
            }
        }
    }

    public static void stopAllSongs(){
        //stop spotify player
        handler.removeCallbacks(elapsedTimeRunnable(0,0));
        if (executor!=null) {
            executor.shutdown();
        }
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
        if (exoPlayer!=null){
            exoPlayer.setPlayWhenReady(false);
        }
    }

    public static void playCurrentSongFrom (int position){
        switch (currentlyPlayingSong.getService()){
            case Song.SPOTIFY:
                spotifyPlayer.playUri(null, "spotify:track:" + currentlyPlayingSong.getUid(), 0, position);
                break;
            case Song.YOUTUBE:
                if (youTubePlayer!=null) {
                    youTubePlayer.seekToMillis(position);
                }
                break;
            case Song.LOCAL:
                exoPlayer.seekTo(position);
            default:
                break;
        }
    }

    public static void updateAlbumCover(){
        if (ivAlbumCover!=null && currentlyPlayingSong.getService() != Song.YOUTUBE){
            ivAlbumCover.bringToFront();
            Glide.with(activity.getApplicationContext())
                    .load(currentlyPlayingSong.getAlbumCoverUrl())
                    .into(ivAlbumCover);
        }
    }

    public static void updateSongInfo(){
        if (tvSongInfo!=null && currentlyPlayingSong!=null) {
            String songInfo = "";
            String artistList = "";
            if (currentlyPlayingSong.getTitle() != null) {
                songInfo += currentlyPlayingSong.getTitle();
            }
            if (currentlyPlayingSong.getArtists() != null) {
                int sizeArtists = currentlyPlayingSong.getArtists().size();
                for (int i = 0; i < sizeArtists; i++) {
                    artistList = artistList + currentlyPlayingSong.getArtists().get(i).name;
                    if (i < sizeArtists - 1) {
                        artistList += ", ";
                    }
                }
                songInfo += " · " + artistList;
            }
            tvSongInfo.setText(songInfo);
            tvSongInfo.setSelected(true);
            tvSongInfo.startAnimation(animationMoveHorizontal);
        }
    }

    public static void playSong(Song song){
        if (song!=null) {
            currentlyPlayingSong = song;
            setPlayButtonColors();
            stopAllSongs();
            updateSongInfo();
            switch (song.getService()) {
                case Song.SPOTIFY:
                    if (spotifyPlayer != null) {
                        playSongFromSpotify(song);
                        int duration = song.getDuration_ms();
                        sbSongProgress.setMax(duration);
                        updateAlbumCover();
                    } else {
                        Log.e("player", "spotify player not initialized");
                    }
                    break;

                case Song.YOUTUBE:
                    frameLayout.bringToFront();
                    initializeYoutubePlayerFragment(song); //calls play song from youtube
//                    playSongFromYoutube(song);
                    break;
                case Song.LOCAL:
                    if (exoPlayer != null) {
                        exoPlayer.setPlayWhenReady(true);
                        prepareExoPlayerFromFileUri(song.getSongUri());
                        updateAlbumCover();
//                    int duration = (int) exoPlayer.getDuration(); //todo make sure this cast is safe
//                    sbSongProgress.setMax(duration);

                    } else {
                        Log.e("player", "local player not initialized");
                    }
                    break;
                default:
                    break;
            }
            executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(updateSeekBar(), 0, 1, TimeUnit.SECONDS);
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
            exoPlayer.addListener(new ExoPlayer.EventListener() {
                @Override
                public void onTimelineChanged(Timeline timeline, Object manifest) {

                }

                @Override
                public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

                }

                @Override
                public void onLoadingChanged(boolean isLoading) {

                }

                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    if (playbackState == ExoPlayer.STATE_READY){
                        int duration = (int) exoPlayer.getDuration();
                        sbSongProgress.setMax(duration);
                    }
                    if (playbackState == ExoPlayer.STATE_ENDED){
                        playNextSongInQueue();
                    }

                }

                @Override
                public void onPlayerError(ExoPlaybackException error) {

                }

                @Override
                public void onPositionDiscontinuity() {

                }

                @Override
                public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

                }
            });
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
    }

    private static void playSongFromYoutube(Song song){
        if (youTubePlayer != null){
            youTubePlayer.loadVideo(song.getUid());
            youTubePlayer.play();
            song.playing=true;
        }
        else{
            Log.e("player", "failed to play song from youtube");
        }
    }

    public static void pauseSong(Song song){
        if (song!=null) {
            if (executor != null) {
                executor.shutdown();
            }
            if (pauseButton != null && playButton != null) {
                setPauseButtonColors();
            }
            switch (song.getService()) {
                case Song.SPOTIFY:
                    pauseSongFromSpotify(song);
                    break;
                case Song.YOUTUBE:
                    pauseSongFromYoutube(song);
                    break;
                case Song.LOCAL:
                    setPlayback(false);
                default:
                    break;
            }
        }
    }

    public static void unPauseSong(Song song){
        if (song!=null) {
            setPlayButtonColors();
            switch (song.getService()) {
                case Song.SPOTIFY:
                    unPauseSongFromSpotify(song);
                    break;
                case Song.YOUTUBE:
                    unPauseSongFromYoutube(song);
                    break;
                case Song.LOCAL:
                    setPlayback(true);
                    break;
                default:
                    break;
            }
            executor = Executors.newScheduledThreadPool(1);
            executor.scheduleAtFixedRate(updateSeekBar(), 0, 1, TimeUnit.SECONDS);
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

    public static void setPlayback (boolean state){
        if(state){setPlayButtonColors();}
        else {setPauseButtonColors();}
        exoPlayer.setPlayWhenReady(state);
    }


    public static void initializeYoutubePlayerFragment(final Song song){
        youtubePlayerFragment = new YouTubePlayerSupportFragment();
        fragmentTransaction = SongListFragment.fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.youtube_fragment, youtubePlayerFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        youtubePlayerFragment.initialize(activity.getString(R.string.googlePlay_client_id), new YouTubePlayer.OnInitializedListener() {
            @Override
            public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youTubePlayer, boolean b) {
                com.ruppal.orbz.models.Player.setYouTubePlayer(youTubePlayer);
                playSongFromYoutube(song);
            }

            @Override
            public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
                Toast.makeText(activity.getApplicationContext(), "Failed to initalize the youtube player", Toast.LENGTH_LONG).show();
            }
        });
    }


}