package com.ruppal.orbz.models;

import org.json.JSONException;
import org.json.JSONObject;

import static com.ruppal.orbz.models.Song.GOOGLE_PLAY;
import static com.ruppal.orbz.models.Song.LASTFM;
import static com.ruppal.orbz.models.Song.SPOTIFY;

/**
 * Created by ruppal on 7/12/17.
 */

public class Artist {
    public String uid;
    public String name;


    public static Artist fromJSON(String service, JSONObject object) throws JSONException {

        switch (service) {
            case SPOTIFY:
                return parseSpotifyJSON(object);
            //case SOUNDCLOUD:
                //return parseSoundcloudJSON(object);
            //break;
            case GOOGLE_PLAY:
                return parseGooglePlayJSON(object);
            case LASTFM:
                return parseLastFMJSON(object);
            //break;
            default:
                return null;
        }


    }


    private static Artist parseSpotifyJSON(JSONObject object) throws JSONException{
        //REQUIRES: being passed in a spotify artist object
        Artist artist = new Artist();
        artist.uid = object.getString("id");
        artist.name = object.getString("name");
        return artist;
    }

//    private Song parseSoundcloudJSON(JSONObject object){
//
//    }
    private static Artist parseGooglePlayJSON(JSONObject object) throws JSONException {
        Artist artist = new Artist();
        artist.uid = null;
        artist.name = object.getString("artist");
        return artist;
    }
    //TODO
    private static Artist parseLastFMJSON(JSONObject object) throws JSONException {
        Artist artist = new Artist();
        artist.uid = object.getString("mid");
        artist.name = object.getString("artist");
        return artist;
    }
//    private Song parseYoutubeJSON(JSONObject object){
//
//    }


}
