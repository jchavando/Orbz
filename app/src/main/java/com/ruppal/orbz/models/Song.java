package com.ruppal.orbz.models;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;

/**
 * Created by ruppal on 7/12/17.
 */

@Parcel
public class Song {
    public String title;
    public ArrayList<Artist> artists;
    public String albumCoverUrl;
    public String uid;
    public boolean playing;
    public String album;
    public int popularity;
    public String service;
    public int duration_ms;

    public String data;
    public String artist;

    public long SongID; // different from uid

    public static final String SPOTIFY = "Spotify";
    public static final String SOUNDCLOUD = "Soundcloud";
    public static final String GOOGLE_PLAY = "GooglePlay";
    public static final String YOUTUBE = "Youtube";
    public static final String LOCAL = "Local";
    public static final String LASTFM = "Last.fm";

    public Song(){}

    public Song(long id, String title){
        SongID = id;
        this.title = title;
    }

    public Song(long id, String title, String artist, String data){
        SongID = id;
        this.title = title;
        this.artist = artist;
        this.data = data;
        service = "Local";
    }

    public static Song fromJSON(String service, JSONObject object) throws JSONException {
        switch (service){
            case SPOTIFY:
                 return parseSpotifyJSON(object);
                //break;
//            case SOUNDCLOUD:
//                return parseSoundcloudJSON(object);
//                break;
            case GOOGLE_PLAY:
                return parseGooglePlayJSON(object);
                //break;

            case LASTFM:
                return parseLastFMJSON(object);
            //case YOUTUBE:
               // return parseYoutubeJSON(object);

            case YOUTUBE:
                return parseYoutubeJSON(object);

                //break;
            default:
                return null;

        }
    }



    private static Song parseSpotifyJSON(JSONObject object) throws JSONException{
        //REQUIRES: object is an entry from items, which is inside tracks JSON object
        Song song = new Song ();

        JSONObject albumObj = object.getJSONObject("album");
        song.album = albumObj.getString("name");
        song.uid = object.getString("id");
        song.title = object.getString("name");
        song.popularity = object.getInt("popularity");
        song.duration_ms = object.getInt("duration_ms");
        song.playing = false;
        song.service = SPOTIFY;
        JSONArray images = albumObj.getJSONArray("images");
//        //todo : can get a different image size based on which index is used
        int sizeImages = images.length();
        song.albumCoverUrl = images.getJSONObject(sizeImages - 1).getString("url");

        //call the  artist from JSON in a for loop to populate artists array
        //for example, Artist.fromJSON(SPOTIFY, object);
        JSONArray mArtists = object.getJSONArray("artists");
        song.artists = new ArrayList<>();
        for (int i = 0; i < mArtists.length(); i++){
            Artist artist = Artist.fromJSON(SPOTIFY, mArtists.getJSONObject(i));
            song.artists.add(artist);
        }

        return song;

    }
//    private Song parseSoundcloudJSON(JSONObject object){
//        //call the  artist from JSON in a for loop to populate artists array
//
//    }
    private static Song parseGooglePlayJSON(JSONObject object) throws JSONException {
        Song song = new Song();
        //call the  artist from JSON in a for loop to populate artists array
        song.title = object.getString("title");
        song.albumCoverUrl = object.getString("albumArt");
        song.album = object.getString("album");
        //popularity = object.getInt("");
        song.service = GOOGLE_PLAY;
        song.duration_ms = object.getInt("total"); //time object
        song.playing = object.getBoolean("playing"); //
        //uid = object.getString(); //can't find id
        song.artists.add(Artist.fromJSON(GOOGLE_PLAY, object));

        return song;

    }

    //TODO
    private static Song parseLastFMJSON(JSONObject object) throws JSONException {
        Song song = new Song();
        song.title = object.getString("track");
        song.uid = object.getString("mid");
        song.artists.add(Artist.fromJSON(LASTFM, object)); //"artist"

        return song;

    }

        private static Song parseYoutubeJSON(JSONObject object) throws JSONException {
        //call the  artist from JSON in a for loop to populate artists array
        Song song = new Song();


        JSONObject snippet = object.getJSONObject("snippet");
            song.title = snippet.getString("title");
        song.uid = object.getJSONObject("id").getString("videoId");
        song.service = YOUTUBE;
        song.albumCoverUrl = snippet.getJSONObject("thumbnails").getJSONObject("default").getString("url");
        //todo - find a better way to get artist
        song.artists = new ArrayList<>();
        song.artists.add(Artist.fromJSON(YOUTUBE, object));

        return song;
    }

//    private Song parseYoutubeJSON(JSONObject object){
//        //call the  artist from JSON in a for loop to populate artists array
//    }

    public String getService() {
        return service;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<Artist> getArtists() {
        return artists;
    }

    public String getAlbumCoverUrl() {
        return albumCoverUrl;
    }

    public String getUid() {
        return uid;
    }

    public boolean isPlaying() {
        return playing;
    }

    public String getAlbum() {
        return album;
    }

    public int getPopularity() {
        return popularity;
    }

    public int getDuration_ms() {
        return duration_ms;
    }


    public long getSongID(){
        return SongID;
    }

    public Uri getSongUri() {
        return Uri.parse("file:///"+ getData());
    }

    public String getData() {
        return data;
    }

    public String getArtist() {
        return artist;
    }
}
