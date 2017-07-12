package com.ruppal.orbz.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by ruppal on 7/12/17.
 */

public class Song {
    public String title;
    public ArrayList<Artist> artists;
    public String albumCoverUrl;
    public String uid;
    public boolean playing;
    public String album;
    public int popularity;
    public int duration_ms;

    public static final String SPOTIFY = "Spotify";
    public static final String SOUNDCLOUD = "Soundcloud";
    public static final String GOOGLE_PLAY = "GooglePlay";
    public static final String YOUTUBE = "Youtube";

    public static Song fromJSON(String service, JSONObject object) throws JSONException {
        switch (service){
            case SPOTIFY:
                 return parseSpotifyJSON(object);

            case SOUNDCLOUD:
                return parseSpotifyJSON(object);
//                return parseSoundcloudJSON(object);

            case GOOGLE_PLAY:
                return parseGooglePlayJSON(object);

            case YOUTUBE:
                return parseSpotifyJSON(object);
//                return parseYoutubeJSON(object);
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
//        JSONArray images = object.getJSONArray("images");
        //todo : can get a different image size based on which index is used
//        song.albumCoverUrl = images.getJSONObject(0).getString("url");

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

        song.duration_ms = object.getInt("total"); //time object
        song.playing = object.getBoolean("playing"); //
        //uid = object.getString(); //can't find id
        song.artists.add(Artist.fromJSON(GOOGLE_PLAY, object));
        return song;
        
    }

//    private Song parseYoutubeJSON(JSONObject object){
//        //call the  artist from JSON in a for loop to populate artists array
//    }


}
