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
                break;
            case SOUNDCLOUD:
                return parseSoundcloudJSON(object);
                break;
            case GOOGLE_PLAY:
                return parseGooglePlayJSON(object);
                break;
            case YOUTUBE:
                return parseYoutubeJSON(object);
                break;
        }
    }

    private Song parseSpotifyJSON(JSONObject object){
        //call the  artist from JSON in a for loop to populate artists array
        //for example, Artist.fromJSON(SPOTIFY, object);

    }
    private Song parseSoundcloudJSON(JSONObject object){
        //call the  artist from JSON in a for loop to populate artists array

    }
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




    }
    private Song parseYoutubeJSON(JSONObject object){
        //call the  artist from JSON in a for loop to populate artists array
    }


}
