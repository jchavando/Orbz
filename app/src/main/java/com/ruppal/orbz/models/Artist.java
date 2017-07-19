package com.ruppal.orbz.models;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import static com.ruppal.orbz.models.Song.GOOGLE_PLAY;
import static com.ruppal.orbz.models.Song.SPOTIFY;
import static com.ruppal.orbz.models.Song.YOUTUBE;

/**
 * Created by ruppal on 7/12/17.
 */

@Parcel
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
            case YOUTUBE:
                return parseYoutubeJSON(object);
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
    private static Artist parseYoutubeJSON(JSONObject object) throws JSONException {
        JSONObject snippet = object.getJSONObject("snippet");
        Artist artist = new Artist();
        artist.uid = snippet.getString("channelId");
        artist.name = snippet.getString("channelTitle");
        return artist;
    }


}
